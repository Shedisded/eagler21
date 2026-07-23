package net.minecraft.client.sounds;

import java.util.concurrent.locks.LockSupport;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;

public class SoundEngineExecutor extends BlockableEventLoop<Runnable> {
    private Thread thread = this.createThread();
    private volatile boolean shutdown;

    public SoundEngineExecutor() {
        super("Sound executor");
    }

    private Thread createThread() {
        // ponytail: TeaVM is single-threaded (JS). A real "Sound engine" thread would run()->
        // waitForTasks()->LockSupport.park() and TeaVM throws "Cannot park the only thread" in a
        // 600x crash loop. Return the current (main/render) thread instead of spawning: getRunningThread()
        // now == the caller thread, so isSameThread() is true and BlockableEventLoop runs every sound
        // task INLINE (see scheduleExecutables/submit/execute) with no background thread or parking.
        return Thread.currentThread();
    }

    @Override
    public Runnable wrapRunnable(Runnable p_120341_) {
        return p_120341_;
    }

    @Override
    public void schedule(Runnable p_445731_) {
        if (!this.shutdown) {
            super.schedule(p_445731_);
        }
    }

    @Override
    protected boolean shouldRun(Runnable p_120339_) {
        return !this.shutdown;
    }

    @Override
    protected Thread getRunningThread() {
        return this.thread;
    }

    private void run() {
        while (!this.shutdown) {
            this.managedBlock(() -> this.shutdown);
        }
    }

    @Override
    public void waitForTasks() {
        LockSupport.park("waiting for tasks");
    }

    public void shutDown() {
        // ponytail: no background thread to interrupt/join (see createThread) - just stop scheduling.
        this.shutdown = true;
        this.dropAllTasks();
    }

    public void startUp() {
        this.shutdown = false;
        this.thread = this.createThread();
    }
}
