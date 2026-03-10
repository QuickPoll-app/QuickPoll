"""Production-style ETL Pipeline for QuickPoll Analytics"""
"""
Analytics Tables Generated

analytics_poll_summary:
    Poll-level aggregation of total votes.

analytics_vote_trends:
    Daily voting activity for time-series dashboards.

analytics_user_participation:
    User-level participation metrics.
"""

import logging
from datetime import datetime

import pandas as pd
from sqlalchemy import create_engine, text

from config import DATABASE_URL


# Logging Configuration
# -----------------------------------

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s | %(levelname)s | %(message)s"
)

logger = logging.getLogger(__name__)


# Database Engine
# -----------------------------------

engine = create_engine(
    DATABASE_URL,
    pool_pre_ping=True
)


# Metadata Table Creation
# -----------------------------------

def ensure_metadata_table():

    query = """
    CREATE TABLE IF NOT EXISTS etl_pipeline_runs (
        id SERIAL PRIMARY KEY,
        pipeline_name TEXT,
        run_started_at TIMESTAMP,
        run_finished_at TIMESTAMP,
        status TEXT,
        rows_processed INTEGER,
        duration_seconds FLOAT
    )
    """

    with engine.begin() as conn:
        conn.execute(text(query))


# Pipeline Metadata Logging
# -----------------------------------

def log_pipeline_start(conn, pipeline_name):

    query = text("""
        INSERT INTO etl_pipeline_runs (
            pipeline_name,
            run_started_at,
            status
        )
        VALUES (:name, :start_time, 'running')
        RETURNING id
    """)

    result = conn.execute(query, {
        "name": pipeline_name,
        "start_time": datetime.utcnow()
    })

    return result.scalar()


def log_pipeline_finish(conn, run_id, rows_processed, status):

    query = text("""
        UPDATE etl_pipeline_runs
        SET
            run_finished_at = :end_time,
            rows_processed = :rows,
            status = :status,
            duration_seconds = EXTRACT(EPOCH FROM (:end_time - run_started_at))
        WHERE id = :run_id
    """)

    conn.execute(query, {
        "run_id": run_id,
        "end_time": datetime.utcnow(),
        "rows": rows_processed,
        "status": status
    })


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
        return conn.execute(query, {"table_name": table_name}).scalar()


def check_required_tables():

    required = ["users", "polls", "poll_options", "votes"]

    missing = [t for t in required if not table_exists(t)]

    if missing:
        logger.warning(f"Missing required tables: {missing}")
        return False

    return True


# Incremental ETL Support
# -----------------------------------

def get_last_etl_timestamp():

    if not table_exists("analytics_vote_trends"):
        return None

    query = text("""
        SELECT MAX(etl_run_at)
        FROM analytics_vote_trends
    """)

    with engine.connect() as conn:
        return conn.execute(query).scalar()


# Extract Layer
# -----------------------------------

def extract_polls(conn):

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

    df = pd.read_sql(query, conn)

    logger.info(f"Extracted {len(df)} polls")

    return df


def extract_votes(conn, last_run=None):

    logger.info("Extracting votes")

    if last_run:

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
            WHERE v.created_at > :last_run
        """)

        df = pd.read_sql(query, conn, params={"last_run": last_run})

    else:

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

        df = pd.read_sql(query, conn)

    logger.info(f"Extracted {len(df)} votes")

    return df


# Data Quality Checks
# -----------------------------------

def validate_data(df, name):

    logger.info(f"Running data quality checks for {name}")

    if df.empty:
        logger.warning(f"{name} dataset is empty")

    if df.isnull().sum().sum() > 0:
        logger.warning(f"{name} contains missing values")

    if df.duplicated().any():
        logger.warning(f"{name} contains duplicate rows")


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

    result["etl_run_at"] = datetime.utcnow()

    return result[
        ["id", "question", "creator_name", "total_votes", "created_at", "etl_run_at"]
    ]


def transform_vote_trends(votes_df):

    logger.info("Transforming vote trends")

    trends = (
        votes_df
        .assign(vote_date=votes_df["created_at"].dt.date)
        .groupby("vote_date")
        .size()
        .reset_index(name="votes_per_day")
    )

    trends["etl_run_at"] = datetime.utcnow()

    return trends


def transform_user_participation(votes_df):

    logger.info("Transforming user participation")

    participation = (
        votes_df
        .groupby("voter_name")
        .size()
        .reset_index(name="total_votes_cast")
    )

    total_votes = participation["total_votes_cast"].sum()

    participation["participation_rate"] = (
        participation["total_votes_cast"] / total_votes
    )

    participation["etl_run_at"] = datetime.utcnow()

    return participation


# Load Layer
# -----------------------------------

def load_table(conn, df, table_name):

    if df.empty:
        logger.warning(f"No data to load into {table_name}")
        return

    logger.info(f"Loading {len(df)} rows into {table_name}")

    df.to_sql(
        table_name,
        conn,
        if_exists="append",
        index=False
    )


# Pipeline Orchestration
# -----------------------------------

def run_pipeline():

    logger.info("Starting QuickPoll Analytics Pipeline")

    if not check_required_tables():
        logger.info("Skipping pipeline until backend creates tables")
        return

    ensure_metadata_table()

    rows_processed = 0

    with engine.begin() as conn:

        run_id = log_pipeline_start(conn, "quickpoll_analytics_pipeline")

        try:

            last_run = get_last_etl_timestamp()

            polls_df = extract_polls(conn)
            votes_df = extract_votes(conn, last_run)

            validate_data(polls_df, "polls")
            validate_data(votes_df, "votes")

            if votes_df.empty:
                logger.info("No new data detected")
                log_pipeline_finish(conn, run_id, 0, "success")
                return

            poll_summary = transform_poll_summary(polls_df, votes_df)
            vote_trends = transform_vote_trends(votes_df)
            participation = transform_user_participation(votes_df)

            load_table(conn, poll_summary, "analytics_poll_summary")
            load_table(conn, vote_trends, "analytics_vote_trends")
            load_table(conn, participation, "analytics_user_participation")

            rows_processed = len(votes_df)

            log_pipeline_finish(conn, run_id, rows_processed, "success")

        except Exception as e:

            logger.error(f"Pipeline failed: {e}")

            log_pipeline_finish(conn, run_id, rows_processed, "failed")

            raise

    logger.info("ETL pipeline completed successfully")

# Entry Point
# -----------------------------------

if __name__ == "__main__":
    run_pipeline()