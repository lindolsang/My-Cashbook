package kr.lindol.mycashbook;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author lindol
 *  @see http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html
 *  i refer code in 'http://www.learn2crack.com/2013/10/android-custom-listview-images-text-example.html'
 */
public class CustomList extends ArrayAdapter<String> {

	private Activity context;
	private String[] tag;
	private Integer[] price;

	public CustomList(Activity context, String[] tag, Integer[] price) {
		super(context, R.layout.list_single, tag);

		this.context = context;
		this.tag = tag;
		this.price = price;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.list_single, null, true);

		TextView textTag = (TextView) rowView.findViewById(R.id.tag);
		textTag.setText(tag[position]);

		TextView textPrice = (TextView) rowView.findViewById(R.id.price);
		textPrice.setText(String.valueOf(price[position]));

		return rowView;
	}

}
