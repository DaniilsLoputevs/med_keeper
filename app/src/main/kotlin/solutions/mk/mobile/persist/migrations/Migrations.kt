package solutions.mk.mobile.persist.migrations

import solutions.mk.mobile.persist.migration

/*
TODO - create simple custom API for recreate SqlLite table -
        SQLite supports a limited subset of ALTER TABLE. [https://www.sqlite.org/lang_altertable.html]
        The ALTER TABLE command in SQLite allows the user to rename a table or to add a new column to an existing table.
        It is not possible to rename or remove a column & add or remove constraints from a table.
 */

/**
 * Migration changelog - list of all actual migrations.
 * Element order is IMPORTANT!!!
 */
val migrationChangelog by lazy {
    arrayOf(
        MIGRATION__INIT,
        MIGRATION__1__2,
        MIGRATION__2__3,
    )
}

val MIGRATION__INIT = migration(0, 1) {
    execSQL(
        """
        CREATE TABLE records
        (
        file_name      TEXT NOT NULL UNIQUE,
        description    TEXT NOT NULL,
        issuer         TEXT NOT NULL
        );
    """.trimIndent()
    )
    println("MIGRATION__INIT # END")
}
val MIGRATION__1__2 = migration(1, 2) {
    execSQL(
        """
        CREATE TABLE IF NOT EXISTS `groups`
        (
            `name` TEXT NOT NULL,
            PRIMARY KEY (`name`)
        );
    """.trimIndent()
    )
    execSQL(
        """
        CREATE TABLE IF NOT EXISTS `record__group__relation`
        (
            `fileName`  TEXT NOT NULL,
            `groupName` TEXT NOT NULL,
            PRIMARY KEY (`fileName`, `groupName`)
        );
    """.trimIndent()
    )

    println("MIGRATION__1__2 # END")
}
val MIGRATION__2__3 = migration(2, 3) {
    println("MIGRATION__2__3 # RUN")

    +"DROP TABLE record__group__relation"
    +"""
        CREATE TABLE IF NOT EXISTS `record__group__relation`
        (
            file_name  TEXT NOT NULL,
            group_name TEXT NOT NULL,
            PRIMARY KEY (file_name, group_name)
        );
    """.trimIndent()

    +"""
        CREATE TABLE records__new
        (
        file_name      TEXT NOT NULL UNIQUE PRIMARY KEY,
        description    TEXT NOT NULL DEFAULT ''
        );
    """.trimIndent()
    +"INSERT INTO records__new SELECT r.fileName, r.description FROM records r"
    +"DROP TABLE records"
    +"ALTER TABLE records__new RENAME TO records"
}