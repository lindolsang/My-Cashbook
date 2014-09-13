package kr.lindol.mycashbook;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.lindol.mycashbook.db.CashLogContract.CashLogEntry;
import kr.lindol.mycashbook.db.CashLogDbHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class is Main Class in application
 * 
 * I refer http://entireboy.egloos.com/viewer/4152244
 * 
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// creating dbHelper
		cashLogDbHelper = new CashLogDbHelper(getApplicationContext());

		// today display to textview
		TextView todayTextView = (TextView) findViewById(R.id.today);
		Date today = new Date();
		todayTextView.setText(String.format("%d-%d, %d", today.getDate(),
				today.getMonth() + 1, (1900 + today.getYear())));

		// read cashlog in cashLog Db
		readCashLogInDb();

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

		computeSumOfCash();

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

	}

	/**
	 * This method is to read cashlog in Db. This method will fill cashLogList
	 * from cashLog Db.
	 * I refer http://stackoverflow.com/questions/4920528/iterate-through-rows-from-sqlite-query
	 *   and http://developer.android.com/training/basics/data-storage/databases.html#DefineContract
	 */
	private void readCashLogInDb() {
		SQLiteDatabase db = cashLogDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { CashLogEntry._ID,
				CashLogEntry.COLUMN_NAME_ITEM_TITLE,
				CashLogEntry.COLUMN_NAME_PRICE, };

		// How you want the results sorted in the resulting Cursor
		String sortOrder = CashLogEntry.COLUMN_NAME_MONTH_TAG + " DESC";

		String selection = null;
		String[] selectionArgs = null;

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
	 * textview for 'sum'
	 * 
	 * note: I reference decimal comma format code in below website
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
						new Date());
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
