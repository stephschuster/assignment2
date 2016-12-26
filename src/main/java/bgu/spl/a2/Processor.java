package bgu.spl.a2;

import java.util.LinkedList;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {

    private final WorkStealingThreadPool pool;
    private final int id;

    /**
     * constructor for this class
     * <p>
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     * <p>
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id   - the processor id (every processor need to have its own unique
     *             id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
    }

    @Override
    public void run() {
        // this is enough?!
        while (!this.pool.isShutdown || !this.pool.pairs[id].snd.isEmpty()) {
            //if there is a task - do the task
            if (!this.pool.pairs[id].snd.isEmpty()) {
                Thread t = Thread.currentThread();
                String name = t.getName();

                this.pool.pairs[id].snd.pollFirst().handle(this);
            }
            //else if no tasks in linked list - steal()
            else if (steal()) {
                //not sure if to do anything in here
            }
            //else - sleep (until version update)
            else {
                try {
                    pool.monitor.await(pool.monitor.getVersion());
                } catch (InterruptedException e) {
                }
            }
        }
    }


    private boolean steal() {
        boolean result = false;
        //loop through the processors starting from the one next to me.
        for (int i = id + 1; i < pool.pairs.length - 1 + id && !result; i++) {
            Processor stolen = pool.pairs[i % pool.pairs.length].fst;

            //find the first one you can steal from.
            if (stolen.canStealFromMe()) {
                //steal half of his tasks.
                int half = pool.pairs[i % pool.pairs.length].snd.size() / 2;
                for (int j = 0; j < half; j++) {
                    Task task = pool.pairs[i % pool.pairs.length].snd.pollLast();
                    if(task != null) {
                        this.addNewTask(task);
                        result = true;
                    }

                }
            }
        }


        return result;
    }

    /*package*/ void addNewTask(Task task) {
        this.pool.pairs[id].snd.add(task);
        this.pool.monitor.inc();
    }

    /*package*/ boolean canStealFromMe() {
        return pool.pairs[this.id].snd.size() > 1;
    }
}
