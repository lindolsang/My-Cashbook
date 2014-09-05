package kr.lindol.mycashbook;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int ADD_CASHLOG = 77;

	ListView list;

	ArrayList<CashLogItem> cashlogList = new ArrayList<CashLogItem>();

	private CustomList adapter;
	private int testInputIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// today display to textview
		TextView todayTextView = (TextView) findViewById(R.id.today);
		Date today = new Date();
		todayTextView.setText(String.format("%d-%d, %d", today.getDate(),
				today.getMonth() + 1, (1900 + today.getYear())));

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
}
