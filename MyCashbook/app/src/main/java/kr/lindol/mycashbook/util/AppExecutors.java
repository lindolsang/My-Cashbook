package kr.lindol.mycashbook.util;

import static com.google.common.base.Preconditions.checkNotNull;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

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

    protected AppExecutors(@NonNull Executor diskIo, @NonNull Executor mainThread) {
        checkNotNull(diskIo, "diskIo can not be null");
        checkNotNull(mainThread, "mainThread can not be null");

        this.mDiskIo = diskIo;
        this.mMainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        // TODO: 2021/12/13 need to improve for singleton
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
