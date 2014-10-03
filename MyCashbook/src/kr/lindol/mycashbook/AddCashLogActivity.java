
package kr.lindol.mycashbook;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This class can add cashlog to listview in MainActivity
 * 
 * I refer on website
 *  - passing intent to parent Activity, http://stackoverflow.com/questions/17242713/how-to-pass-parcelable-object-from-child-to-parent-activity
 *   - example of startActivityForResult(), http://jamdol.tistory.com/37
 *    - how to passing ArrayList or CustomObject to Activity, http://arsviator.blogspot.kr/2010/10/parcelable%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%9C-%EC%98%A4%EB%B8%8C%EC%A0%9D%ED%8A%B8-%EC%A0%84%EB%8B%AC-object.html
 *    
 * @author lindol
 */
public class AddCashLogActivity extends Activity {

    /**
     * adding data
     */
    private ArrayList<CashLogItem> dataList = new ArrayList<CashLogItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_cashlog);

        Button addButton = (Button) findViewById(R.id.button_item_add);
        addButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                EditText tagText = (EditText) findViewById(R.id.string_of_tag);
                EditText priceText = (EditText) findViewById(R.id.value_of_price);

                String tagTextValue = tagText.getText().toString();

                if (tagTextValue.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.alert_string_empty_tag_text), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                int inputPrice = 0;
                try {
                    inputPrice = Integer.valueOf(priceText.getText().toString());

                } catch (NumberFormatException ne) {
                    inputPrice = 0;
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.alert_string_empty_tag_text), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                dataList.add(new CashLogItem(tagTextValue, inputPrice));

                Toast.makeText(getApplicationContext(),
                        getString(R.string.alert_string_added_your_item),
                        Toast.LENGTH_SHORT).show();

                clearInputField();
                
                // set focus
                // I refer http://stackoverflow.com/questions/1796671/move-focus-from-one-edit-text-box-to-another
                tagText.requestFocus();
            }
        });

        Button closeButton = (Button) findViewById(R.id.button_close);
        closeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // set cashlog list
                Intent passingData = new Intent();
                passingData.putParcelableArrayListExtra("addingList", dataList);
                setResult(77, passingData);
                finish();
            }
        });
    }

    /**
     * clear input data field
     */
    protected void clearInputField() {
        EditText tagText = (EditText) findViewById(R.id.string_of_tag);
        tagText.setText("");

        EditText priceText = (EditText) findViewById(R.id.value_of_price);
        priceText.setText("");
    }
}
