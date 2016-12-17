/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.test;

import bgu.spl.a2.Task;
import bgu.spl.a2.WorkStealingThreadPool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class MergeSort extends Task<int[]> {

    private final int[] array;

    public MergeSort(int[] array) {
        this.array = array;
    }

    @Override
    protected void start() {
        int sum=0;
        List<Task<Integer>> tasks = new ArrayList<>();
        int rows = array.length;
        for(int i=0;i<rows;i++){
            SumRow newTask=new SumRow(array,i);
            spawn(newTask);
            tasks.add(newTask);
        }
        whenResolved(tasks,()->{
                    int[] res = new int[rows];
                    for(int j=0; j< rows; j++){
                        res[j] = tasks.get(j).getResult().get();
                    }
                    complete(res);
                }
        );
    }

    public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
        int n = 1000000; //you may check on different number of elements if you like
        int[] array = new Random().ints(n).toArray();

        MergeSort task = new MergeSort(array);

        CountDownLatch l = new CountDownLatch(1);
        pool.start();
        pool.submit(task);
        task.getResult().whenResolved(() -> {
            //warning - a large print!! - you can remove this line if you wish
            System.out.println(Arrays.toString(task.getResult().get()));
            l.countDown();
        });

        l.await();
        pool.shutdown();
    }

}

class SumRow extends Task<Integer> {
    private int[] array;
    private int r;

    SumRow(int[] array,int r) {
        this.array = array;
        this.r=r;
    }
    protected void start(){
        int sum=0;
        for(int j=0 ;j<array.length;j++)
            sum+=array[j];
        complete(sum);
    }
}
