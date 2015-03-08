package kr.lindol.mycashbook;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * 
 * @author lindol
 * @see http 
 *      ://www.learn2crack.com/2013/10/android-custom-listview-images-text-example
 *      .html i refer code in
 *      'http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.
 *      h t m l '
 */
public class CustomList extends ArrayAdapter<CashLogItem> {

	private Activity context;
	private ArrayList<CashLogItem> cashLogList = null;
	private DecimalFormat df = new DecimalFormat("#,##0");
	private boolean isVisibleCashlogCheckBox = false;

	public CustomList(Activity context, ArrayList<CashLogItem> cashLogList) {
		super(context, R.layout.list_single, cashLogList);
		this.context = context;
		this.cashLogList = cashLogList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single, null, true);

		TextView textTag = (TextView) rowView.findViewById(R.id.tag);
		textTag.setText(cashLogList.get(position).getTag());

		TextView textPrice = (TextView) rowView.findViewById(R.id.price);

		textPrice.setText(df.format(cashLogList.get(position).getPrice()));

		CheckBox cashlogSelected = (CheckBox) rowView.findViewById(R.id.checked_cash_log);

		// For checkbox
		CheckBox isSelectedBox = (CheckBox) rowView
				.findViewById(R.id.checked_cash_log);
		if (isVisibleCashlogCheckBox) {
			isSelectedBox.setVisibility(View.VISIBLE);
			cashlogSelected.setChecked(cashLogList.get(position).isChecked());
		} else {
			isSelectedBox.setVisibility(View.GONE);
			cashlogSelected.setChecked(false);
		}
		return rowView;
	}

	/**
	 * This method will return state of visible for cashlog checkbox
	 * @return
	 */
	public boolean isVisibleCashlogCheckBox() {
		return isVisibleCashlogCheckBox;
	}

	/**
	 * This method is setter for state of visible for cashlog checkbox
	 * if isVisibleCashlogCheckbox is true
	 *  checkbox will show in listitem
	 * if isVisibleCashlogCheckboxis false
	 *  checkbox will disappear in listitem
	 *   
	 * @param isVisibleCashlogCheckBox
	 */
	public void setVisibleCashlogCheckBox(boolean isVisibleCashlogCheckBox) {
		this.isVisibleCashlogCheckBox = isVisibleCashlogCheckBox;
		notifyDataSetChanged();
	}
}
