package kr.lindol.mycashbook.list;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.lindol.mycashbook.R;

public class CashLogItemView extends LinearLayout {
    private TextView mTitle;
    private TextView mAmount;
    private LinearLayout mLayoutCheckbox;
    private CheckBox mCheck;
    private TextView mMemo;
    private LinearLayout mLayoutLogDate;
    private TextView mDate;
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd (E)");

    public CashLogItemView(Context context) {
        super(context);

        initView(context);
    }

    public CashLogItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cash_log_list_item, this, true);

        mTitle = findViewById(R.id.textView_list_title);
        mAmount = findViewById(R.id.textView_list_amount);
        mCheck = findViewById(R.id.checkBox_list_item);
        mMemo = findViewById(R.id.textView_list_memo);
        mLayoutCheckbox = findViewById(R.id.layout_check);
        mLayoutLogDate = findViewById(R.id.layout_log_date);
        mDate = findViewById(R.id.textView_log_date_text);
    }

    public void setTitle(@Nullable String title) {
        mTitle.setText(title);
    }

    public void setAmount(@Nullable String amount) {
        mAmount.setText(amount);
    }

    public void setCheck(boolean checked) {
        mCheck.setChecked(checked);
    }

    public void showCheckBox(boolean isShow) {
        mLayoutCheckbox.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setMemo(@Nullable String memo) {
        mMemo.setText(memo);
    }

    public void showMemo(boolean isShow) {
        mMemo.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setAmountColor(int color) {
        mAmount.setTextColor(color);
    }

    public void showDate(boolean isShow) {
        mLayoutLogDate.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setDate(@NonNull Date date) {
        checkNotNull(date, "date can not be null");

        mDate.setText(mDateFormat.format(date));
    }
}