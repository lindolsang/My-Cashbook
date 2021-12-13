package kr.lindol.mycashbook.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private Executor mDiskIo;
    private Executor mMainThread;

    private static AppExecutors INSTANCE;

    private AppExecutors() {
        mDiskIo = Executors.newSingleThreadExecutor();
        mMainThread = new MainThreadExecutor();
    }

    public static AppExecutors getInstance() {
        // TODO: need to improve for singleton
        if (INSTANCE == null) {
            INSTANCE = new AppExecutors();
        }

        return INSTANCE;
    }

    public Executor diskIo() {
        return mDiskIo;
    }

    public Executor mainThread() {
        return mMainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mHandler;

        public MainThreadExecutor() {
            mHandler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void execute(Runnable command) {
            mHandler.post(command);
        }
    }
}
