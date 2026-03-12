/*
-------------------------------------------------------
QuickPoll Analytics Dashboard Views


These views expose analytics datasets for the backend
dashboard endpoints. They read from ETL-generated
analytics tables.

Backend services can query these views directly.
-------------------------------------------------------
*/


-- Dashboard Poll Summary
-- Shows total votes per poll
-------------------------------------------------------

CREATE OR REPLACE VIEW dashboard_poll_summary AS
SELECT
    id AS poll_id,
    title,
    creator_name,
    total_votes,
    created_at
FROM analytics_poll_summary;


-- Dashboard Vote Trends
-- Shows daily voting activity
-------------------------------------------------------

CREATE OR REPLACE VIEW dashboard_vote_trends AS
SELECT
    vote_date,
    votes_per_day
FROM analytics_vote_trends;


-- Dashboard User Participation
-- Shows voting participation metrics
-------------------------------------------------------

CREATE OR REPLACE VIEW dashboard_user_participation AS
SELECT
    voter_name,
    total_votes_cast,
    participation_rate
FROM analytics_user_participation;