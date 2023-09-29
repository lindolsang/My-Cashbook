package kr.lindol.mycashbook.data.db;

public enum CashType {
    INCOME(0),
    EXPENSE(1);

    private int v;

    CashType(int v) {
        this.v = v;
    }

    public int getValue() {
        return v;
    }
}
