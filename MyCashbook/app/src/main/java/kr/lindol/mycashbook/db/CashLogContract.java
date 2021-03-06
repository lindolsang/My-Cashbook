package kr.lindol.mycashbook.db;

import android.provider.BaseColumns;

public class CashLogContract {
    public CashLogContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class CashLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "cash_log";
        public static final String COLUMN_NAME_EVENT_ID = "event_id";
        /**
         * data example) 201409
         */
        public static final String COLUMN_NAME_MONTH_TAG = "month_tag";
        /**
         * data example) 20140913
         */
        public static final String COLUMN_NAME_DAY_TAG = "day_tag";
        public static final String COLUMN_NAME_ITEM_TITLE = "item_title";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_NULLABLE = null;

    }
}
