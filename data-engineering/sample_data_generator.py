"""
Sample Data Generator for QuickPoll

Purpose
-------
Generate realistic sample data for development and testing.

This script populates the following tables:
- users
- polls
- poll_options
- votes

It helps test:
- ETL pipeline
- analytics dashboards
- frontend visualizations
"""

import random
import uuid
from datetime import datetime, timedelta

from faker import Faker
from sqlalchemy import create_engine, text

from config import DATABASE_URL


# Configuration
# -----------------------------------

NUM_USERS = 20
NUM_POLLS = 10
OPTIONS_PER_POLL = 4
NUM_VOTES = 100


fake = Faker()

engine = create_engine(DATABASE_URL)

# Generate Users
# -----------------------------------

def generate_users(conn):
    """
    Create fake users.
    """

    print("Generating users...")

    user_ids = []

    for _ in range(NUM_USERS):

        user_id = str(uuid.uuid4())

        conn.execute(
            text("""
            INSERT INTO users (
                id,
                email,
                password,
                full_name,
                role,
                created_at,
                updated_at
            )
            VALUES (
                :id,
                :email,
                :password,
                :full_name,
                :role,
                :created_at,
                :updated_at
            )
            """),
            {
                "id": user_id,
                "email": fake.email(),
                "password": "$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS",
                "full_name": fake.name(),
                "role": "USER",
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow()
            }
        )

        user_ids.append(user_id)

    """
    Extra Admin And User
    """
    user_id = str(uuid.uuid4())
    user_ids.append(user_id)
    conn.execute(
            text("""
            INSERT INTO users (
                id,
                email,
                password,
                full_name,
                role,
                created_at,
                updated_at
            )
            VALUES (
                :id,
                :email,
                :password,
                :full_name,
                :role,
                :created_at,
                :updated_at
            )
            """),
            {
                "id": user_id,
                "email": "adminadmin@quickpoll.com",
                "password": "$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS",
                "full_name": "Admin User",
                "role": "ADMIN",
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow()
            }
        )
    user_id = str(uuid.uuid4())
    user_ids.append(user_id)
    conn.execute(
            text("""
            INSERT INTO users (
                id,
                email,
                password,
                full_name,
                role,
                created_at,
                updated_at
            )
            VALUES (
                :id,
                :email,
                :password,
                :full_name,
                :role,
                :created_at,
                :updated_at
            )
            """),
            {
                "id": user_id,
                "email": "useruser@quickpoll.com",
                "password": "$2a$10$Pbjy8rZd5CnfNlmMtx6sSOdksvM7VnaVX7sSFNuzz8S14MczE/UuS",
                "full_name": "Regular User",
                "role": "USER",
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow()
            }
        )


    return user_ids


# Generate Polls
# -----------------------------------

def generate_polls(conn, user_ids):
    """
    Create polls linked to users.
    """

    print("Generating polls...")

    poll_ids = []

    for _ in range(NUM_POLLS):

        poll_id = str(uuid.uuid4())

        creator = random.choice(user_ids)

        conn.execute(
            text("""
            INSERT INTO polls (
                id,
                title,
                description,
                status,
                creator_id,
                multi_select,
                expires_at,
                created_at,
                updated_at
            )
            VALUES (
                :id,
                :title,
                :description,
                :status,
                :creator_id,
                :multi_select,
                :expires_at,
                :created_at,
                :updated_at
            )
            """),
            {
                "id": poll_id,
                "title": fake.sentence(nb_words=6),
                "description": fake.paragraph(),
                "status": "ACTIVE",
                "creator_id": creator,
                "multi_select": random.choice([True, False]),
                "expires_at": datetime.utcnow() + timedelta(days=7),
                "created_at": datetime.utcnow(),
                "updated_at": datetime.utcnow()
            }
        )

        poll_ids.append(poll_id)

    return poll_ids

# Generate Poll Options
# -----------------------------------

def generate_options(conn, poll_ids):
    """
    Create poll options for each poll.
    """

    print("Generating poll options...")

    option_ids = []

    for poll_id in poll_ids:

        for _ in range(OPTIONS_PER_POLL):

            option_id = str(uuid.uuid4())

            conn.execute(
                text("""
                INSERT INTO poll_options (
                    id,
                    poll_id,
                    option_text
                )
                VALUES (
                    :id,
                    :poll_id,
                    :option_text
                )
                """),
                {
                    "id": option_id,
                    "poll_id": poll_id,
                    "option_text": fake.word()
                }
            )

            option_ids.append((option_id, poll_id))

    return option_ids

# Generate Votes
# -----------------------------------

def generate_votes(conn, user_ids, option_ids):
    """
    Simulate voting activity.
    """

    print("Generating votes...")

    vote_records = set()

    for _ in range(NUM_VOTES):

        user_id = random.choice(user_ids)

        option_id, poll_id = random.choice(option_ids)

        # enforce unique constraint (poll_id, option_id, user_id)
        key = (poll_id, option_id, user_id)

        if key in vote_records:
            continue

        vote_records.add(key)

        conn.execute(
            text("""
            INSERT INTO votes (
                id,
                poll_id,
                option_id,
                user_id,
                created_at
            )
            VALUES (
                :id,
                :poll_id,
                :option_id,
                :user_id,
                :created_at
            )
            """),
            {
                "id": str(uuid.uuid4()),
                "poll_id": poll_id,
                "option_id": option_id,
                "user_id": user_id,
                "created_at": datetime.utcnow()
            }
        )

# Main Generator
# -----------------------------------

def run():

    print("Starting QuickPoll Sample Data Generation")

    with engine.begin() as conn:

        users = generate_users(conn)

        polls = generate_polls(conn, users)

        options = generate_options(conn, polls)

        generate_votes(conn, users, options)

    print("Sample data generation completed successfully")

# Entry Point
# -----------------------------------

if __name__ == "__main__":
    run()