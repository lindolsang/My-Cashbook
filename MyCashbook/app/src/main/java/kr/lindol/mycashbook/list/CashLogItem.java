package kr.lindol.mycashbook.list;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;

import java.util.Date;

import kr.lindol.mycashbook.data.db.CashLog;
import kr.lindol.mycashbook.data.db.CashType;
import kr.lindol.mycashbook.util.DateUtils;

public class CashLogItem {
    private final CashLog mLog;
    private boolean mShowMemo;
    private boolean mChecked;
    private Date mDate;

    public CashLogItem(@NonNull CashLog log) {
        mLog = checkNotNull(log, "log cannot be null");
        mDate = DateUtils.fromStr(log.dayTag);
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

    public CashType getType() {
        return mLog.type;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    public CashLog getLog() {
        return mLog;
    }

    public Date getDate() {
        return mDate;
    }

    public int getDateTag() {
        return mLog.dateTag;
    }
}
