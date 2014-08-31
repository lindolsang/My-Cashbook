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

	ListView list;

	ArrayList<CashLogItem> cashlogList = new ArrayList<CashLogItem>();

	private CustomList adapter;
	private int testInputIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// today display to textview
		TextView todayTextView = (TextView)findViewById(R.id.today);
		Date today= new Date();
		todayTextView.setText(String.format("%d-%d, %d", today.getDate(), today.getMonth() + 1, (1900 + today.getYear())));
		
		// make test data

		cashlogList.add(new CashLogItem("#세탁소", 12000));
		cashlogList.add(new CashLogItem("#마트", 6900));

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
				Toast.makeText(MainActivity.this, "You Clicked 'add button'",
						Toast.LENGTH_SHORT).show();
				adapter.add(new CashLogItem(String.format("#테스트%d",
						testInputIndex++), (testInputIndex * 100)));
				
				// update sum of cash
				computeSumOfCash();
				
				// open addCashlogActivity
				Intent intent = new Intent(MainActivity.this, AddCashLogActivity.class);
				startActivity(intent);
			}
		});
	}

	/**
	 * This method is compute Sum of cashlog
	 *  and this method will update to textview for 'sum' 
	 *  
	 *  note:
	 *  I reference decimal comma format code in below website
	 *  http://mwultong.blogspot.com/2006/11/java-3-comma-commify.html
	 */
	private void computeSumOfCash() {
		double sum = 0;
		for(CashLogItem cashLogItem : cashlogList) {
			sum = sum + cashLogItem.getPrice();
		}
		
		TextView sumOfCashView = (TextView)findViewById(R.id.sum_of_cash);
		
		DecimalFormat df = new DecimalFormat("#,##0");
		
		sumOfCashView.setText(df.format(sum));
	}
}
