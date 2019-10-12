package kr.lindol.mycashbook;

import android.os.Parcel;
import android.os.Parcelable;

public class CashLogItem implements Parcelable {
    private long rowId;
    private String tag = null;
    private int price = 0;
    private boolean isChecked;

    public CashLogItem(long rowId, String tag, int price) {
        this.rowId = rowId;
        this.tag = tag;
        this.price = price;
    }

    public CashLogItem(Parcel in) {
        this.rowId = in.readLong();
        this.tag = in.readString();
        this.price = in.readInt();
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }

    public long getRowId() {
        return rowId;
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
        dest.writeLong(rowId);
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

    /**
     * This method will return value of checkbox in CashLogItem
     *
     * @return
     */
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
