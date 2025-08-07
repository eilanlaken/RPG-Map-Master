package com.heavybox.jtix.async;

import com.heavybox.jtix.collections.Array;
import com.heavybox.jtix.physics2d.Physics2DException;

import java.util.ArrayList;
import java.util.List;

public class AsyncTaskRunner {

    public static <T extends AsyncTask> Thread[] async(T ...tasks) {
        Array<Thread> tThreads = new Array<>();
        for (T task : tasks) {
            tThreads.add(async(task));
        }
        return tThreads.toArray(Thread.class);
    }

    public static <T extends AsyncTask> Thread[] async(Iterable<T> tasks) {
        Array<Thread> tThreads = new Array<>();
        for (AsyncTask task : tasks) {
            tThreads.add(async(task));
        }
        return tThreads.toArray(Thread.class);
    }

    public static <T extends AsyncTask> Thread async(T task) {
        //if (task.inProgress || task.complete) return null; // TODO: consider

        if (task.prerequisites != null && !task.prerequisites.isEmpty()) {
            List<Thread> pThreads = new ArrayList<>();
            for (AsyncTask prerequisite : task.prerequisites) {
                Thread pThread = async(prerequisite);
                pThreads.add(pThread);
            }

            for (Thread pThread : pThreads) {
                try {
                    pThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        Thread tThread = new Thread(task::run);
        tThread.setDaemon(true);
        tThread.start();
        return tThread;
    }

    public static void await(Thread ...threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new AsyncException(e.getMessage());
            }
        }
    }

    public static void await(Iterable<Thread> threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new Physics2DException(e.getLocalizedMessage());
            }
        }
    }

    public static void execute(AsyncTask ...tasks) {
        for (AsyncTask task : tasks) execute(task);
    }

    public static void execute(Iterable<AsyncTask> tasks) {
        for (AsyncTask task : tasks) execute(task);
    }

    public static void execute(AsyncTask task) {
        for (AsyncTask preRequisite : task.prerequisites) {
            execute(preRequisite);
        }
        task.run();
    }
    
}
