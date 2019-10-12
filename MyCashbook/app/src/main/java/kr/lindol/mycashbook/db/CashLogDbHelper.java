package kr.lindol.mycashbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CashLogDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cashLog.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
            + CashLogContract.CashLogEntry.TABLE_NAME + " ( " + CashLogContract.CashLogEntry._ID
            + " INTEGER PRIMARY KEY, " + CashLogContract.CashLogEntry.COLUMN_NAME_EVENT_ID
            + TEXT_TYPE + COMMA_SEP + CashLogContract.CashLogEntry.COLUMN_NAME_MONTH_TAG
            + TEXT_TYPE + COMMA_SEP + CashLogContract.CashLogEntry.COLUMN_NAME_DAY_TAG
            + TEXT_TYPE + COMMA_SEP + CashLogContract.CashLogEntry.COLUMN_NAME_ITEM_TITLE
            + TEXT_TYPE + COMMA_SEP + CashLogContract.CashLogEntry.COLUMN_NAME_PRICE
            + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + CashLogContract.CashLogEntry.TABLE_NAME;

    public CashLogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
