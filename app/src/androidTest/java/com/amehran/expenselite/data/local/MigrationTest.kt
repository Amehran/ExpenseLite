package com.amehran.expenselite.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private const val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ExpenseDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1)

        // Insert some data in version 1
        db.execSQL("INSERT INTO categories (id, name, iconResName, isSystemDefault) VALUES (1, 'Food', 'ic_food', 0)")
        db.execSQL("INSERT INTO expenses (id, title, amountCents, categoryId, timestamp, isIncome, isSubscription, recurrenceInterval, isPaused) VALUES (1, 'Lunch', 1500, 1, 100000, 0, 0, 'NONE', 0)")

        // Prepare for the next version.
        db.close()

        // Re-open the database with version 2 and provide MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, ExpenseDatabase.MIGRATION_1_2)

        // Query the data to ensure it was preserved and new columns have defaults
        val cursor = db.query("SELECT * FROM expenses WHERE id = 1")
        cursor.moveToFirst()

        val categoryNameIndex = cursor.getColumnIndex("categoryName")
        val categoryColorHexIndex = cursor.getColumnIndex("categoryColorHex")

        assertEquals("", cursor.getString(categoryNameIndex))
        assertEquals("", cursor.getString(categoryColorHexIndex))

        val titleIndex = cursor.getColumnIndex("title")
        assertEquals("Lunch", cursor.getString(titleIndex))

        cursor.close()

        val categoryCursor = db.query("SELECT * FROM categories WHERE id = 1")
        categoryCursor.moveToFirst()
        val colorHexIndex = categoryCursor.getColumnIndex("colorHex")
        assertEquals("", categoryCursor.getString(colorHexIndex))
        categoryCursor.close()
    }
}
