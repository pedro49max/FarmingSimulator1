package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class WolfBuilder extends Builder<Animal> {
	private Factory<SelectionStrategy> strategyFactory;
	
	public WolfBuilder(Factory<SelectionStrategy> strategyFactory) {
        super("wolf", "Wolf Builder");
        this.strategyFactory = strategyFactory;
    }

    @Override
    protected Animal create_instance(JSONObject data) {
        // Parsing genetic code, diet, and sight range


        // Parsing mate strategy
        SelectionStrategy mateStrategy = parseStrategy(data.optJSONObject("mate_strategy"));

        // Parsing danger strategy
        SelectionStrategy dangerStrategy = parseStrategy(data.optJSONObject("hunger_strategy"));

        // Parsing position
        Vector2D position = parsePosition(data.optJSONObject("pos"));

        // Creating Sheep instance with provided attributes
        return new Sheep( mateStrategy, dangerStrategy, position);
    }

    // Helper method to parse a SelectionStrategy from JSON
    private SelectionStrategy parseStrategy(JSONObject strategyJson) {
    	if (strategyJson == null) {
            // Default to SelectFirstStrategy if no strategy is provided
            return new SelectFirst();
        } else {
            // Create the strategy using the factory
            String strategyType = strategyJson.getString("type");
            JSONObject strategyData = strategyJson.getJSONObject("data");
            return strategyFactory.create_instance(new JSONObject().put("type", strategyType).put("data", strategyData));
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
