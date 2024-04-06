package org.example;

import java.util.concurrent.*;

interface ResultCallback1<T> {
    void onResult(T result);
}

interface ResultCallback2<T1, T2> {
    void onResult(T1 r1, T2 r2);
}

interface ResultCallback3<T1, T2, T3> {
    void onResult(T1 r1, T2 r2, T3 r3);
}

public class ExecutorSample {
    private static final int MAX_THREAD_NUM = Runtime.getRuntime().availableProcessors();
    private static final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREAD_NUM);

    public static <T1> void runAsync(final Callable<T1> task,
                                     final ResultCallback1<T1> callback) {
        executor.submit(() -> {
            try {
                T1 result = task.call();
                callback.onResult(result);
            } catch (Exception e) {
                callback.onResult(null);
            }
        });
    }

    public static <T1, T2> void runAsync(final Callable<T1> task1,
                                         final Callable<T2> task2,
                                         final ResultCallback2<T1, T2> callback) {
        final Object[] results = new Object[2];
        final CountDownLatch latch = new CountDownLatch(2);

        executor.submit(() -> {
            try {
                latch.await();
                callback.onResult(castOrNull(results[0]), castOrNull(results[1]));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executeTaskWithResult(task1, 0, results, latch);
        executeTaskWithResult(task2, 1, results, latch);
    }

    public static <T1, T2, T3> void runAsync(final Callable<T1> task1,
                                             final Callable<T2> task2,
                                             final Callable<T3> task3,
                                             final ResultCallback3<T1, T2, T3> callback) {
        final Object[] results = new Object[3];
        final CountDownLatch latch = new CountDownLatch(3);

        executor.submit(() -> {
            try {
                latch.await();
                callback.onResult(castOrNull(results[0]), castOrNull(results[1]), castOrNull(results[2]));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executeTaskWithResult(task1, 0, results, latch);
        executeTaskWithResult(task2, 1, results, latch);
        executeTaskWithResult(task3, 2, results, latch);
    }

    private static <T> void executeTaskWithResult(final Callable<T> task,
                                                  final int index,
                                                  final Object[] results,
                                                  final CountDownLatch latch) {
        executor.submit(() -> {
            try {
                results[index] = task.call();
            } catch (Exception e) {
                results[index] = null;
            } finally {
                latch.countDown();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> T castOrNull(Object obj) {
        try {
            return (T) obj;
        } catch (Exception e) {
            return null;
        }
    }
}
