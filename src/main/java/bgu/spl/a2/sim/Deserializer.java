package bgu.spl.a2.sim;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Deserializer implements JsonDeserializer<ParseJson> {

    public ParseJson deserialize(JsonElement json, Type type,
                                 JsonDeserializationContext context) throws JsonParseException {

        JsonObject obj = json.getAsJsonObject();

        ParseJson test = new ParseJson();
        test.setThreads(obj.get("threads").getAsInt());

        Gson Gson = new Gson();
        Type toolsType = new TypeToken<List<ParseTool>>(){}.getType();
        List<ParseTool> toolsList = Gson.fromJson(obj.get("tools"), toolsType);
        test.setTools(toolsList);

        Type plansType = new TypeToken<List<ParsePlan>>(){}.getType();
        List<ParsePlan> plansList = Gson.fromJson(obj.get("plans"), plansType);
        test.setPlans(plansList);

       Type waveType = new TypeToken<List<List<ParseWave>>>(){}.getType();
       List<List<ParseWave>> wavesList = Gson.fromJson(obj.get("waves"), waveType);
       test.setWaves(wavesList);



        return test;
    }
}