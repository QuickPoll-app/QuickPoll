package com.amalitech.qa.testdata;

import org.testng.annotations.DataProvider;

public class PollTestData {

    // ════════════════════════════════════════════════════════════════════════
    // VALID REQUEST BODIES
    // ════════════════════════════════════════════════════════════════════════

    public static final String VALID_SINGLE_SELECT_POLL = """
            {
                "title": "What is your favourite programming language?",
                "description": "Choose your favourite language",
                "type": "SINGLE_SELECT",
                "options": [
                    {"optionText": "Java"},
                    {"optionText": "Python"},
                    {"optionText": "JavaScript"}
                ]
            }
            """;

    public static final String VALID_MULTI_SELECT_POLL = """
            {
                "title": "Which frameworks do you use?",
                "description": "Select all that apply",
                "type": "MULTI_SELECT",
                "options": [
                    {"optionText": "Spring Boot"},
                    {"optionText": "Angular"},
                    {"optionText": "React"}
                ]
            }
            """;

    public static final String VALID_POLL_WITH_EXPIRY = """
            {
                "title": "When should we have the next team meeting?",
                "description": "Vote before the deadline",
                "type": "SINGLE_SELECT",
                "options": [
                    {"optionText": "Monday"},
                    {"optionText": "Tuesday"}
                ],
                "expiresAt": "2026-03-12T17:00:00Z"
            }
            """;

    // ════════════════════════════════════════════════════════════════════════
    // POLL-001 — CREATE POLL DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Valid poll creation scenarios
     * Columns: title, description, type, expectedStatus, scenario
     */
    @DataProvider(name = "validPollCreationData")
    public static Object[][] validPollCreationData() {
        return new Object[][] {
                {
                        "What is your favourite language?",
                        "Choose one",
                        "SINGLE_SELECT",
                        201,
                        "Valid single select poll"
                },
                {
                        "Which tools do you use?",
                        "Select all that apply",
                        "MULTI_SELECT",
                        201,
                        "Valid multi select poll"
                },
                {
                        "Do you prefer remote work?",
                        "",
                        "SINGLE_SELECT",
                        201,
                        "Valid poll without description"
                }
        };
    }

    /**
     * Invalid poll creation scenarios
     * Columns: title, type, options count, expectedStatus, scenario
     */
    @DataProvider(name = "invalidPollCreationData")
    public static Object[][] invalidPollCreationData() {
        return new Object[][] {
                {
                        "",
                        "SINGLE_SELECT",
                        2,
                        400,
                        "Missing title"
                },
                {
                        "Valid Title",
                        "",
                        2,
                        400,
                        "Missing poll type"
                },
                {
                        "Valid Title",
                        "SINGLE_SELECT",
                        1,
                        400,
                        "Only one option provided"
                },
                {
                        "Valid Title",
                        "SINGLE_SELECT",
                        0,
                        400,
                        "No options provided"
                },
                {
                        "Valid Title",
                        "INVALID_TYPE",
                        2,
                        400,
                        "Invalid poll type"
                }
        };
    }

    /**
     * Poll type scenarios
     * Columns: type, expectedStatus, scenario
     */
    @DataProvider(name = "pollTypeData")
    public static Object[][] pollTypeData() {
        return new Object[][] {
                {
                        "SINGLE_SELECT",
                        201,
                        "Single select poll type"
                },
                {
                        "MULTI_SELECT",
                        201,
                        "Multi select poll type"
                },
                {
                        "INVALID_TYPE",
                        400,
                        "Invalid poll type"
                },
                {
                        "",
                        400,
                        "Empty poll type"
                }
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-004 — EDIT POLL DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Valid poll update scenarios
     * Columns: newTitle, expectedStatus, scenario
     */
    @DataProvider(name = "validPollUpdateData")
    public static Object[][] validPollUpdateData() {
        return new Object[][] {
                {
                        "Updated Poll Title",
                        200,
                        "Update title before voting starts"
                },
                {
                        "Another Updated Title",
                        200,
                        "Update title again before voting starts"
                }
        };
    }

    // ════════════════════════════════════════════════════════════════════════
    // POLL-006 — EXPIRY DATE DATA PROVIDERS
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Expiry date scenarios
     * Columns: expiresAt, expectedStatus, scenario
     */
    @DataProvider(name = "expiryDateData")
    public static Object[][] expiryDateData() {
        return new Object[][] {
                {
                        "2026-03-12T17:00:00Z",
                        201,
                        "Valid future expiry date"
                },
                {
                        "2020-01-01T00:00:00Z",
                        400,
                        "Expiry date in the past"
                },
                {
                        "",
                        201,
                        "No expiry date — open ended poll"
                }
        };
    }

}