package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;

import java.util.ArrayList;

/**
 * Created by stephanieschustermann on 25/12/2016.
 */
public class WaveTask extends Task<ArrayList<Product>> {
    int qty;
    long startId;
    Warehouse warehouse;
    String product;

    public WaveTask(String product, Warehouse warehouse, long startId, int qty) {
        this.product = product;
        this.warehouse = warehouse;
        this.startId = startId;
        this.qty = qty;
    }

    @Override
    protected void start() {
        ArrayList<ManufactureTask> tasks = new ArrayList<>();
        // spawn task as a qty
        for(int i = 0; i < this.qty; i++){
            ManufactureTask task = new ManufactureTask(this.product, this.warehouse, this.startId);
            tasks.add(task);
            spawn(task);
        }

        ArrayList<Product> result = new ArrayList<>();
        // waits to all to be resolved
        whenResolved(tasks, () -> {
            for(ManufactureTask task : tasks){
                Product prod = task.getResult().get();
                result.add(prod);
            }

            complete(result);
        });
    }
}
