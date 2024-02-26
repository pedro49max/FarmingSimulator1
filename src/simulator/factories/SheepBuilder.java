package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectClosest;
import simulator.model.SelectFirst;
import simulator.model.SelectYoungest;

public class SheepBuilder extends Builder<Animal> {
	public SheepBuilder() {
        super("sheep", "Sheep Builder");
    }

    @Override
    protected Animal create_instance(JSONObject data) {
        // Parsing genetic code, diet, and sight range


        // Parsing mate strategy
        SelectionStrategy mateStrategy = parseStrategy(data.optJSONObject("mate_strategy"));

        // Parsing danger strategy
        SelectionStrategy dangerStrategy = parseStrategy(data.optJSONObject("danger_strategy"));

        // Parsing position
        Vector2D position = parsePosition(data.optJSONObject("pos"));

        // Creating Sheep instance with provided attributes
        return new Sheep( mateStrategy, dangerStrategy, position);
    }

    // Helper method to parse a SelectionStrategy from JSON
    private SelectionStrategy parseStrategy(JSONObject strategyJson) {
        if (strategyJson == null) {
            return new SelectFirst();
        } else {
        		int x = strategyJson.getInt("random");
        		if(x%3 == 0)
        			return new SelectFirst();
        		else if(x%3 == 1)
        			return new SelectClosest();
        		else
        			return new SelectYoungest();
            
        }
    }
    private Vector2D parsePosition(JSONObject positionJson) {
        if (positionJson != null) {
            double x = positionJson.getDouble("x");
            double y = positionJson.getDouble("y");
            return new Vector2D(x, y);
        } else {
            return null;
        }
    }

}
