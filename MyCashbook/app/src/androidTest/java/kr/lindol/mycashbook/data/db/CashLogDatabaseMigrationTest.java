package kr.lindol.mycashbook.data.db;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CashLogDatabaseMigrationTest {
    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    public CashLogDatabaseMigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                CashLogDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @After
    public void clean() {
        // clear old test data
        ApplicationProvider.getApplicationContext().deleteDatabase(TEST_DB);
    }

    private void insertSampleDataIntoOldDb(SQLiteDatabase db) {
        db.execSQL("INSERT INTO cash_log VALUES(1,'7723','202012','20201204','tgf','5000')");
        db.execSQL("INSERT INTO cash_log VALUES(2,'181','202111','20211104','ujh''','3666')");
        db.execSQL("INSERT INTO cash_log VALUES(3,'6156','202112','20211203','aaa','60')");
    }

    /**
     * migration testing from SQLite to Room
     *
     * @throws IOException
     */
    @Test
    public void migrate1To2() throws IOException {
        SQLiteDatabase oldDb = new OldDbOpenHelper(ApplicationProvider.getApplicationContext()).getWritableDatabase();
        insertSampleDataIntoOldDb(oldDb);
        oldDb.close();

        SupportSQLiteDatabase db = helper.runMigrationsAndValidate(TEST_DB, 2, true, CashLogDatabase.MIGRATION_1_2);
        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder("cash_logs").create();
        Cursor cursor = db.query(query);

        List<String> migratedList = new ArrayList<>();
        List<Long> migratedCreatedByDataList = new ArrayList<>();
        while (cursor.moveToNext()) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%d,", cursor.getInt(cursor.getColumnIndex("id"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("item"))));
            sb.append(String.format("%d,", cursor.getInt(cursor.getColumnIndex("type"))));
            sb.append(String.format("%d,", cursor.getLong(cursor.getColumnIndex("amount"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("day_tag"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("month_tag"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("description"))));

            migratedCreatedByDataList.add(cursor.getLong(cursor.getColumnIndex("created_by")));

            migratedList.add(sb.toString());
        }
        cursor.close();
        db.close();

        List<String> expectedList = new ArrayList<>();
        expectedList.add("1,tgf,1,5000,20201204,202012,,");
        expectedList.add("2,ujh',1,3666,20211104,202111,,"); // test with escaping character
        expectedList.add("3,aaa,1,60,20211203,202112,,");

        assertArrayEquals(expectedList.toArray(), migratedList.toArray());
        assertTrue(migratedCreatedByDataList.get(0).longValue() > 0);
        assertTrue(migratedCreatedByDataList.get(1).longValue() > 0);
        assertTrue(migratedCreatedByDataList.get(2).longValue() > 0);
    }

    /**
     * Migration test SQLite to Room without no rows. but tables is exists
     *
     * @throws IOException
     */
    @Test
    public void migrate1To2WhenTableHasNoRows() throws IOException {
        SQLiteDatabase oldDb = new OldDbOpenHelper(ApplicationProvider.getApplicationContext()).getWritableDatabase();
        oldDb.close();

        SupportSQLiteDatabase db = helper.runMigrationsAndValidate(TEST_DB, 2, true, CashLogDatabase.MIGRATION_1_2);
        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder("cash_logs").create();
        Cursor cursor = db.query(query);

        assertTrue(cursor.getCount() == 0);
    }

    private static class OldDbOpenHelper extends SQLiteOpenHelper {
        private static final int DATABASE_VERSION = 1;
        private boolean mCreate;
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE cash_log ( _id INTEGER PRIMARY KEY, event_id TEXT,month_tag TEXT,day_tag TEXT,item_title TEXT,price TEXT )";

        public OldDbOpenHelper(Context context) {
            this(context, true);
        }

        public OldDbOpenHelper(Context context, boolean create) {
            super(context, TEST_DB, null, DATABASE_VERSION);
            mCreate = create;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if (mCreate) {
                db.execSQL(SQL_CREATE_ENTRIES);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}