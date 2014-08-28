package kr.lindol.mycashbook;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author lindol
 * @see http 
 *      ://www.learn2crack.com/2013/10/android-custom-listview-images-text-example
 *      .html i refer code in
 *      'http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.h
 *      t m l '
 */
public class CustomList extends ArrayAdapter<CashLogItem> {

	private Activity context;
	private ArrayList<CashLogItem> cashLogList = null;

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
		textPrice.setText(String.valueOf(cashLogList.get(position).getPrice()));

		return rowView;
	}
}
