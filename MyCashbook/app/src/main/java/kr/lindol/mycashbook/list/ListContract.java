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

        void showEditLog(int logId);

        void showCalendar(@NonNull Date date);

        void showCalendarForMonth(@NonNull Date date);

        void showCalendarForFromDate(@NonNull Date fromDate, @NonNull Date toDate);

        void showCalendarForToDate(@NonNull Date fromDate, @NonNull Date toDate);

        void showSuccessfullyDeletedLog();

        void showErrorDeleteLog();

        void showBalance(@NonNull Date date,
                         long monthlyIncome,
                         long monthlyExpenses,
                         long monthlyBalance,
                         long dailyExpenses);

        void showBalanceForMonth(@NonNull Date date,
                                 long income,
                                 long expense,
                                 long balance);

        void showBalanceForDateRange(@NonNull Date from,
                                     @NonNull Date to,
                                     long income,
                                     long expense,
                                     long balance);

        void showErrorBalanceLoad();

        void showListType(@NonNull ListType type);

        void showMonth(@NonNull Date date);

        void showDateRange(@NonNull Date from, @NonNull Date to);
    }

    interface Presenter extends BasePresenter {
        void previous();

        void today();

        void next();

        void setToDate(@NonNull Date date);

        void setToDateRange(@NonNull Date from, @NonNull Date to);

        void addLog();

        void editLog(int logId);

        void selectDate();

        void selectMonth();

        void selectFromDate();

        void selectToDate();

        void selectCashLog(int id);

        void deleteLog(@NonNull List<CashLog> logs);

        void setListType(@NonNull ListType type);

        @NonNull
        ListType getListType();

        /**
         * reload all of data with current options
         */
        void reload();
    }
}
