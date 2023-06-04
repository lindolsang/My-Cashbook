package kr.lindol.mycashbook.data.db;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.room.Room;
import androidx.room.migration.Migration;
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
import java.util.Arrays;
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

    private void insertVersion1DataSample(SQLiteDatabase db) {
        db.execSQL("INSERT INTO cash_log VALUES(1,'7723','202012','20201204','Watermelon','5000')");
        db.execSQL("INSERT INTO cash_log VALUES(2,'181','202111','20211104','Drinking water''','3666')");
        db.execSQL("INSERT INTO cash_log VALUES(3,'6156','202112','20211203','Pencil','60')");
    }

    /**
     * migration testing from SQLite to Room
     *
     * @throws IOException
     */
    @Test
    public void migrate1To2() throws IOException {
        SQLiteDatabase oldDb = new OldDbOpenHelper(ApplicationProvider.getApplicationContext()).getWritableDatabase();
        insertVersion1DataSample(oldDb);
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
        expectedList.add("1,Watermelon,1,5000,20201204,202012,,");
        expectedList.add("2,Drinking water',1,3666,20211104,202111,,"); // test with escaping character
        expectedList.add("3,Pencil,1,60,20211203,202112,,");

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

    @Test
    public void migrate1To3() throws Exception {
        SQLiteDatabase oldDb = new OldDbOpenHelper(ApplicationProvider.getApplicationContext()).getWritableDatabase();
        insertVersion1DataSample(oldDb);
        oldDb.close();

        SupportSQLiteDatabase db = helper.runMigrationsAndValidate(TEST_DB, 3, true, CashLogDatabase.MIGRATION_1_3);
        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder("cash_logs").create();
        Cursor cursor = db.query(query);

        List<String> migrated = new ArrayList<>();
        List<Long> migratedCreatedBy = new ArrayList<>();
        while (cursor.moveToNext()) {
            StringBuilder sb = new StringBuilder();

            sb.append(String.format("%d,", cursor.getInt(cursor.getColumnIndex("id"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("item"))));
            sb.append(String.format("%d,", cursor.getInt(cursor.getColumnIndex("type"))));
            sb.append(String.format("%d,", cursor.getLong(cursor.getColumnIndex("amount"))));
            sb.append(String.format("%d,", cursor.getInt(cursor.getColumnIndex("date_tag"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("day_tag"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("month_tag"))));
            //sb.append(String.format("%d,", cursor.getLong(cursor.getColumnIndex("created_by"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("description"))));

            migratedCreatedBy.add(cursor.getLong(cursor.getColumnIndex("created_by")));

            migrated.add(sb.toString());
        }

        cursor.close();
        db.close();

        List<String> expected = new ArrayList<>();
        expected.add("1,Watermelon,1,5000,20201204,20201204,202012,,");
        expected.add("2,Drinking water',1,3666,20211104,20211104,202111,,"); // test with escaping character
        expected.add("3,Pencil,1,60,20211203,20211203,202112,,");

        assertArrayEquals(expected.toArray(), migrated.toArray());
        assertTrue(migratedCreatedBy.get(0).longValue() > 0);
        assertTrue(migratedCreatedBy.get(1).longValue() > 0);
        assertTrue(migratedCreatedBy.get(2).longValue() > 0);
    }

    private void insertVersion2DataSample(SupportSQLiteDatabase db) {
        db.execSQL("INSERT INTO cash_logs VALUES(2,'Coffee',1,3500,'20230204','202302',1675607487237,'Ice coffee')");
        db.execSQL("INSERT INTO cash_logs VALUES(3,'bonus',0,100000,'20230204','202302',1675607524129,'deploy new one')");
        db.execSQL("INSERT INTO cash_logs VALUES(4,'ice cream',1,5000,'20230205','202302',1675607600570,'for my friends')");
        db.execSQL("INSERT INTO cash_logs VALUES(5,'bonus for day',0,50000,'20230205','202302',1675607655805,'test memo')");
        db.execSQL("INSERT INTO cash_logs VALUES(6,'Coffee',1,5000,'20230101','202301',1675607707281,'ice latte')");
        db.execSQL("INSERT INTO cash_logs VALUES(7,'new year bonus',0,50000,'20230102','202301',1675607740035,'test memo')");
    }

    @Test
    public void migrate2To3() throws Exception {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 2);

        // db has schema version 2.
        // insert data for version 2
        insertVersion2DataSample(db);

        // Prepare for  the next version.
        db.close();

        // Re-open the database with version 3 and provide
        // MIGRATION_2_3 as the migration process
        // verify schema changes
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, CashLogDatabase.MIGRATION_2_3);

        // validate that the data was migrated properly
        SupportSQLiteQuery query = SupportSQLiteQueryBuilder.builder("cash_logs").create();
        Cursor cursor = db.query(query);
        List<String> migrated = new ArrayList<>();
        while (cursor.moveToNext()) {
            StringBuilder sb = new StringBuilder();

            sb.append(String.format("%d,", cursor.getInt(cursor.getColumnIndex("id"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("item"))));
            sb.append(String.format("%d,", cursor.getInt(cursor.getColumnIndex("type"))));
            sb.append(String.format("%d,", cursor.getLong(cursor.getColumnIndex("amount"))));
            sb.append(String.format("%d,", cursor.getInt(cursor.getColumnIndex("date_tag"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("day_tag"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("month_tag"))));
            sb.append(String.format("%d,", cursor.getLong(cursor.getColumnIndex("created_by"))));
            sb.append(String.format("%s,", cursor.getString(cursor.getColumnIndex("description"))));

            migrated.add(sb.toString());
        }

        cursor.close();
        db.close();

        List<String> expected = Arrays.asList(
                "2,Coffee,1,3500,20230204,20230204,202302,1675607487237,Ice coffee,",
                "3,bonus,0,100000,20230204,20230204,202302,1675607524129,deploy new one,",
                "4,ice cream,1,5000,20230205,20230205,202302,1675607600570,for my friends,",
                "5,bonus for day,0,50000,20230205,20230205,202302,1675607655805,test memo,",
                "6,Coffee,1,5000,20230101,20230101,202301,1675607707281,ice latte,",
                "7,new year bonus,0,50000,20230102,20230102,202301,1675607740035,test memo,");

        assertArrayEquals(expected.toArray(new String[0]), migrated.toArray(new String[0]));
    }

    @Test
    public void migrateAll() throws Exception {
        // Create earliest version of the database.
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 2);
        db.close();

        // Open latest version of the database. Room will validate the schema
        // once all migrations execute.
        CashLogDatabase appDb = Room.databaseBuilder(
                        InstrumentationRegistry.getInstrumentation().getTargetContext(),
                        CashLogDatabase.class,
                        TEST_DB)
                .addMigrations(ALL_MIGRATIONS).build();
        appDb.getOpenHelper().getWritableDatabase();
        appDb.close();
    }

    // Array of all migrations
    private static final Migration[] ALL_MIGRATIONS = new Migration[]{
            CashLogDatabase.MIGRATION_1_2,
            CashLogDatabase.MIGRATION_2_3
    };

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