package com.example.geobird;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class TaskScheduler {
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final AtomicReference<Runnable> taskRef = new AtomicReference<>();
    private static Runnable pausedTask = null;

    public static void scheduleTask(Runnable task) {
        taskRef.set(task);
        executor.scheduleAtFixedRate(TaskScheduler::executeTask, 0, 15, TimeUnit.MILLISECONDS);
    }

    public static void updateTask(Runnable newTask) {
        taskRef.set(newTask);
    }

    private static void executeTask() {
        Runnable task = taskRef.get();
        if (task != null) {
            task.run();
        }
    }

    public static void pause() {
        TaskScheduler.pausedTask = taskRef.get();
        TaskScheduler.updateTask(() -> {});
    }

    public static void resume() {
        if (TaskScheduler.pausedTask == null) {
            Log.d("ERROR", "Resume was called before a pause");
            return;
        }
        TaskScheduler.updateTask(TaskScheduler.pausedTask);
        TaskScheduler.pausedTask = null;
    }
}