package kr.lindol.mycashbook.list;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;

import kr.lindol.mycashbook.data.db.CashLog;

public class CashLogItem {
    private CashLog mLog;

    public CashLogItem(@NonNull CashLog log) {
        mLog = checkNotNull(log, "log cannot be null");
    }

    public String getTitle() {
        return mLog.item;
    }

    public int getAmount() {
        return mLog.amount;
    }
}
