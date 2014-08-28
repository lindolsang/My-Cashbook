package kr.lindol.mycashbook;

/**
 *  This class is value object for listview.
 *   
 * @author lindol
 *
 */

public class CashLogItem {
	private String tag = null;
	private int price = 0;

	public CashLogItem(String tag, int price) {
		this.tag = tag;
		this.price = price;
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

}