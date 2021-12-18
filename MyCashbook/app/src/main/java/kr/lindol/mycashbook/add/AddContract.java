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

        void showFailure();

        void showItemValueEmptyError();

        void showAmountValueEmptyError();

        void showAmountValueSmallError();

        void showAmountValueFormatError();
    }

    interface Presenter extends BasePresenter {
        void addAsIncome(@NonNull String item,
                         @NonNull String amount,
                         @NonNull Date date,
                         @Nullable String description);

        void addAsOutlay(@NonNull String item,
                         @NonNull String amount,
                         @NonNull Date date,
                         @Nullable String description);
    }
}
