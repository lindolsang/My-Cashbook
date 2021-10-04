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

    void loadByDate(@NonNull Date date, @Nullable LoadCashLogCallback callback);

    void save(@NonNull CashLog log, @Nullable OperationCallback callback);
}
