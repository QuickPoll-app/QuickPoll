package com.amalitech.qa.testdata;

import org.testng.annotations.DataProvider;

public class VoteTestData {

    // ════════════════════════════════════════════════════════════════════════
    // SEEDED POLL IDs (from DB seed data)
    // ════════════════════════════════════════════════════════════════════════

    // Single-select poll (Preferred Work Model)
    public static final String SINGLE_SELECT_POLL_ID =
            "660e8400-e29b-41d4-a716-446655440002";

    // Multi-select poll (Best Programming Language)
    public static final String MULTI_SELECT_POLL_ID =
            "660e8400-e29b-41d4-a716-446655440001";

    // ════════════════════════════════════════════════════════════════════════
    // SEEDED OPTION IDs (from DB seed data)
    // ════════════════════════════════════════════════════════════════════════

    // Single-select poll options (Preferred Work Model)
    public static final String OPTION_REMOTE   = "770e8400-e29b-41d4-a716-446655440005";
    public static final String OPTION_HYBRID   = "770e8400-e29b-41d4-a716-446655440006";
    public static final String OPTION_ON_SITE  = "770e8400-e29b-41d4-a716-446655440007";

    // Multi-select poll options (Best Programming Language)
    public static final String OPTION_JAVA         = "770e8400-e29b-41d4-a716-446655440001";
    public static final String OPTION_PYTHON       = "770e8400-e29b-41d4-a716-446655440002";
    public static final String OPTION_JAVASCRIPT   = "770e8400-e29b-41d4-a716-446655440003";
    public static final String OPTION_TYPESCRIPT   = "770e8400-e29b-41d4-a716-446655440004";

    // ════════════════════════════════════════════════════════════════════════
    // VOTE-001 — SINGLE SELECT VOTE DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Valid single select vote scenarios
     * Columns: pollId, optionId, expectedStatus, scenario
     */
    @DataProvider(name = "validSingleSelectVoteData")
    public static Object[][] validSingleSelectVoteData() {
        return new Object[][] {
                {
                        SINGLE_SELECT_POLL_ID,
                        OPTION_REMOTE,
                        201,
                        "Valid single select vote — choose Remote"
                },
                {
                        SINGLE_SELECT_POLL_ID,
                        OPTION_HYBRID,
                        201,
                        "Valid single select vote — choose Hybrid"
                }
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE-002 — MULTI SELECT VOTE DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Valid multi select vote scenarios
     * Columns: pollId, optionIds (comma-separated), expectedStatus, scenario
     */
    @DataProvider(name = "validMultiSelectVoteData")
    public static Object[][] validMultiSelectVoteData() {
        return new Object[][] {
                {
                        MULTI_SELECT_POLL_ID,
                        new String[]{ OPTION_JAVA, OPTION_PYTHON },
                        201,
                        "Valid multi select vote — two options"
                },
                {
                        MULTI_SELECT_POLL_ID,
                        new String[]{ OPTION_JAVA, OPTION_PYTHON, OPTION_JAVASCRIPT },
                        201,
                        "Valid multi select vote — three options"
                },
                {
                        MULTI_SELECT_POLL_ID,
                        new String[]{ OPTION_TYPESCRIPT },
                        201,
                        "Valid multi select vote — single option selected"
                }
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE-003 — DUPLICATE VOTE DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Duplicate vote scenarios
     * Columns: pollId, optionId, expectedStatus, scenario
     */
    @DataProvider(name = "duplicateVoteData")
    public static Object[][] duplicateVoteData() {
        return new Object[][] {
                {
                        SINGLE_SELECT_POLL_ID,
                        OPTION_ON_SITE,
                        409,
                        "Duplicate vote on single select poll"
                },
                {
                        MULTI_SELECT_POLL_ID,
                        new String[]{ OPTION_JAVA },
                        409,
                        "Duplicate vote on multi select poll"
                }
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // VOTE-005 — INVALID VOTE DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Invalid vote scenarios
     * Columns: pollId, optionIds, expectedStatus, scenario
     */
    @DataProvider(name = "invalidVoteData")
    public static Object[][] invalidVoteData() {
        return new Object[][] {
                {
                        SINGLE_SELECT_POLL_ID,
                        new String[]{},
                        400,
                        "Empty optionIds — no option selected"
                },
                {
                        "00000000-0000-0000-0000-000000000000",
                        new String[]{ OPTION_REMOTE },
                        404,
                        "Vote on non-existent poll"
                }
        };
    }

}
