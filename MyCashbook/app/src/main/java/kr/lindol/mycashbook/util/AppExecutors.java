package kr.lindol.mycashbook.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {
    private Executor mDiskIo;
    private Executor mMainThread;

    public AppExecutors() {
        mDiskIo = Executors.newSingleThreadExecutor();
        mMainThread = new MainThreadExecutor();
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
