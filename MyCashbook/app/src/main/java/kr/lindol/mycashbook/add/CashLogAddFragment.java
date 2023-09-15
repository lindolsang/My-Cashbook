package kr.lindol.mycashbook.add;

import static com.google.common.base.Preconditions.checkNotNull;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.lindol.mycashbook.R;

public class CashLogAddFragment extends Fragment implements AddContract.View {
    private static final String TAG = "CashLogAddFragment";

    private AddContract.Presenter mPresenter;

    private TextView mEditTextItem;
    private TextView mEditTextAmount;
    private TextView mEditTextDescription;

    private TextView mTextViewInputDate;
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd (E)");

    public static CashLogAddFragment newInstance() {
        CashLogAddFragment fragment = new CashLogAddFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cash_log_add, container, false);

        mEditTextItem = view.findViewById(R.id.editText_item);
        mEditTextAmount = view.findViewById(R.id.editText_amount);
        mEditTextDescription = view.findViewById(R.id.editText_description);

        mTextViewInputDate = view.findViewById(R.id.textView_input_date);
        mTextViewInputDate.setOnClickListener(v -> mPresenter.selectDate());

        Button inputButtonAsIncome = view.findViewById(R.id.button_inputAsIncome);
        inputButtonAsIncome.setOnClickListener((v) -> {
                    String item = mEditTextItem.getText().toString().trim();
                    String amount = mEditTextAmount.getText().toString().trim();
                    String description = mEditTextDescription.getText().toString().trim();

                    mPresenter.addAsIncome(item, amount, description);
                }
        );

        Button inputButtonAsOutlay = view.findViewById(R.id.button_inputAsExpense);
        inputButtonAsOutlay.setOnClickListener((v) -> {
                    String item = mEditTextItem.getText().toString().trim();
                    String amount = mEditTextAmount.getText().toString().trim();
                    String description = mEditTextDescription.getText().toString().trim();

                    mPresenter.addAsExpense(item, amount, description);
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
    public void setPresenter(@NonNull AddContract.Presenter presenter) {
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
    public void onResume() {
        super.onResume();

        mPresenter.start();
    }

    @Override
    public void showDate(@NonNull Date date) {
        checkNotNull(date, "date can not be null");

        mTextViewInputDate.setText(mDateFormat.format(date));
    }
}