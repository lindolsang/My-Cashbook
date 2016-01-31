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
 *      'http://www.learn2crack.com/2013/10/android-custom-listview-images-text-exam
 *      p l e . h t m l '
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

		CheckBox cashlogSelected = (CheckBox) rowView
				.findViewById(R.id.checked_cash_log);

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
	 * 
	 * @return
	 */
	public boolean isVisibleCashlogCheckBox() {
		return isVisibleCashlogCheckBox;
	}

	/**
	 * This method is setter for state of visible for cashlog checkbox if
	 * isVisibleCashlogCheckbox is true checkbox will show in listitem if
	 * isVisibleCashlogCheckboxis false checkbox will disappear in listitem
	 * 
	 * @param isVisibleCashlogCheckBox
	 */
	public void setVisibleCashlogCheckBox(boolean isVisibleCashlogCheckBox) {
		this.isVisibleCashlogCheckBox = isVisibleCashlogCheckBox;
		notifyDataSetChanged();
	}

	/**
	 * This method will return the counting value that is selected by user
	 * 
	 * @return
	 */
	public int computeSelectedItemsCount() {

		int returnCount = 0;

		for (CashLogItem logItem : cashLogList) {
			if (logItem.isChecked()) {
				returnCount = returnCount + 1;
			}
		}

		return returnCount;
	}

	/**
	 * This method will get to your selected item object as CashLogItem
	 * 
	 * @return
	 */
	public ArrayList<CashLogItem> getSelectedItemsToArrayList() {

		ArrayList<CashLogItem> returnList = new ArrayList<CashLogItem>();

		for (CashLogItem logItem : cashLogList) {
			if (logItem.isChecked()) {
				returnList.add(logItem);
			}
		}
		return returnList;
	}
}
