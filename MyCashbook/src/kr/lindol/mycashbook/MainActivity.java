package kr.lindol.mycashbook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	ListView list;
	String[] tag = {
			"#마트비", 
			"#맥주",
			"#택시비",
			"#활명수",
			"#세탁소",
			"#마트비2",
			"#맥주2",
			"#택시비2",
			"#활명수2",
			"#세탁소2",
			"#마트비3",
			"#맥주3",
			"#택시비3",
			"#활명수3",
			"#세탁소3",
	};
	
	Integer[] price = {
			3000,
			13000,
			4500,
			800,
			3100,
			13100,
			4600,
			900,
			1000,
			50000,
			4000,
			2700,
			1000,
			3090,
			20000,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// i add test data (in case of tag, price)
		CustomList adapter = new CustomList(MainActivity.this, tag, price);
		list = (ListView)findViewById(R.id.log_list);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Toast.makeText(MainActivity.this, "You Clicked at " + tag[position], Toast.LENGTH_SHORT).show();
				
			}
		});
	}
}
