package com.example.geobird;

import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Scheduler {
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final AtomicReference<Runnable> taskRef = new AtomicReference<>();
    private Runnable pausedTask = null;

    /**
     * Set up a scheduler that will execute a given task passed to updateTask
     * with a set interval between calls. It does nothing until passed a runnable
     * to updateTask
     * */
    public Scheduler(long interval) {
        executor.scheduleAtFixedRate(this::executeTask, 0, interval, TimeUnit.MILLISECONDS);
    }

    public void updateTask(Runnable newTask) {
        taskRef.set(newTask);
    }

    private void executeTask() {
        Runnable task = taskRef.get();
        if (task != null) {
            task.run();
        }
    }

    public void pause() {
        pausedTask = taskRef.get();
        updateTask(() -> {});
    }

    public void resume() {
        if (pausedTask == null) {
            Log.d("ERROR", "Resume was called before a pause");
            return;
        }
        updateTask(pausedTask);
        pausedTask = null;
    }
}
