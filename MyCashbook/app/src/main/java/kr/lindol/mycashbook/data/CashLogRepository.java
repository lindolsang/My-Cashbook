package kr.lindol.mycashbook.data;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Date;
import java.util.List;

import kr.lindol.mycashbook.data.db.CashLog;
import kr.lindol.mycashbook.data.db.CashLogDao;
import kr.lindol.mycashbook.util.AppExecutors;

public class CashLogRepository implements CashLogDataSource {

    private CashLogDao mDao;
    private AppExecutors mExecutors;

    public CashLogRepository(@NonNull CashLogDao dao, @NonNull AppExecutors executors) {
        mDao = checkNotNull(dao, "dao cannot be null");
        mExecutors = checkNotNull(executors, "executors cannot be null");
    }

    @Override
    public void loadByDate(@NonNull Date date, @Nullable LoadCashLogCallback callback) {
        checkNotNull(date, "date cannot be null");

        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                //TODO 2021/09/23 need to update
                List<CashLog> result = mDao.loadByDate("202001");

                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            if (result.size() > 0) {
                                callback.onCashLogLoaded(result);
                            } else {
                                callback.onDataNotAvailable();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void save(@NonNull CashLog log, @Nullable OperationCallback callback) {
        checkNotNull(log, "log cannot be null");

        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                boolean success;
                try {
                    mDao.insertAll(log);
                    success = true;
                } catch (Exception e) {
                    success = false;
                }

                final boolean opSuccess = success;
                if (callback != null) {
                    mExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (opSuccess) {
                                callback.onFinished();
                            } else {
                                callback.onError();
                            }
                        }
                    });
                }
            }
        });
    }
}
