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

        void showSelectionBox();

        void hideSelectionBox();

        void showDeleteButton();

        void hideDeleteButton();

        void showMemo(int id);

        void hideMemo(int id);

        void showAddLog(@NonNull Date date);

        void showCalendar(@NonNull Date date);
    }

    interface Presenter extends BasePresenter {
        void yesterday();

        void today();

        void tomorrow();

        void setToDate(@NonNull Date date);

        void addLog();

        void openOptions();

        void openCalendar();

        void selectCashLog(int id);
    }
}
