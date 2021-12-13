package kr.lindol.mycashbook.data.db;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CashLog.class}, version = 2)
public abstract class CashLogDatabase extends RoomDatabase {
    public abstract CashLogDao cashLogDao();

    private static CashLogDatabase INSTANCE;

    public static CashLogDatabase getInstance(@NonNull Context context) {
        checkNotNull(context, "context cannot be null");

        // TODO: 2021/09/22 needs to improve for thread safe
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    CashLogDatabase.class,
                    "cashLog.db")
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }

        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        private static final String TAG = "Migration";

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            Log.i(TAG, "Start migration 1 to 2");

            // create SQL for room
            database.execSQL("CREATE TABLE IF NOT EXISTS `cash_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `item` TEXT, `type` INTEGER NOT NULL, `amount` INTEGER NOT NULL, `day_tag` TEXT, `month_tag` TEXT, `created_by` INTEGER NOT NULL, `description` TEXT)");

            Cursor cursor = database.query("SELECT * FROM cash_log");
            int idIndex = cursor.getColumnIndex("_id");
            int monthTagIndex = cursor.getColumnIndex("month_tag");
            int dayTagIndex = cursor.getColumnIndex("day_tag");
            int itemIndex = cursor.getColumnIndex("item_title");
            int priceIndex = cursor.getColumnIndex("price");

            while (cursor.moveToNext()) {
                ContentValues cv = new ContentValues();
                cv.put("id", cursor.getLong(idIndex));
                cv.put("item", cursor.getString(itemIndex));
                cv.put("type", 1);
                cv.put("amount", cursor.getLong(priceIndex));
                cv.put("day_tag", cursor.getString(dayTagIndex));
                cv.put("month_tag", cursor.getString(monthTagIndex));
                cv.put("created_by", System.currentTimeMillis());
                cv.put("description", "");

                // insert old table's data into new table
                database.insert("cash_logs", SQLiteDatabase.CONFLICT_REPLACE, cv);
            }

            cursor.close();

            // delete old table
            database.execSQL("DROP TABLE IF EXISTS cash_log");

            Log.i(TAG, "Done - migration 1 to 2");
        }
    };
}
