package kr.lindol.mycashbook;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

import kr.lindol.mycashbook.db.CashLogContract.CashLogEntry;
import kr.lindol.mycashbook.db.CashLogDbHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.SyncStateContract.Columns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class is Main Class in application
 * 
 * I refer http://entireboy.egloos.com/viewer/4152244
 *   and http://stackoverflow.com/questions/5134231/android-closing-activity-programatically
 * @author lindol
 * 
 */
public class MainActivity extends Activity {

    private static final int ADD_CASHLOG = 77;

    ListView list;

    ArrayList<CashLogItem> cashlogList = new ArrayList<CashLogItem>();

    private CustomList adapter;
    private int testInputIndex = 0;

    private CashLogDbHelper cashLogDbHelper = null;

    private Calendar currentDate = Calendar.getInstance();

    private int sumOfThisMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creating dbHelper
        cashLogDbHelper = new CashLogDbHelper(getApplicationContext());

        displaycashLog();

        Button addButton = (Button) findViewById(R.id.button_add_cash_log);
        addButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // open addCashlogActivity
                Intent intent = new Intent(MainActivity.this,
                        AddCashLogActivity.class);
                startActivityForResult(intent, ADD_CASHLOG);
            }
        });

        Button yesterdayButton = (Button) findViewById(R.id.button_yesterday);
        yesterdayButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // go yesterday
                currentDate.add(Calendar.DAY_OF_YEAR, -1);
                displaycashLog();
            }
        });

        Button tomorrowButton = (Button) findViewById(R.id.button_tomorrow);
        tomorrowButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // go tomorrow
                currentDate.add(Calendar.DAY_OF_YEAR, 1);
                displaycashLog();
            }
        });

        Button todayButton = (Button) findViewById(R.id.button_today);
        todayButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                currentDate = Calendar.getInstance();
                displaycashLog();
            }
        });

        Button closeAppButton = (Button) findViewById(R.id.button_close_app);
        closeAppButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // finish MainActivity
                finish();
            }
        });
    }

    /**
     * This method is display cashlog in listview I refer
     * http://tutorials.jenkov.com/java-date-time/java-util-calendar.html
     */
    private void displaycashLog() {

        // reset
        cashlogList.clear();

        // today display to textview
        TextView todayTextView = (TextView) findViewById(R.id.today);

        // display current date
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        todayTextView.setText(dateFormater.format(new Date(currentDate.getTimeInMillis())));

        // display cashlog in today
        SimpleDateFormat todayFormater = new SimpleDateFormat("yyyyMMdd");
        String today = todayFormater.format(currentDate.getTimeInMillis());

        // read cashlog in cashLog Db
        readCashLogInDb(today);

        SimpleDateFormat thisMonthFormatter = new SimpleDateFormat("yyyyMM");
        String thisMonth = thisMonthFormatter.format(currentDate.getTimeInMillis());

        // read sum of month in db (using SUM aggregate function)
        sumOfThisMonth = computeSumOfMonthInDb(thisMonth);

        // do show cashlog
        adapter = new CustomList(MainActivity.this, cashlogList);
        list = (ListView) findViewById(R.id.log_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                Toast.makeText(MainActivity.this,
                        "You Clicked at " + cashlogList.get(position).getTag(),
                        Toast.LENGTH_SHORT).show();

            }
        });

        /**
         * I refer below link.
         * - http://stackoverflow.com/questions/8846707/how-to-implement-a-long-click-listener-on-a-listview
         */
        list.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO I will make to popup window for modify cashlog
                Toast.makeText(MainActivity.this, "Your item longclick at " + position,
                        Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        
        computeSumOfCash();
    }

    /**
     * 
     * get sum of month from Database (Using SUM aggregate function) through thisMonth
     * @param thisMonth
     * @return return sum of month
     */
    private int computeSumOfMonthInDb(String thisMonth) {

        int returnSumOfMOnthValue = 0;

        SQLiteDatabase db = cashLogDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String queryOfSum = String.format("SELECT SUM(%s) FROM %s WHERE %s = ?",
                CashLogEntry.COLUMN_NAME_PRICE, CashLogEntry.TABLE_NAME,
                CashLogEntry.COLUMN_NAME_MONTH_TAG);

        String selectionArgs[] = {
                thisMonth
        };

        Cursor c = db.rawQuery(queryOfSum, selectionArgs);
        if (c.moveToFirst()) {
            // get sum of month value
            returnSumOfMOnthValue = c.getInt(0);
        }
        c.close();

        return returnSumOfMOnthValue;
    }

    /**
     * This method is to read cashlog in Db. This method will fill cashLogList
     * from cashLog Db. I refer
     * http://stackoverflow.com/questions/4920528/iterate-through-rows-from-sqlite-query and
     * http://developer.android.com/training/basics/data-storage/databases.html#DefineContract
     */
    private void readCashLogInDb(String date) {
        SQLiteDatabase db = cashLogDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                CashLogEntry._ID,
                CashLogEntry.COLUMN_NAME_ITEM_TITLE,
                CashLogEntry.COLUMN_NAME_PRICE,
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = CashLogEntry.COLUMN_NAME_MONTH_TAG + " DESC";

        String selection = CashLogEntry.COLUMN_NAME_DAY_TAG + "= ?";
        String[] selectionArgs = new String[] {
                date
        };

        Cursor c = db.query(CashLogEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by groups
                sortOrder // The sort order
                );

        c.moveToFirst();
        for (; c.isAfterLast() == false; c.moveToNext()) {
            String tag = c
                    .getString(c
                            .getColumnIndexOrThrow(CashLogEntry.COLUMN_NAME_ITEM_TITLE));
            long price = c.getLong(c
                    .getColumnIndexOrThrow(CashLogEntry.COLUMN_NAME_PRICE));

            cashlogList.add(new CashLogItem(tag, (int) price));
        }
    };

    /**
     * This method is compute Sum of cashlog and this method will update to
     * textview for 'sum' and 'sum of month' 
     * note: I reference decimal comma format code in below
     * website http://mwultong.blogspot.com/2006/11/java-3-comma-commify.html
     */
    private void computeSumOfCash() {
        double sum = 0;
        for (CashLogItem cashLogItem : cashlogList) {
            sum = sum + cashLogItem.getPrice();
        }

        TextView sumOfCashView = (TextView) findViewById(R.id.sum_of_cash);

        DecimalFormat df = new DecimalFormat("#,##0");

        sumOfCashView.setText(df.format(sum));

        // update sum of month
        TextView sumOfMonth = (TextView) findViewById(R.id.sum_of_month);

        // zero base month of year
        int thisMonth = currentDate.get(Calendar.MONTH) + 1;
        sumOfMonth.setText(String.format("%d월 %s", thisMonth, df.format(sumOfThisMonth)));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ADD_CASHLOG) {

            int itemCount = 0;
            ArrayList<CashLogItem> itemList = data
                    .getParcelableArrayListExtra("addingList");
            itemCount = itemList.size();

            for (CashLogItem cashLog : itemList) {
                adapter.add(new CashLogItem(cashLog.getTag(), cashLog
                        .getPrice()));
                insertCashLogIntoDb(
                        new CashLogItem(cashLog.getTag(), cashLog.getPrice()),
                        new Date(currentDate.getTimeInMillis()));
                /*
                 * add cashLog to sumOfThisMonth value because we do not call
                 * computeSumOfMonthInDb function.
                 */
                sumOfThisMonth = sumOfThisMonth + cashLog.getPrice();

            }

            // compute sum of cash and update sum's textView
            computeSumOfCash();

            Toast.makeText(
                    getApplicationContext(),
                    String.format(
                            getString(R.string.alert_string_added_cashlog_item),
                            itemCount), Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * insert new row into DB for cashLog
     * 
     * @param cashLogItem
     * @param date
     */
    private void insertCashLogIntoDb(CashLogItem cashLogItem, Date date) {

        // Gets the data repository in write mode
        SQLiteDatabase db = cashLogDbHelper.getWritableDatabase();

        SimpleDateFormat monthTagFormat = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat dayTagFormat = new SimpleDateFormat("yyyyMMdd");

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CashLogEntry.COLUMN_NAME_EVENT_ID,
                (int) (Math.random() * 10000));
        values.put(CashLogEntry.COLUMN_NAME_MONTH_TAG,
                monthTagFormat.format(date));
        values.put(CashLogEntry.COLUMN_NAME_DAY_TAG, dayTagFormat.format(date));
        values.put(CashLogEntry.COLUMN_NAME_ITEM_TITLE, cashLogItem.getTag());
        values.put(CashLogEntry.COLUMN_NAME_PRICE, cashLogItem.getPrice());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(CashLogEntry.TABLE_NAME,
                CashLogEntry.COLUMN_NAME_NULLABLE, values);

        Log.d("MyCashbook", String.format("newRowId: %d", newRowId));
    }
}
