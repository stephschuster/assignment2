package bgu.spl.a2;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * an abstract class that represents a task that may be executed using the
 * {@link WorkStealingThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the task result type
 */
public abstract class Task<R> {
    Processor currentProcessor;
    AtomicInteger whenResolveCounter  = new AtomicInteger(0);
    Deferred<R> deferred = new Deferred();
    boolean readyToComplete;
    boolean started;
    Runnable callback;
    public String name;
    /**
     * start handling the task - note that this method is protected, a handler
     * cannot call it directly but instead must use the
     * {@link #handle(bgu.spl.a2.Processor)} method
     */
    // check if the task is ready to run
    // separate the task
    // add them to handler
    // wait for the tasks to finish
    protected abstract void start();

    /**
     * start/continue handling the task
     * <p>
     * this method should be called by a processor in order to start this task
     * or continue its execution in the case where it has been already started,
     * any sub-tasks / child-tasks of this task should be submitted to the queue
     * of the handler that handles it currently
     * <p>
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * @param handler the handler that wants to handle the task
     */
    /*package*/
    final void handle(Processor handler) {
        // save the handler on this
        this.currentProcessor = handler;

        if (this.readyToComplete && this.callback != null && this.started) {
            this.callback.run();
        } else if(!this.started){
            this.started = true;
            // this will spawn, add callback, then, after all the tasks are finished
            // the callback will be called and the complete function will be called
            // thats the way we "re-add" the function to the process
            this.start();
        }
    }

    /**
     * This method schedules a new task (a child of the current task) to the
     * same processor which currently handles this task.
     *
     * @param task the task to execute
     */
    protected final void spawn(Task<?>... task) {
        for (Task current : task) {
            this.currentProcessor.addNewTask(current);
        }
    }

    /**
     * add a callback to be executed once *all* the given tasks results are
     * resolved
     * <p>
     * Implementors note: make sure that the callback is running only once when
     * all the given tasks completed.
     *
     * @param tasks
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void whenResolved(Collection<? extends Task<?>> tasks, Runnable callback) {
        this.callback = callback;
        for (Task curr : tasks) {
            curr.getResult().whenResolved(() -> {
                // check if all the tasks are done
                if (tasks.size() == this.whenResolveCounter.addAndGet(1)) {
                    readyToComplete = true;
                    // re-add the task to processor
                    this.currentProcessor.addNewTask(this);
                }
            });
        }
    }

    /**
     * resolve the internal result - should be called by the task derivative
     * once it is done.
     *
     * @param result - the task calculated result
     */
    protected final void complete(R result) {
        this.deferred.resolve(result);
    }

    /**
     * @return this task deferred result
     */
    public final Deferred<R> getResult() {
        return this.deferred;
    }
}
