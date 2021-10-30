package kr.lindol.mycashbook.list;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;

import kr.lindol.mycashbook.BasePresenter;
import kr.lindol.mycashbook.BaseView;
import kr.lindol.mycashbook.data.db.CashLog;

public interface ListContract {
    interface View extends BaseView<Presenter> {
        void showList(@NonNull List<CashLog> logs);

        void showNoListData();

        void showDate(@NonNull Date date);

        void showMemo(int id);

        void hideMemo(int id);

        void showAddLog(@NonNull Date date);

        void showCalendar(@NonNull Date date);

        void showSuccessfullyDeletedLog();

        void showErrorDeleteLog();
    }

    interface Presenter extends BasePresenter {
        void yesterday();

        void today();

        void tomorrow();

        void setToDate(@NonNull Date date);

        void addLog();

        void selectDate();

        void selectCashLog(int id);

        void deleteLog(@NonNull List<CashLog> logs);
    }
}
