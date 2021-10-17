package kr.lindol.mycashbook.list;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;

import kr.lindol.mycashbook.data.db.CashLog;

public class CashLogItem {
    private final CashLog mLog;
    private boolean mShowMemo;

    public CashLogItem(@NonNull CashLog log) {
        mLog = checkNotNull(log, "log cannot be null");
    }

    public String getTitle() {
        return mLog.item;
    }

    public int getAmount() {
        return mLog.amount;
    }

    public void setShowMemo(boolean isShow) {
        mShowMemo = isShow;
    }

    public boolean isShowMemo() {
        return mShowMemo;
    }

    public String getMemo() {
        return mLog.description;
    }

    public int getType() {
        return mLog.type;
    }
}
