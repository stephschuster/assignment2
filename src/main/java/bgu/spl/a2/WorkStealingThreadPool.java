package bgu.spl.a2;

import com.sun.tools.javac.util.Pair;

import java.util.LinkedList;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {
        //we need to change the linked list to ConcurrentLinkedDeque. it should work much better with threads
        protected Pair<Processor, LinkedList<Task>>[] pairs;
        private int howManyProcessors;
        private Thread[] arrayThread;
        private VersionMonitor myMonitor;
    /**
     * creates a {@link WorkStealingThreadPool} which has nthreads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     *
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     * thread pool
     */
    public WorkStealingThreadPool(int nthreads) {
        howManyProcessors = nthreads;
        pairs = new Pair[howManyProcessors];
        arrayThread = new Thread[howManyProcessors];
        myMonitor = new VersionMonitor();
        for(int i=0; i<howManyProcessors; i++) {
            pairs[i] = new Pair<>(new Processor(i, this), new LinkedList<Task>());
            arrayThread[i] = new Thread(pairs[i].fst);
        }

    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     *
     * @param task the task to execute
     */
    public void submit(Task<?> task) {
        int rand = (int)(Math.random()*howManyProcessors);
        pairs[rand].fst.addNewTask(task);
        myMonitor.inc();
    }

    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     *
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is
     * interrupted
     * @throws UnsupportedOperationException if the thread that attempts to
     * shutdown the queue is itself a processor of this queue
     */
    public void shutdown() throws InterruptedException {
        for(int i=0; i<howManyProcessors; i++) {
            pairs[i].fst.setShutdown();
            arrayThread[i].join();
        }
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        for(int i=0; i<howManyProcessors; i++) {
            arrayThread[i].start();
        }
    }

}
