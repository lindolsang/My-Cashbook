package kr.lindol.mycashbook;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	ListView list;

	ArrayList<CashLogItem> cashlogList = new ArrayList<CashLogItem>();

	private CustomList adapter;
	private int testInputIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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

		Button addButton = (Button) findViewById(R.id.button_add_cash_log);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "You Clicked 'add button'",
						Toast.LENGTH_SHORT).show();
				adapter.add(new CashLogItem(String.format("#테스트%d",
						testInputIndex++), (testInputIndex * 100)));
			}
		});
	}
}
