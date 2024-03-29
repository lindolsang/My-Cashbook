package kr.lindol.mycashbook.data;

import static com.google.common.base.Preconditions.checkNotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import kr.lindol.mycashbook.data.db.CashLog;
import kr.lindol.mycashbook.data.db.CashLogDao;
import kr.lindol.mycashbook.util.AppExecutors;

public class CashLogRepository implements CashLogDataSource {

    private CashLogDao mDao;
    private AppExecutors mExecutors;
    private SimpleDateFormat mSfDate = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat mSfMonth = new SimpleDateFormat("yyyyMM");

    public CashLogRepository(@NonNull CashLogDao dao, @NonNull AppExecutors executors) {
        mDao = checkNotNull(dao, "dao cannot be null");
        mExecutors = checkNotNull(executors, "executors cannot be null");
    }

    @Override
    public void loadByDate(@NonNull Date date, @NonNull LoadCashLogCallback callback) {
        checkNotNull(date, "date cannot be null");
        checkNotNull(callback, "callback cannot be null");

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        final String searchDate = sf.format(date);

        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                List<CashLog> result = mDao.loadByDate(searchDate);

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
    public void loadByMonth(@NonNull Date date, @NonNull LoadCashLogCallback callback) {
        checkNotNull(date, "date can not be null");
        checkNotNull(callback, "callback can not be null");

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
        final String searchMonth = sf.format(date);

        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                List<CashLog> result = mDao.loadByMonth(searchMonth);

                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (result.size() > 0) {
                            callback.onCashLogLoaded(result);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void loadByDateRange(@NonNull Date from, @NonNull Date to,
                                @NonNull LoadCashLogCallback callback) {
        checkNotNull(from, "from can not be null");
        checkNotNull(to, "to can not be null");
        checkNotNull(callback, "callback can not be null");

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        int dateFrom = Integer.parseInt(sf.format(from));
        int dateTo = Integer.parseInt(sf.format(to));
        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                List<CashLog> result = mDao.loadByDateRange(dateFrom, dateTo);

                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (result.size() > 0) {
                            callback.onCashLogLoaded(result);
                        } else {
                            callback.onDataNotAvailable();
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

    @Override
    public void update(@NonNull CashLog log, @Nullable OperationCallback callback) {
        checkNotNull(log, "log cannot be null");

        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                boolean success;
                try {
                    mDao.updateAll(log);
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

    @Override
    public void delete(@NonNull List<CashLog> logs, @Nullable OperationCallback callback) {
        checkNotNull(logs, "logs cannot be null");

        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                boolean success;
                try {
                    for (CashLog log : logs) {
                        mDao.delete(log);
                    }
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

    @Override
    public void balance(@NonNull Date date, @NonNull GetBalanceForDayCallback callback) {
        checkNotNull(date, "date cannot be null");
        checkNotNull(callback, "callback cannot be null");

        String tempDate = mSfDate.format(date);
        String tempMonth = mSfMonth.format(date);

        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                long incomeMonth = mDao.sumOnMonth(tempMonth, 0);
                long outlayMonth = mDao.sumOnMonth(tempMonth, 1);
                long outlayToday = mDao.sumOnDate(tempDate, 1);
                long balance = incomeMonth - outlayMonth;

                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onBalanceLoaded(incomeMonth, outlayMonth, balance, outlayToday);
                    }
                });
            }
        });
    }

    @Override
    public void balanceByMonth(@NonNull Date date, @NonNull GetBalanceCallback callback) {
        checkNotNull(date, "date can not be null");
        checkNotNull(callback, "callback can not be null");

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");

        String monthTag = sf.format(date);
        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                long incomeMonth = mDao.sumOnMonth(monthTag, 0);
                long outlayMonth = mDao.sumOnMonth(monthTag, 1);
                long balance = incomeMonth - outlayMonth;

                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onBalanceLoaded(incomeMonth, outlayMonth, balance);
                    }
                });
            }
        });
    }

    @Override
    public void balanceByDateRange(@NonNull Date from, @NonNull Date to,
                                   @NonNull GetBalanceCallback callback) {
        checkNotNull(from, "from can not be null");
        checkNotNull(to, "to can not be null");
        checkNotNull(callback, "callback can not be null");

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        int dateFrom = Integer.parseInt(sf.format(from));
        int dateTo = Integer.parseInt(sf.format(to));
        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                long income = mDao.sumOnDateRange(dateFrom, dateTo, 0);
                long outlay = mDao.sumOnDateRange(dateFrom, dateTo, 1);
                long balance = income - outlay;

                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onBalanceLoaded(income, outlay, balance);
                    }
                });
            }
        });
    }

    @Override
    public void loadById(int logId, @NonNull LoadSingleCashLogCallback callback) {
        checkNotNull(callback, "callback can not be null");

        mExecutors.diskIo().execute(new Runnable() {
            @Override
            public void run() {
                CashLog log = mDao.loadById(logId);

                mExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (log == null) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onCashLogLoaded(log);
                        }
                    }
                });
            }
        });
    }
}
