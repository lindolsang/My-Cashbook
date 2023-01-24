package kr.lindol.mycashbook.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;

import kr.lindol.mycashbook.data.db.CashLog;

public interface CashLogDataSource {
    interface LoadCashLogCallback {
        void onCashLogLoaded(List<CashLog> cashLogs);

        void onDataNotAvailable();
    }

    interface OperationCallback {
        void onFinished();

        void onError();
    }

    interface GetBalanceForDayCallback {
        void onBalanceLoaded(long monthlyIncome, long monthlyExpenses, long monthlyBalance, long dailyExpenses);

        void onError();
    }

    interface GetBalanceCallback {
        void onBalanceLoaded(long income, long expense, long balance);

        void onError();
    }

    void loadByDate(@NonNull Date date, @NonNull LoadCashLogCallback callback);

    void loadByMonth(@NonNull Date date, @NonNull LoadCashLogCallback callback);

    void loadByDateRange(@NonNull Date from, @NonNull Date to,
                         @NonNull LoadCashLogCallback callback);

    void save(@NonNull CashLog log, @Nullable OperationCallback callback);

    void delete(@NonNull List<CashLog> logs, @Nullable OperationCallback callback);

    void balance(@NonNull Date date, @NonNull GetBalanceForDayCallback callback);

    void balanceByMonth(@NonNull Date date, @NonNull GetBalanceCallback callback);

    void balanceByDateRange(@NonNull Date from,
                            @NonNull Date to,
                            @NonNull GetBalanceCallback callback);
}
