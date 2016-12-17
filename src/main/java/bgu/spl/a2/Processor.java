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
    private boolean isShutDown;

    /**
     * constructor for this class
     *
     * IMPORTANT:
     * 1) this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * 2) you may not add other constructors to this class
     * nor you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param id - the processor id (every processor need to have its own unique
     * id inside its thread pool)
     * @param pool - the thread pool which owns this processor
     */
    /*package*/ Processor(int id, WorkStealingThreadPool pool) {
        this.id = id;
        this.pool = pool;
    }

    @Override
    public void run() {
        while(!this.isShutDown){
            //if there is a task - do the task
            if(!this.pool.pairs[id].snd.isEmpty()) {
                System.out.println("ID: " + this.id + " handle task");
                this.pool.pairs[id].snd.pollFirst().handle(this);
            }
            //else if no tasks in linked list - steal()
            else if( steal()) {
                System.out.println("ID: " + this.id + " steal tasks");
                //not sure if to do anything in here
            }
            //else - sleep (until version update)
            else {
                try {
                    System.out.println("ID: " + this.id + " waiting");
                    pool.monitor.await(pool.monitor.getVersion());
                    System.out.println("ID: " + this.id + " stop waiting");
                } catch (InterruptedException e) {
                }
            }

            System.out.println("ID: " + this.id + " have " + this.pool.pairs[id].snd.size() + " tasks enqueue");
        }



        // this will run once, pay attention... what happens after you finish the first task?
        // who is gonna call the thread again?
        // do I missing something? maybe the while loop needs to be somewhere else?
        // ok Im waiting, for do what? whos gonna awake me?
        // I think we need to add a protected/package method to add new task
        // so when a task spawns, it has where to save the tasks...
        // this method will call the pool  - task.processor.addNewTask(newTask)
        // other magical non-object oriented programming (*sarcasm*)
    }


    private boolean steal() {
        System.out.println("steal!!!!");
        LinkedList<Task> temp = new LinkedList<>();
        boolean stealing = false;
        //loop through the processors starting from the one next to me.
        for(int i = id+1; i<pool.pairs.length+id+1 && !stealing; i++) {
            //find the first one you can steal from.
            if(pool.pairs[i%pool.pairs.length].fst.canStealFromMe()) {
                System.out.println("stealing from " + i%pool.pairs.length + " to " + this.id);
                //steal half of his tasks.
                for(int j =0; j<pool.pairs[i%pool.pairs.length].snd.size()/2; j++) {
                    this.addNewTask(pool.pairs[i % pool.pairs.length].snd.getLast());
                    stealing = true;
                }
            }
        }

        return stealing;
    }

    /*package*/ void addNewTask(Task task){
        this.pool.pairs[id].snd.add(task);
        this.pool.monitor.inc();

    }

    /*package*/ void setShutdown(){
        this.isShutDown = true;
    }

    /*package*/ boolean canStealFromMe() {
        if(pool.pairs[this.id].snd.size() <= 1) {
            return false;
        } else {
            return true;
        }
    }


}
