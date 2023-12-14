package kr.lindol.mycashbook.add;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;

import kr.lindol.mycashbook.BasePresenter;
import kr.lindol.mycashbook.BaseView;

public interface AddContract {
    interface View extends BaseView<Presenter> {
        void clearForms();

        void showSuccess();

        void showSuccessWithEdit();

        void showFailure();

        void showItemValueEmptyError();

        void showAmountValueEmptyError();

        void showAmountValueSmallError();

        void showAmountValueFormatError();

        void showCalendar(@NonNull Date date);

        void showDate(@NonNull Date date);

        void showItem(@NonNull String item);

        void showAmount(int amount);

        void showMemo(@NonNull String memo);

        void closeWindow();
    }

    interface Presenter extends BasePresenter {
        void addAsIncome(@NonNull String item,
                         @NonNull String amount,
                         @Nullable String description);

        void addAsExpense(@NonNull String item,
                          @NonNull String amount,
                          @Nullable String description);

        void selectDate();

        void setToDate(@NonNull Date date);
    }
}
