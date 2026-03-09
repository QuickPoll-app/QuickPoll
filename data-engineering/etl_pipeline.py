"""Production-style ETL Pipeline for QuickPoll Analytics"""

import logging
import pandas as pd
from sqlalchemy import create_engine, text
from config import DATABASE_URL

# -----------------------------------
# Logging Configuration
# -----------------------------------

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)s | %(message)s"
)

logger = logging.getLogger(__name__)

# -----------------------------------
# Database Engine
# -----------------------------------

engine = create_engine(
    DATABASE_URL,
    pool_pre_ping=True
)

# -----------------------------------
# Table Dependency Check
# -----------------------------------

def table_exists(table_name):
    query = text("""
        SELECT EXISTS (
            SELECT FROM information_schema.tables
            WHERE table_name = :table_name
        )
    """)

    with engine.connect() as conn:
        result = conn.execute(query, {"table_name": table_name}).scalar()

    return result


def check_required_tables():
    required = ["users", "polls", "poll_options", "votes"]

    missing = [t for t in required if not table_exists(t)]

    if missing:
        logger.warning(f"Missing required tables: {missing}")
        return False

    return True


# -----------------------------------
# Extract Layer
# -----------------------------------

def extract_polls():
    logger.info("Extracting polls")

    query = text("""
        SELECT 
            p.id,
            p.question,
            p.status,
            p.created_at,
            p.multiple_choice,
            u.name AS creator_name
        FROM polls p
        JOIN users u ON p.creator_id = u.id
    """)

    with engine.connect() as conn:
        df = pd.read_sql(query, conn)

    logger.info(f"Extracted {len(df)} polls")

    return df


def extract_votes():
    logger.info("Extracting votes")

    query = text("""
        SELECT 
            v.id,
            v.created_at,
            po.poll_id,
            po.text AS option_text,
            u.name AS voter_name
        FROM votes v
        JOIN poll_options po ON v.poll_option_id = po.id
        JOIN users u ON v.user_id = u.id
    """)

    with engine.connect() as conn:
        df = pd.read_sql(query, conn)

    logger.info(f"Extracted {len(df)} votes")

    return df


# -----------------------------------
# Data Quality Checks
# -----------------------------------

def validate_data(df, name):

    logger.info(f"Running data quality checks for {name}")

    if df.empty:
        logger.warning(f"{name} dataset is empty")

    if df.isnull().sum().sum() > 0:
        logger.warning(f"{name} contains missing values")


# -----------------------------------
# Transform Layer
# -----------------------------------

def transform_poll_summary(polls_df, votes_df):

    logger.info("Transforming poll summary")

    vote_counts = (
        votes_df
        .groupby("poll_id")
        .size()
        .reset_index(name="total_votes")
    )

    result = polls_df.merge(
        vote_counts,
        left_on="id",
        right_on="poll_id",
        how="left"
    )

    result["total_votes"] = result["total_votes"].fillna(0).astype(int)

    return result[[
        "id",
        "question",
        "creator_name",
        "total_votes",
        "created_at"
    ]]


def transform_vote_trends(votes_df):

    logger.info("Transforming vote trends")

    trends = (
        votes_df
        .assign(vote_date=votes_df["created_at"].dt.date)
        .groupby("vote_date")
        .size()
        .reset_index(name="votes_per_day")
    )

    return trends


def transform_user_participation(votes_df):

    logger.info("Transforming user participation")

    participation = (
        votes_df
        .groupby("voter_name")
        .size()
        .reset_index(name="total_votes_cast")
    )

    return participation


# -----------------------------------
# Load Layer
# -----------------------------------

def load_table(df, table_name):

    if df.empty:
        logger.warning(f"No data to load into {table_name}")
        return

    logger.info(f"Loading {len(df)} rows into {table_name}")

    with engine.begin() as conn:
        df.to_sql(
            table_name,
            conn,
            if_exists="replace",
            index=False
        )


# -----------------------------------
# Pipeline Orchestration
# -----------------------------------

def run_pipeline():

    logger.info("Starting QuickPoll Analytics Pipeline")

    if not check_required_tables():
        logger.info("Skipping pipeline until backend creates tables")
        return

    polls_df = extract_polls()
    votes_df = extract_votes()

    validate_data(polls_df, "polls")
    validate_data(votes_df, "votes")

    poll_summary = transform_poll_summary(polls_df, votes_df)
    vote_trends = transform_vote_trends(votes_df)
    participation = transform_user_participation(votes_df)

    load_table(poll_summary, "analytics_poll_summary")
    load_table(vote_trends, "analytics_vote_trends")
    load_table(participation, "analytics_user_participation")

    logger.info("ETL pipeline completed successfully")


# -----------------------------------
# Entry Point
# -----------------------------------

if __name__ == "__main__":
    run_pipeline()