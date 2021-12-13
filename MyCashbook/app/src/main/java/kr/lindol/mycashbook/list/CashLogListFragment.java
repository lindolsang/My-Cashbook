package kr.lindol.mycashbook.list;

import static com.google.common.base.Preconditions.checkNotNull;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private TextView mTextViewDailyExpenses;
    private TextView mTextViewTitleMonthlyIncome;
    private TextView mTextViewMonthlyIncome;
    private TextView mTextViewTitleMonthlyExpenses;
    private TextView mTextViewMonthlyExpenses;
    private TextView mTextViewTitleMonthlyBalance;
    private TextView mTextViewMonthlyBalance;

    private Button mButtonDelete;

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

        mTextViewCurrentDate = fragment.findViewById(R.id.textView_currentDate);
        mTextViewCurrentDate.setOnClickListener((v) -> mPresenter.selectDate());

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

        Button buttonYesterday = fragment.findViewById(R.id.button_yesterday);
        buttonYesterday.setOnClickListener((v) -> {
            Log.d(TAG, "Yesterday button");

            mPresenter.yesterday();
        });

        Button buttonToday = fragment.findViewById(R.id.button_today);
        buttonToday.setOnClickListener((v) -> {
            Log.d(TAG, "Today button");

            mPresenter.today();
        });

        Button buttonTomorrow = fragment.findViewById(R.id.button_tomorrow);
        buttonTomorrow.setOnClickListener((v) -> {
            Log.d(TAG, "Tomorrow button");

            mPresenter.tomorrow();
        });

        Button buttonWrite = fragment.findViewById(R.id.button_write);
        buttonWrite.setOnClickListener((v) -> mPresenter.addLog());

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
                    // TODO: improve to reload when data was added only.
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

        mTextViewDailyExpenses.setText(mAmountFormat.format(dailyExpenses));
        mTextViewTitleMonthlyIncome.setText(getString(R.string.balance_title_monthly_income, month));
        mTextViewMonthlyIncome.setText(mAmountFormat.format(monthlyIncome));
        mTextViewTitleMonthlyExpenses.setText(getString(R.string.balance_title_monthly_expenses, month));
        mTextViewMonthlyExpenses.setText(mAmountFormat.format(monthlyExpenses));
        mTextViewTitleMonthlyBalance.setText(getString(R.string.balance_title_monthly_balance, month));
        mTextViewMonthlyBalance.setText(mAmountFormat.format(monthlyBalance));
    }

    @Override
    public void showErrorBalanceLoad() {
        Log.w(TAG, "Can't show state");
    }

    private class CashLogListAdapter extends BaseAdapter {

        private final List<CashLogItem> mCashLogs = new ArrayList<>();
        private final Context mContext;

        public CashLogListAdapter(@NonNull Context context) {
            mContext = checkNotNull(context, "context cannot be null");
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

            int coloIncome = getResources().getColor(R.color.list_income);
            int colorExpense = getResources().getColor(R.color.list_expense);
            itemView.setAmountColor(mCashLogs.get(position).getType() == 0 ? coloIncome : colorExpense);

            return itemView;
        }
    }
}