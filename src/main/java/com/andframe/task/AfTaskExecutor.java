package com.andframe.task;

import android.os.Looper;
import android.support.annotation.MainThread;

import com.andframe.api.TaskExecutor;
import com.andframe.application.AfApp;
import com.andframe.util.java.AfDateGuid;
import com.andframe.util.java.AfReflecter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 任务执行器
 * Created by SCWANG on 2016/9/2.
 */
@SuppressWarnings("unused")
public class AfTaskExecutor implements TaskExecutor {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 1;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            if (r instanceof AfTask) {
                AfTask task = (AfTask) r;
                String name = AfDateGuid.NewID();
                if (AfApp.get().isDebug()) {
                    name = task.getClass().getSimpleName();
                    if (task instanceof AfDataTask || task instanceof AfData2Task || task instanceof AfData3Task) {
                        Object listener = AfReflecter.getMemberNoException(task, "listener");
                        if (listener != null) {
                            name = listener.getClass().getSimpleName();
                        }
                    }
                }
                return new Thread(r, "AfTaskExecutor-" + name + " #" + mCount.getAndIncrement());
            }
            return new Thread(r, "AfTaskExecutor #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<>(128);

    /**
     * An {@link Executor} that can be used to execute tasks in parallel.
     */
    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    private static volatile Executor sDefaultExecutor = THREAD_POOL_EXECUTOR;
    private static AfTaskExecutor instance;

    public static void setDefaultExecutor(Executor exec) {
        sDefaultExecutor = exec;
    }

    public static AfTaskExecutor getInstance() {
        if (instance == null) {
            instance = new AfTaskExecutor();
        }
        return instance;
    }

    /**
     * Convenience version of for use with
     * a simple Runnable object. See for more
     * information on the order of execution.
     */
    @MainThread
    public void execute(Runnable runnable) {
        sDefaultExecutor.execute(runnable);
    }

    @MainThread
    public void execute(AfTask task) {
        if (!task.isCanceled() && task.prepare()) {
            sDefaultExecutor.execute(task);
        }
    }

    public <T extends AfTask> T postTask(T task) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            execute(task);
        } else {
            AfDispatcher.dispatch(() -> execute(task));
        }
        return task;
    }
    /**
     * 抛送带数据任务到Worker执行
     */
    @Override
    public <T> AfDataTask postDataTask(T t, AfDataTask.OnTaskHandlerListener<T> task) {
        return postTask(new AfDataTask<>(t, task));
    }

    /**
     * 抛送带数据任务到Worker执行
     */
    @Override
    public <T, TT> AfData2Task postDataTask(T t, TT tt, AfData2Task.OnData2TaskHandlerListener<T, TT> task) {
        return postTask(new AfData2Task<>(t, tt, task));
    }

    /**
     * 抛送带数据任务到Worker执行
     */
    @Override
    public <T, TT, TTT> AfData3Task postDataTask(T t, TT tt, TTT ttt, AfData3Task.OnData3TaskHandlerListener<T, TT, TTT> task) {
        return postTask(new AfData3Task<>(t, tt, ttt, task));
    }


}