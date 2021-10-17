package kr.lindol.mycashbook.list;

import static com.google.common.base.Preconditions.checkNotNull;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private Button mButtonDelete;

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

        mTextViewCurrentDate = fragment.findViewById(R.id.textView_currentDate);

        mButtonDelete = fragment.findViewById(R.id.button_delete);
        Button mButtonView = fragment.findViewById(R.id.button_view);
        mButtonView.setOnClickListener((v) -> {
            Log.d(TAG, "View button clicked");
            mPresenter.openOptions();
        });

        mListAdapter = new CashLogListAdapter(getContext());
        ListView listView = fragment.findViewById(R.id.listView_cashlog);
        listView.setAdapter(mListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "item click: pos = " + position + ", id = " + id);

                mPresenter.selectCashLog((int) id);
            }
        });

        Button buttonYesterday = fragment.findViewById(R.id.button_yesterday);
        buttonYesterday.setOnClickListener((v) -> {
            Log.d(TAG, "buttonYesterday clicked");

            mPresenter.yesterday();
        });

        Button buttonToday = fragment.findViewById(R.id.button_today);
        buttonToday.setOnClickListener((v) -> {
            Log.d(TAG, "buttonToday clicked");

            mPresenter.today();
        });

        Button buttonTomorrow = fragment.findViewById(R.id.button_tomorrow);
        buttonTomorrow.setOnClickListener((v) -> {
            Log.d(TAG, "buttonTomorrow clicked");

            mPresenter.tomorrow();
        });

        Button buttonInput = fragment.findViewById(R.id.button_input);
        buttonInput.setOnClickListener((v) -> mPresenter.addLog());

        Button buttonClose = fragment.findViewById(R.id.button_close);
        buttonClose.setOnClickListener((v) -> {
            Log.d(TAG, "buttonClose clicked");
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

        Log.d(TAG, "Cash logs size = " + logs.size());
        for (CashLog log : logs) {
            Log.d(TAG, "id = " + log.id + ", item = " + log.item + ", amount = " + log.amount + ", dayTag = " + log.dayTag);
        }

        mListAdapter.clear();
        for (CashLog log : logs) {
            mListAdapter.add(new CashLogItem(log));
        }

        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNoListData() {
        Log.d(TAG, "No datas");
        mListAdapter.clear();
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDate(@NonNull Date date) {
        Log.d(TAG, "showDate()");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd (E)");
        mTextViewCurrentDate.setText(sf.format(date));
    }

    @Override
    public void showSelectionBox() {
        Log.d(TAG, "showSelectionBox");
        mShowSelection = true;
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void hideSelectionBox() {
        Log.d(TAG, "hideSelectionBox");
        mShowSelection = false;
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDeleteButton() {
        Log.d(TAG, "showDeleteButton");
        mButtonDelete.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDeleteButton() {
        Log.d(TAG, "hideDeleteButton");
        mButtonDelete.setVisibility(View.GONE);
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
                    //TODO improve to reload when data was added only.
                    mPresenter.reload();
                }
            });

    @Override
    public void showAddLog(@NonNull Date date) {
        Intent i = new Intent(getActivity(), CashLogAddActivity.class);
        i.putExtra(CashLogAddActivity.EXTRA_DATE, date.getTime());

        mStartForResult.launch(i);
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

        public void addAll(List<CashLogItem> logs) {
            mCashLogs.addAll(logs);
        }

        public void clear() {
            mCashLogs.clear();
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
            itemView.setAmount(String.valueOf(mCashLogs.get(position).getAmount()));
            itemView.showCheckBox(mShowSelection);
            itemView.showMemo(mCashLogs.get(position).isShowMemo());
            itemView.setMemo(mCashLogs.get(position).getMemo());

            int coloIncome = getResources().getColor(R.color.list_income);
            int colorOutlay = getResources().getColor(R.color.list_outlay);
            itemView.setAmountColor(mCashLogs.get(position).getType() == 0 ? coloIncome : colorOutlay);

            return itemView;
        }
    }
}