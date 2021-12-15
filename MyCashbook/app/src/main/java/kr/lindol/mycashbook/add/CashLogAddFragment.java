package kr.lindol.mycashbook.add;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Date;

import kr.lindol.mycashbook.R;

public class CashLogAddFragment extends Fragment implements AddContract.View {
    private static final String TAG = "CashLogAddFragment";

    private static final String ARG_DATE = "arg_date";

    private Date mDate;
    private AddContract.Presenter mPresenter;

    private TextView mEditTextItem;
    private TextView mEditTextAmount;
    private TextView mEditTextDescription;

    public static CashLogAddFragment newInstance(@NonNull Date date) {
        checkNotNull(date, "date cannot be null");

        CashLogAddFragment fragment = new CashLogAddFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_DATE, date.getTime());
        fragment.setArguments(args);

        return fragment;
    }

    public CashLogAddFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mDate = new Date(getArguments().getLong(ARG_DATE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_log_add, container, false);

        mEditTextItem = view.findViewById(R.id.editText_item);
        mEditTextAmount = view.findViewById(R.id.editText_amount);
        mEditTextDescription = view.findViewById(R.id.editText_description);

        Button inputButtonAsIncome = view.findViewById(R.id.button_inputAsIncome);
        inputButtonAsIncome.setOnClickListener((v) -> {
                    String item = mEditTextItem.getText().toString().trim();
                    String amount = mEditTextAmount.getText().toString().trim();
                    String description = mEditTextDescription.getText().toString().trim();

                    mPresenter.addAsIncome(item, amount, mDate, description);
                }
        );
        Button inputButtonAsOutlay = view.findViewById(R.id.button_inputAsExpense);
        inputButtonAsOutlay.setOnClickListener((v) -> {
                    String item = mEditTextItem.getText().toString().trim();
                    String amount = mEditTextAmount.getText().toString().trim();
                    String description = mEditTextDescription.getText().toString().trim();

                    mPresenter.addAsOutlay(item, amount, mDate, description);
                }
        );

        Button closeButton = view.findViewById(R.id.button_close);
        closeButton.setOnClickListener((v) -> {
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
        );

        return view;
    }

    @Override
    public void setPresenter(AddContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter, "presenter cannot be null");
    }

    @Override
    public void clearForms() {
        mEditTextItem.setText("");
        mEditTextAmount.setText("");
        mEditTextDescription.setText("");
    }

    @Override
    public void showSuccess() {
        Log.i(TAG, "CashLog was added");
        Toast.makeText(getContext(), R.string.added_item, Toast.LENGTH_SHORT).show();

        mEditTextItem.requestFocus();
    }

    @Override
    public void showFailure() {
        Log.w(TAG, "showFailure()");
    }

    @Override
    public void showItemValueEmptyError() {
        Log.d(TAG, "Error: Item value is empty");
        Toast.makeText(getContext(), R.string.error_item_empty, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAmountValueEmptyError() {
        Log.d(TAG, "Error: Amount value is empty");
        Toast.makeText(getContext(), R.string.error_amount_empty, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAmountValueSmallError() {
        Log.d(TAG, "Error: Amount value is small");
        Toast.makeText(getContext(), R.string.error_amount_small, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAmountValueFormatError() {
        Log.d(TAG, "Error: amount value has non number char");
        Toast.makeText(getContext(), R.string.error_amount_format, Toast.LENGTH_SHORT).show();
    }
}