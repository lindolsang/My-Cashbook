package kr.lindol.mycashbook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kr.lindol.mycashbook.db.CashLogContract;
import kr.lindol.mycashbook.db.CashLogDbHelper;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_CASHLOG = 77;

    ListView list;

    ArrayList<CashLogItem> cashlogList = new ArrayList<CashLogItem>();

    private CustomList adapter;
    private CashLogDbHelper cashLogDbHelper = null;

    private Calendar currentDate = Calendar.getInstance();

    private int sumOfThisMonth;

    private int numberOfSelected = 0;

    private Activity mainActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivity = this;

        // creating dbHelper
        cashLogDbHelper = new CashLogDbHelper(getApplicationContext());

        displaycashLog();

        Button selectButton = (Button) findViewById(R.id.button_select_item);
        selectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // reset to zero value for initial
                resetValueOfselectedItem();

                // change state of visible for checkbox in listitem
                if (adapter.isVisibleCashlogCheckBox()) {
                    int listCount = adapter.getCount();
                    for (int i = 0; i < listCount; i++) {
                        adapter.getItem(i).setChecked(false);
                    }
                }
                adapter.setVisibleCashlogCheckBox(!adapter
                        .isVisibleCashlogCheckBox());
            }
        });

        Button deleteButton = (Button) findViewById(R.id.button_delete_item);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // create dialog for confirming delete
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        mainActivity);

                builder.setMessage(
                        String.format(
                                getString(R.string.confirm_delete_message),
                                adapter.computeSelectedItemsCount())).setTitle(
                        getString(R.string.confirm_delete_title));

                // Add the buttons for cancel and doing delete

                builder.setPositiveButton(getString(R.string.button_do_delete),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                ArrayList<CashLogItem> deletedList = adapter
                                        .getSelectedItemsToArrayList();

                                int deleted = deleteCashLogFromDB(deletedList);

                                Toast.makeText(
                                        getApplicationContext(),
                                        String.format(
                                                getString(R.string.alert_deleted_item_count_message),
                                                deleted), Toast.LENGTH_SHORT)
                                        .show();

                                // If list has been deleted row, Do read
                                // cashloglist from db again.
                                if (deleted > 0) {
                                    displaycashLog();
                                }
                            }
                        });

                builder.setNegativeButton(getString(R.string.button_do_cancel),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        Button addButton = (Button) findViewById(R.id.button_add_cash_log);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // open addCashlogActivity
                Intent intent = new Intent(MainActivity.this,
                        AddCashLogActivity.class);
                startActivityForResult(intent, ADD_CASHLOG);
            }
        });

        Button yesterdayButton = (Button) findViewById(R.id.button_yesterday);
        yesterdayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // go yesterday
                currentDate.add(Calendar.DAY_OF_YEAR, -1);
                displaycashLog();
            }
        });

        Button tomorrowButton = (Button) findViewById(R.id.button_tomorrow);
        tomorrowButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // go tomorrow
                currentDate.add(Calendar.DAY_OF_YEAR, 1);
                displaycashLog();
            }
        });

        Button todayButton = (Button) findViewById(R.id.button_today);
        todayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentDate = Calendar.getInstance();
                displaycashLog();
            }
        });

        Button closeAppButton = (Button) findViewById(R.id.button_close_app);
        closeAppButton.setOnClickListener(new View.OnClickListener() {

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

        // reset value of selectedItem
        resetValueOfselectedItem();
        // change state of visible for delete button
        DoInvisibleOrVisibleDeleteButton();

        // reset
        cashlogList.clear();

        // today display to textview
        TextView todayTextView = (TextView) findViewById(R.id.today);

        // display current date
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd (E)");
        todayTextView.setText(dateFormater.format(new Date(currentDate
                .getTimeInMillis())));

        // display cashlog in today
        SimpleDateFormat todayFormater = new SimpleDateFormat("yyyyMMdd");
        String today = todayFormater.format(currentDate.getTimeInMillis());

        // read cashlog in cashLog Db
        readCashLogInDb(today);

        SimpleDateFormat thisMonthFormatter = new SimpleDateFormat("yyyyMM");
        String thisMonth = thisMonthFormatter.format(currentDate
                .getTimeInMillis());

        // read sum of month in db (using SUM aggregate function)
        sumOfThisMonth = computeSumOfMonthInDb(thisMonth);

        // do show cashlog
        adapter = new CustomList(MainActivity.this, cashlogList);
        list = (ListView) findViewById(R.id.log_list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (adapter.isVisibleCashlogCheckBox()) {
                    CashLogItem selectedItem = adapter.getItem(position);
                    selectedItem.setChecked(!selectedItem.isChecked());

					/*Log.d("MyCashbook",
							String.valueOf(selectedItem.isChecked()));*/

                    if (selectedItem.isChecked()) {
                        plusNumberOfSelected();
                    } else {
                        minusNumberOfSelected();
                    }
                    // update state of checkbox in cashloglsit
                    adapter.notifyDataSetChanged();

                    // check delete button for show button or does'n show button
                    DoInvisibleOrVisibleDeleteButton();
                }

                /*
                 * Toast.makeText(MainActivity.this, "You Clicked at " +
                 * cashlogList.get(position).getTag(),
                 * Toast.LENGTH_SHORT).show();
                 */
            }

        });

        /**
         * I refer below link. -
         * http://stackoverflow.com/questions/8846707/how-to
         * -implement-a-long-click-listener-on-a-listview
         */
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                // TODO I will make to popup window for modify cashlog
                /*
                 * Toast.makeText(MainActivity.this, "Your item longclick at " +
                 * position, Toast.LENGTH_SHORT).show();
                 */

                return true;
            }
        });

        computeSumOfCash();
    }

    private void DoInvisibleOrVisibleDeleteButton() {
        Button delButton = (Button) findViewById(R.id.button_delete_item);

        if (getSelectedItemCount() > 0) {
            delButton.setVisibility(View.VISIBLE);
        } else {
            delButton.setVisibility(View.GONE);
        }
    }

    /**
     * get sum of month from Database (Using SUM aggregate function) through
     * thisMonth
     *
     * @param thisMonth
     * @return return sum of month
     */
    private int computeSumOfMonthInDb(String thisMonth) {

        int returnSumOfMOnthValue = 0;

        SQLiteDatabase db = cashLogDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String queryOfSum = String.format(
                "SELECT SUM(%s) FROM %s WHERE %s = ?",
                CashLogContract.CashLogEntry.COLUMN_NAME_PRICE, CashLogContract.CashLogEntry.TABLE_NAME,
                CashLogContract.CashLogEntry.COLUMN_NAME_MONTH_TAG);

        String selectionArgs[] = {thisMonth};

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
     * http://stackoverflow.com/questions/4920528/iterate
     * -through-rows-from-sqlite-query and
     * http://developer.android.com/training/
     * basics/data-storage/databases.html#DefineContract
     */
    private void readCashLogInDb(String date) {
        SQLiteDatabase db = cashLogDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {CashLogContract.CashLogEntry._ID,
                CashLogContract.CashLogEntry.COLUMN_NAME_ITEM_TITLE,
                CashLogContract.CashLogEntry.COLUMN_NAME_PRICE,};

        // How you want the results sorted in the resulting Cursor
        String sortOrder = CashLogContract.CashLogEntry.COLUMN_NAME_MONTH_TAG + " DESC";

        String selection = CashLogContract.CashLogEntry.COLUMN_NAME_DAY_TAG + "= ?";
        String[] selectionArgs = new String[]{date};

        Cursor c = db.query(CashLogContract.CashLogEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by groups
                sortOrder // The sort order
        );

        c.moveToFirst();
        for (; c.isAfterLast() == false; c.moveToNext()) {

            long itemRowId = c.getLong(c
                    .getColumnIndexOrThrow(CashLogContract.CashLogEntry._ID));
            String tag = c
                    .getString(c
                            .getColumnIndexOrThrow(CashLogContract.CashLogEntry.COLUMN_NAME_ITEM_TITLE));
            long price = c.getLong(c
                    .getColumnIndexOrThrow(CashLogContract.CashLogEntry.COLUMN_NAME_PRICE));

            cashlogList.add(new CashLogItem(itemRowId, tag, (int) price));
        }
    }

    ;

    /**
     * This method is compute Sum of cashlog and this method will update to
     * textview for 'sum' and 'sum of month' note: I reference decimal comma
     * format code in below website
     * http://mwultong.blogspot.com/2006/11/java-3-comma-commify.html
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
        sumOfMonth.setText(String.format(
                getString(R.string.display_date_format), thisMonth,
                df.format(sumOfThisMonth)));

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
                CashLogItem insertedItem = insertCashLogIntoDb(
                        cashLog.getTag(), cashLog.getPrice(), new Date(
                                currentDate.getTimeInMillis()));

                adapter.add(insertedItem);

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
    /**
     * Insert new cashlog into Database
     *
     * @param tag
     * @param price
     * @param date  when you want to insert to date
     * @return
     */
    private CashLogItem insertCashLogIntoDb(String tag, int price, Date date) {

        // Gets the data repository in write mode
        SQLiteDatabase db = cashLogDbHelper.getWritableDatabase();

        SimpleDateFormat monthTagFormat = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat dayTagFormat = new SimpleDateFormat("yyyyMMdd");

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CashLogContract.CashLogEntry.COLUMN_NAME_EVENT_ID,
                (int) (Math.random() * 10000));
        values.put(CashLogContract.CashLogEntry.COLUMN_NAME_MONTH_TAG,
                monthTagFormat.format(date));
        values.put(CashLogContract.CashLogEntry.COLUMN_NAME_DAY_TAG, dayTagFormat.format(date));
        values.put(CashLogContract.CashLogEntry.COLUMN_NAME_ITEM_TITLE, tag);
        values.put(CashLogContract.CashLogEntry.COLUMN_NAME_PRICE, price);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(CashLogContract.CashLogEntry.TABLE_NAME,
                CashLogContract.CashLogEntry.COLUMN_NAME_NULLABLE, values);

        //Log.d("MyCashbook", String.format("newRowId: %d", newRowId));

        return new CashLogItem(newRowId, tag, price);
    }

    /**
     * This method will be deleted by rowId I refered this link.
     * http://developer.android.com/training/basics/data-storage/databases.html
     *
     * @param deletedList
     */
    private int deleteCashLogFromDB(ArrayList<CashLogItem> deletedList) {

        int affectRows = 0;

        // Gets the data repository in write mode
        SQLiteDatabase db = cashLogDbHelper.getWritableDatabase();

        // Define 'where' part of query
        String selection = CashLogContract.CashLogEntry._ID + " = ? ";

        // doing delete
        for (CashLogItem deleteItem : deletedList) {
            String[] selectionArgs = {String.valueOf(deleteItem.getRowId())};
            int rows = db.delete(CashLogContract.CashLogEntry.TABLE_NAME, selection,
                    selectionArgs);
            if (rows > 0) {
                affectRows = affectRows + rows;
            }
        }
        return affectRows;
    }

    /**
     * This method will return count of selected cashlogItem
     *
     * @return
     */
    private int getSelectedItemCount() {
        return numberOfSelected;
    }

    /**
     * This method just plus 1 to value of numberOfSelected
     */
    private void minusNumberOfSelected() {
        numberOfSelected--;
    }

    /**
     * This method just minus 2 to value of numberOfSelected
     */
    private void plusNumberOfSelected() {
        numberOfSelected++;
    }

    /**
     * This method can set to number of selectItem value to zero value(0)
     */
    private void resetValueOfselectedItem() {
        numberOfSelected = 0;
    }
}
