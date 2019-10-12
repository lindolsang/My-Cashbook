package kr.lindol.mycashbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddCashLogActivity extends AppCompatActivity {

    /**
     * this is dummy ROWID for passing CashLogList to parent activity
     */
    private static final int DUMMY_ROW_ID = -1;
    /**
     * adding data
     */
    private ArrayList<CashLogItem> dataList = new ArrayList<CashLogItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cash_log);

        Button addButton = (Button) findViewById(R.id.button_item_add);
        addButton.setOnClickListener(new View.OnClickListener() {

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

                dataList.add(new CashLogItem(DUMMY_ROW_ID, tagTextValue, inputPrice));

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
        closeButton.setOnClickListener(new View.OnClickListener() {

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
