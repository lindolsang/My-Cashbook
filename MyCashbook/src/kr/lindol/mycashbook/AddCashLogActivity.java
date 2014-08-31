package kr.lindol.mycashbook;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * This class can add cashlog to listview in MainActivity
 * @author lindol
 * 
 */
public class AddCashLogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_cashlog);
		
		
		Button closeButton= (Button)findViewById(R.id.button_close);
		closeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
	}
}
