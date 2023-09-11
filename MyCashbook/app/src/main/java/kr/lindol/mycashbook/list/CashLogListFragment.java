package kr.lindol.mycashbook.list;

import static com.google.common.base.Preconditions.checkNotNull;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.lindol.mycashbook.R;
import kr.lindol.mycashbook.add.CashLogAddActivity;
import kr.lindol.mycashbook.data.db.CashLog;

public class CashLogListFragment extends Fragment implements ListContract.View {
    private static final String TAG = "CashLogListFragment";

    private ListContract.Presenter mPresenter;
    private CashLogListAdapter mListAdapter;
    private boolean mShowSelection = false;

    private TextView mTextViewCurrentDate;
    private TextView mTextViewFromDate;
    private TextView mTextViewToDate;
    private TextView mTextViewDailyExpenses;
    private TextView mTextViewTitleMonthlyIncome;
    private TextView mTextViewMonthlyIncome;
    private TextView mTextViewTitleMonthlyExpenses;
    private TextView mTextViewMonthlyExpenses;
    private TextView mTextViewTitleMonthlyBalance;
    private TextView mTextViewMonthlyBalance;

    private LinearLayout mLayoutCurrentDate;
    private LinearLayout mLayoutCurrentDateRange;

    private LinearLayout mLayoutDateStatus;

    private Button mButtonDelete;

    private Button mButtonYesterday;
    private Button mButtonToday;
    private Button mButtonTomorrow;

    private TextView mTextViewTabByDate;
    private TextView mTextViewTabByMonth;
    private TextView mTextViewTabByDateRange;

    private final DecimalFormat mAmountFormat = new DecimalFormat("###,###");
    private final SimpleDateFormat mBalanceTitleDateFormat = new SimpleDateFormat("MMM");

    public CashLogListFragment() {
    }

    public static CashLogListFragment newInstance() {
        return new CashLogListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_cash_log_list, container, false);

        mTextViewDailyExpenses = fragment.findViewById(R.id.textView_daily_expense);
        mTextViewTitleMonthlyIncome = fragment.findViewById(R.id.textView_title_monthly_income);
        mTextViewMonthlyIncome = fragment.findViewById(R.id.textView_monthly_income);
        mTextViewTitleMonthlyExpenses = fragment.findViewById(R.id.textView_title_monthly_expenses);
        mTextViewMonthlyExpenses = fragment.findViewById(R.id.textView_monthly_expenses);
        mTextViewTitleMonthlyBalance = fragment.findViewById(R.id.textView_title_monthly_balance);
        mTextViewMonthlyBalance = fragment.findViewById(R.id.textView_monthly_balance);

        mLayoutCurrentDate = fragment.findViewById(R.id.layout_current_date);
        mLayoutCurrentDateRange = fragment.findViewById(R.id.layout_current_date_range);
        mLayoutDateStatus = fragment.findViewById(R.id.layout_date_status);

        mTextViewCurrentDate = fragment.findViewById(R.id.textView_currentDate);
        mTextViewCurrentDate.setOnClickListener((v) -> {
            if (mPresenter.getListType() == ListType.FOR_DATE) {
                mPresenter.selectDate();
            } else {
                mPresenter.selectMonth();
            }
        });

        mTextViewFromDate = fragment.findViewById(R.id.textView_from_currentDate);
        mTextViewFromDate.setOnClickListener((v) -> mPresenter.selectFromDate());

        mTextViewToDate = fragment.findViewById(R.id.textView_to_currentDate);
        mTextViewToDate.setOnClickListener(((v) -> mPresenter.selectToDate()));

        mTextViewTabByDate = fragment.findViewById(R.id.textView_tabByDate);
        mTextViewTabByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setListType(ListType.FOR_DATE);
                mPresenter.reload();
            }
        });
        mTextViewTabByMonth = fragment.findViewById(R.id.textView_tabByMonth);
        mTextViewTabByMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setListType(ListType.FOR_MONTH);
                mPresenter.reload();
            }
        });
        mTextViewTabByDateRange = fragment.findViewById(R.id.textView_tabByDateRange);
        mTextViewTabByDateRange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setListType(ListType.FOR_DATE_RANGE);
                mPresenter.reload();
            }
        });

        mButtonDelete = fragment.findViewById(R.id.button_delete);
        mButtonDelete.setOnClickListener((v) -> {
            Log.d(TAG, "Delete button");
            confirmDeleteItem();
        });

        Button mButtonView = fragment.findViewById(R.id.button_view);
        mButtonView.setOnClickListener((v) -> {
            Log.d(TAG, "View button");
            selectOption();
        });

        mListAdapter = new CashLogListAdapter(getContext());
        ListView listView = fragment.findViewById(R.id.listView_cashlog);
        listView.setAdapter(mListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mShowSelection) {
                    CashLogItem item = (CashLogItem) mListAdapter.getItem(position);
                    item.setChecked(!item.isChecked());
                    mListAdapter.notifyDataSetChanged();
                } else {
                    mPresenter.selectCashLog((int) id);
                }
            }
        });

        mButtonYesterday = fragment.findViewById(R.id.button_yesterday);
        mButtonYesterday.setOnClickListener((v) -> {
            Log.d(TAG, "Yesterday button");

            mPresenter.previous();
        });

        mButtonToday = fragment.findViewById(R.id.button_today);
        mButtonToday.setOnClickListener((v) -> {
            Log.d(TAG, "Today button");

            mPresenter.today();
        });

        mButtonTomorrow = fragment.findViewById(R.id.button_tomorrow);
        mButtonTomorrow.setOnClickListener((v) -> {
            Log.d(TAG, "Tomorrow button");

            mPresenter.next();
        });

        Button buttonInput = fragment.findViewById(R.id.button_input);
        buttonInput.setOnClickListener((v) -> mPresenter.addLog());

        Button buttonClose = fragment.findViewById(R.id.button_close);
        buttonClose.setOnClickListener((v) -> {
            Log.d(TAG, "Close button");
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return fragment;
    }

    @Override
    public void setPresenter(@NonNull ListContract.Presenter presenter) {
        checkNotNull(presenter);

        mPresenter = presenter;
    }

    @Override
    public void showList(@NonNull List<CashLog> logs) {
        checkNotNull(logs, "logs cannot be null");

        hideOption();

        Log.i(TAG, "Show list: " + logs.size());

        mListAdapter.clear();
        for (CashLog log : logs) {
            mListAdapter.add(new CashLogItem(log));
        }

        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoListData() {
        Log.i(TAG, "Show list: No data");

        hideOption();

        mListAdapter.clear();
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDate(@NonNull Date date) {
        checkNotNull(date, "date cannot be null");

        Log.d(TAG, "Show date: " + date);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd (E)");
        mTextViewCurrentDate.setText(sf.format(date));

    }

    private void showSelectionBox() {
        Log.d(TAG, "Show selectionBox");
        mShowSelection = true;
    }

    private void hideSelectionBox() {
        Log.d(TAG, "Hide SelectionBox");
        mShowSelection = false;
    }

    private void showDeleteButton() {
        Log.d(TAG, "Show DeleteButton");
        mButtonDelete.setVisibility(View.VISIBLE);
    }

    private void hideDeleteButton() {
        Log.d(TAG, "Hide DeleteButton");
        mButtonDelete.setVisibility(View.GONE);
    }

    private void confirmDeleteItem() {
        int numChecked = mListAdapter.getCheckedItems().size();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Resources res = getResources();
        builder.setMessage(res.getString(R.string.dialog_delete_message, numChecked))
                .setTitle(R.string.dialog_delete_title)
                .setPositiveButton(R.string.button_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "delete button");

                        deleteItem();
                    }
                })
                .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "cancel button");
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteItem() {
        List<CashLogItem> items = mListAdapter.getCheckedItems();

        List<CashLog> deleteLogs = new ArrayList<>();
        for (CashLogItem item : items) {
            deleteLogs.add(item.getLog());
        }

        mPresenter.deleteLog(deleteLogs);
    }

    private void selectOption() {
        if (!mShowSelection) {
            showDeleteButton();
            showSelectionBox();
        } else {
            hideDeleteButton();
            hideSelectionBox();
        }
        mListAdapter.notifyDataSetChanged();
    }

    private void hideOption() {
        hideSelectionBox();
        hideDeleteButton();
    }

    @Override
    public void showMemo(int id) {
        CashLogItem item = (CashLogItem) mListAdapter.getItem(id);
        item.setShowMemo(true);
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideMemo(int id) {
        CashLogItem item = (CashLogItem) mListAdapter.getItem(id);
        item.setShowMemo(false);
        mListAdapter.notifyDataSetChanged();
    }

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // TODO 2023-08-16 (improve) needs to reload when data was added only.
                }
            });

    @Override
    public void showAddLog(@NonNull Date date) {
        Intent i = new Intent(getActivity(), CashLogAddActivity.class);
        i.putExtra(CashLogAddActivity.EXTRA_DATE, date.getTime());

        mStartForResult.launch(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void showCalendar(@NonNull Date date) {
        checkNotNull(date, "date cannot be null");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar setCal = Calendar.getInstance();
                setCal.set(year, month, dayOfMonth);

                mPresenter.setToDate(setCal.getTime());
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    //TODO 2023-08-27 (improve) needs to improve to pick Year and Month
    @Override
    public void showCalendarForMonth(@NonNull Date date) {
        checkNotNull(date, "date can not be null");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar setCal = Calendar.getInstance();
                setCal.set(year, month, dayOfMonth);

                mPresenter.setToDate(setCal.getTime());
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    public void showCalendarForFromDate(@NonNull Date fromDate, @NonNull Date toDate) {
        checkNotNull(fromDate, "fromDate can not be null");
        checkNotNull(toDate, "toDate can not be null");

        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar setCal = Calendar.getInstance();
                setCal.set(year, month, dayOfMonth);

                mPresenter.setToDateRange(setCal.getTime(), toDate);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    public void showCalendarForToDate(@NonNull Date fromDate, @NonNull Date toDate) {
        checkNotNull(fromDate, "fromDate can not be null");
        checkNotNull(toDate, "toDate can not be null");

        Calendar cal = Calendar.getInstance();
        cal.setTime(toDate);

        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar setCal = Calendar.getInstance();
                setCal.set(year, month, dayOfMonth);

                mPresenter.setToDateRange(fromDate, setCal.getTime());
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Override
    public void showSuccessfullyDeletedLog() {
        Log.d(TAG, "successfully deleted log");
    }

    @Override
    public void showErrorDeleteLog() {
        Log.d(TAG, "Error delete log");
    }

    @Override
    public void showBalance(@NonNull Date date, long monthlyIncome, long monthlyExpenses, long monthlyBalance, long dailyExpenses) {
        checkNotNull(date, "date cannot be null");

        String month = mBalanceTitleDateFormat.format(date);

        mLayoutDateStatus.setVisibility(View.VISIBLE);

        mTextViewDailyExpenses.setText(mAmountFormat.format(dailyExpenses));
        mTextViewTitleMonthlyIncome.setText(getString(R.string.balance_title_monthly_income, month));
        mTextViewMonthlyIncome.setText(mAmountFormat.format(monthlyIncome));
        mTextViewTitleMonthlyExpenses.setText(getString(R.string.balance_title_monthly_expenses, month));
        mTextViewMonthlyExpenses.setText(mAmountFormat.format(monthlyExpenses));
        mTextViewTitleMonthlyBalance.setText(getString(R.string.balance_title_monthly_balance, month));
        mTextViewMonthlyBalance.setText(mAmountFormat.format(monthlyBalance));
    }

    @Override
    public void showBalanceForMonth(@NonNull Date date, long income, long expense, long balance) {
        checkNotNull(date, "date can not be null");

        String month = mBalanceTitleDateFormat.format(date);

        mLayoutDateStatus.setVisibility(View.GONE);

        mTextViewTitleMonthlyIncome.setText(getString(R.string.balance_title_monthly_income, month));
        mTextViewMonthlyIncome.setText(mAmountFormat.format(income));
        mTextViewTitleMonthlyExpenses.setText(getString(R.string.balance_title_monthly_expenses, month));
        mTextViewMonthlyExpenses.setText(mAmountFormat.format(expense));
        mTextViewTitleMonthlyBalance.setText(getString(R.string.balance_title_monthly_balance, month));
        mTextViewMonthlyBalance.setText(mAmountFormat.format(balance));
    }

    @Override
    public void showBalanceForDateRange(@NonNull Date from, @NonNull Date to, long income, long expense, long balance) {
        checkNotNull(from, "from can not be null");
        checkNotNull(to, "to can not be null");

        mLayoutDateStatus.setVisibility(View.GONE);

        mTextViewTitleMonthlyIncome.setText(getString(R.string.balance_title_income));
        mTextViewMonthlyIncome.setText(mAmountFormat.format(income));
        mTextViewTitleMonthlyExpenses.setText(getString(R.string.balance_title_expenses));
        mTextViewMonthlyExpenses.setText(mAmountFormat.format(expense));
        mTextViewTitleMonthlyBalance.setText(getString(R.string.balance_title_balance));
        mTextViewMonthlyBalance.setText(mAmountFormat.format(balance));
    }

    @Override
    public void showErrorBalanceLoad() {
        Log.w(TAG, "Can't show state");
    }

    @Override
    public void showListType(@NonNull ListType type) {
        checkNotNull(type, "type can not be null");

        int selectedColor = getResources().getColor(R.color.list_tab_selected);
        switch (type) {
            case FOR_DATE:
                mLayoutCurrentDate.setVisibility(View.VISIBLE);
                mLayoutCurrentDateRange.setVisibility(View.GONE);

                mTextViewTabByDate.setBackgroundColor(selectedColor);
                mTextViewTabByMonth.setBackground(null);
                mTextViewTabByDateRange.setBackground(null);

                mButtonYesterday.setText(R.string.button_navi_yesterday);
                mButtonToday.setText(R.string.button_navi_today);
                mButtonTomorrow.setText(R.string.button_navi_tomorrow);
                break;
            case FOR_MONTH:
                mLayoutCurrentDate.setVisibility(View.VISIBLE);
                mLayoutCurrentDateRange.setVisibility(View.GONE);

                mTextViewTabByDate.setBackground(null);
                mTextViewTabByMonth.setBackgroundColor(selectedColor);
                mTextViewTabByDateRange.setBackground(null);

                mButtonYesterday.setText(R.string.button_navi_last_month);
                mButtonToday.setText(R.string.button_navi_this_month);
                mButtonTomorrow.setText(R.string.button_navi_next_month);
                break;
            case FOR_DATE_RANGE:
                mLayoutCurrentDate.setVisibility(View.GONE);
                mLayoutCurrentDateRange.setVisibility(View.VISIBLE);

                mTextViewTabByDate.setBackground(null);
                mTextViewTabByMonth.setBackground(null);
                mTextViewTabByDateRange.setBackgroundColor(selectedColor);

                mButtonYesterday.setText(R.string.button_navi_previous);
                mButtonToday.setText(R.string.button_navi_current);
                mButtonTomorrow.setText(R.string.button_navi_next);
                break;
        }
    }

    @Override
    public void showMonth(@NonNull Date date) {
        checkNotNull(date, "date can not be null");

        Log.d(TAG, "showMonth date = " + date);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        mTextViewCurrentDate.setText(sf.format(date));
    }

    @Override
    public void showDateRange(@NonNull Date from, @NonNull Date to) {
        checkNotNull(from, "from can not be null");
        checkNotNull(to, "to can not be null");

        Log.d(TAG, "showDateRange from = " + from);
        Log.d(TAG, "showDateRange to = " + to);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd (E)");
        mTextViewFromDate.setText(sf.format(from));
        mTextViewToDate.setText(sf.format(to));
    }

    private class CashLogListAdapter extends BaseAdapter {

        private final List<CashLogItem> mCashLogs = new ArrayList<>();
        private final Context mContext;

        private final int mColorIncome;
        private final int mColorExpense;

        public CashLogListAdapter(@NonNull Context context) {
            mContext = checkNotNull(context, "context cannot be null");

            mColorIncome = getResourceColor(R.color.list_income);
            mColorExpense = getResourceColor(R.color.list_expense);
        }

        public void add(CashLogItem log) {
            mCashLogs.add(log);
        }

        public void clear() {
            mCashLogs.clear();
        }

        public List<CashLogItem> getCheckedItems() {
            List<CashLogItem> temp = new ArrayList<>();

            for (CashLogItem item : mCashLogs) {
                if (item.isChecked()) {
                    temp.add(item);
                }
            }

            return temp;
        }

        @Override
        public int getCount() {
            return mCashLogs.size();
        }

        @Override
        public Object getItem(int position) {
            return mCashLogs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CashLogItemView itemView = (CashLogItemView) convertView;
            if (itemView == null) {
                itemView = new CashLogItemView(mContext);
            }

            itemView.setTitle(mCashLogs.get(position).getTitle());
            itemView.setAmount(mAmountFormat.format(mCashLogs.get(position).getAmount()));
            itemView.showCheckBox(mShowSelection);
            itemView.setCheck(mCashLogs.get(position).isChecked());
            itemView.showMemo(mCashLogs.get(position).isShowMemo());
            itemView.setMemo(mCashLogs.get(position).getMemo());

            itemView.setAmountColor(mCashLogs.get(position).getType() == 0 ? mColorIncome : mColorExpense);

            return itemView;
        }

        @SuppressWarnings("deprecation")
        private int getResourceColor(@ColorRes int resId) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return getResources().getColor(resId, mContext.getTheme());
            }

            return getResources().getColor(resId);
        }
    }
}