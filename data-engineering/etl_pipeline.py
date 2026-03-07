"""ETL Pipeline for QuickPoll Analytics"""

import logging
import pandas as pd
from sqlalchemy import create_engine, text
from config import DATABASE_URL

logging.basicConfig(level=logging.INFO)

engine = create_engine(DATABASE_URL)


def extract_poll_summary():
    logging.info("Extracting poll summary")

    query = text("""
        SELECT 
            p.id,
            p.question,
            u.name AS creator_name,
            COUNT(v.id) AS total_votes,
            p.created_at
        FROM polls p
        JOIN users u ON p.creator_id = u.id
        LEFT JOIN poll_options po ON po.poll_id = p.id
        LEFT JOIN votes v ON v.poll_option_id = po.id
        GROUP BY p.id, p.question, u.name, p.created_at
    """)

    with engine.connect() as conn:
        return pd.read_sql(query, conn)


def extract_participation_metrics():
    logging.info("Extracting participation metrics")

    query = text("""
        SELECT 
            p.id AS poll_id,
            COUNT(DISTINCT v.user_id) AS unique_voters,
            (COUNT(DISTINCT v.user_id)::float /
             (SELECT COUNT(*) FROM users)) * 100 AS participation_rate
        FROM polls p
        LEFT JOIN poll_options po ON po.poll_id = p.id
        LEFT JOIN votes v ON v.poll_option_id = po.id
        GROUP BY p.id
    """)

    with engine.connect() as conn:
        return pd.read_sql(query, conn)


def extract_vote_trends():
    logging.info("Extracting voting trends")

    query = text("""
        SELECT 
            DATE(created_at) AS vote_date,
            COUNT(*) AS votes_per_day
        FROM votes
        GROUP BY vote_date
        ORDER BY vote_date
    """)

    with engine.connect() as conn:
        return pd.read_sql(query, conn)


def load_analytics(df, table_name):
    logging.info(f"Loading {len(df)} rows into {table_name}")

    with engine.begin() as conn:
        df.to_sql(table_name, conn, if_exists="replace", index=False)


def run_pipeline():
    logging.info("Starting QuickPoll ETL pipeline")

    poll_summary = extract_poll_summary()
    participation = extract_participation_metrics()
    vote_trends = extract_vote_trends()

    load_analytics(poll_summary, "analytics_poll_summary")
    load_analytics(participation, "analytics_participation")
    load_analytics(vote_trends, "analytics_vote_trends")

    logging.info("ETL pipeline completed successfully")


if __name__ == "__main__":
    run_pipeline()
