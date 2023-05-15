package solutions.mk.mobile.persist.migrations

import solutions.mk.mobile.persist.migration

val MIGRATION__INIT = migration(0, 1) {
    query(
        """
        CREATE TABLE records
        (
        file_name      TEXT NOT NULL UNIQUE,
        description    TEXT NOT NULL,
        issuer         TEXT NOT NULL
        );
    """.trimIndent()
    )
}