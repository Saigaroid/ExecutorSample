package org.example;

import java.lang.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorTest {
    public static void main(String[] args) {
        Callable<String> stringTask = () -> {
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " Completed");
            return "Hello World!!";
        };

        Callable<Integer> integerTask = () -> {
            Thread.sleep(800);
            System.out.println(Thread.currentThread().getName() + " Completed");
            return 100;
        };

        Callable<Boolean> booleanTask = () -> {
            Thread.sleep(200);
            System.out.println(Thread.currentThread().getName() + " Completed");
            return Boolean.TRUE;
        };

        AtomicBoolean main = new AtomicBoolean(true);

        ExecutorSample.runAsync(
                stringTask,
                integerTask,
                booleanTask,
                (result1, result2, result3) -> {
                    main.set(false);
                    System.out.println("result1 = " + result1);
                    System.out.println("result2 = " + result2);
                    System.out.println("result3 = " + result3);
                });

        while (main.get())  {
            try {
                System.out.println("Main thread is not blocked...");
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

