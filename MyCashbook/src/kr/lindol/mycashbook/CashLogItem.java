package kr.lindol.mycashbook;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  This class is value object for listview.
 *   
 * @author lindol
 *
 */

public class CashLogItem implements Parcelable {
	private String tag = null;
	private int price = 0;

	public CashLogItem(String tag, int price) {
		this.tag = tag;
		this.price = price;
	}
	
	public CashLogItem(Parcel in) {
		this.tag = in.readString();
		this.price = in.readInt();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(tag);
		dest.writeInt(price);
	}

	public static final Creator<CashLogItem> CREATOR = new Parcelable.Creator<CashLogItem>() {

		@Override
		public CashLogItem createFromParcel(Parcel source) {
			return new CashLogItem(source);
		}

		@Override
		public CashLogItem[] newArray(int size) {
			return new CashLogItem[size];
		}
	};
}