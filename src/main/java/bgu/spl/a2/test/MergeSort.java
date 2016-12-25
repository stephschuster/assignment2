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
        System.out.println("MergeSort started");

        if(array.length <= 1){
            complete(array);
        } else {
            List<Task<int[]>> tasks = new ArrayList<>();
            int[] firstPart = new int[this.array.length / 2];
            System.arraycopy(this.array, 0, firstPart, 0, this.array.length / 2);

            MergeSort firstTask = new MergeSort(firstPart);
            tasks.add(firstTask);

            int[] secPart = new int[this.array.length - (this.array.length / 2)];
            System.arraycopy(this.array, this.array.length / 2, secPart, 0, this.array.length - (this.array.length / 2));
            MergeSort secTask = new MergeSort(secPart);
            tasks.add(secTask);

            spawn(firstTask, secTask);

            whenResolved(tasks, () -> {
                System.out.println("this is the callback from when resolve");
                int[] firstArray = firstTask.getResult().get();
                int[] secArray = secTask.getResult().get();
                int[] result = new int[firstArray.length + secArray.length];
                int i = 0;
                int j = 0;
                int r = 0;
                while (i < firstArray.length && j < secArray.length) {
                    if (firstArray[i] <= secArray[j]) {
                        result[r] = firstArray[i];
                        i++;
                        r++;
                    } else {
                        result[r] = secArray[j];
                        j++;
                        r++;
                    }
                    if (i == firstArray.length) {
                        while (j < secArray.length) {
                            result[r] = secArray[j];
                            r++;
                            j++;
                        }
                    }
                    if (j == secArray.length) {
                        while (i < firstArray.length) {
                            result[r] = firstArray[i];
                            r++;
                            i++;
                        }
                    }
                }
                complete(result);
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        WorkStealingThreadPool pool = new WorkStealingThreadPool(4);
        int n = 1000; //you may check on different number of elements if you like
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