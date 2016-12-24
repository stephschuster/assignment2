package bgu.spl.a2.sim;

import java.util.List;

/**
 * Created by Medhopz on 12/24/2016.
 */
public class ParseJson {
    private int threads;
    private List<ParseTool> tools;
    private List<ParsePlan> plans;
    private List<List<ParseWave>> waves;



    public void setThreads(int _threads) {
        this.threads = _threads;
    }


    public int getThreads() {
        return threads;
    }

    public void setTools(List<ParseTool> tools) {
        this.tools = tools;
    }

    public List<ParseTool> getTools() {
        return tools;
    }

    public void setPlans(List<ParsePlan> plans) {
        this.plans = plans;
    }

    public List<ParsePlan> getPlans() {
        return plans;
    }

    public void setWaves(List<List<ParseWave>> wave) {
        this.waves = wave;
    }

    public List<List<ParseWave>> getWaves() {
        return waves;
    }
}
