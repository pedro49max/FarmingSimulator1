package simulator.control;

import java.io.OutputStream;

import simulator.model.Simulator;

public class Controller {
	private Simulator sim;
	
	public Controller(Simulator sim) {
		this.sim = sim;
	}
	public void load_data(JSONObject data) {
		/*we assume that data has the two keys
"animals" and "regions", the latter being optional. The values of these keys are of type
JSONArray and each element of the array is a JSONObject corresponding to the specification of an
animal or a region, respectively. For each element you must do the following (it is very important to add
the regions before adding the animals):
 Each JSONObject in the list of regions (if any, since it is optional) has the form
{"row": [rf,rt], "col": [cf,ct], "spec": Obj}
where rf, rt, cf and ct are integers and Obj is a JSONObject describing a region (see
section "Factories"). _sim.set_region(R,C,Obj) must be called for each rf≤R≤rt and
cf≤C≤ct (i.e. using a nested loop to modify multiple regions).
 Each JSONObject in the list of animals has the form
{"amount": N, "spec": Obj}
where N is a positive integer and Obj is a JSONObject describing an animal (see section
"Factories"). _sim.add_animal(Obj) must be called in a loop N times to add N animals of this
type*/
	}
	public void run(double t, double dt, boolean sv, OutputStream out) {
		while(this.sim.get_time() < t) {
			this.sim.advance(dt);
			/*In addition, it writes a JSON structure of the following form to out:
{
"in": init_state,
"out": final_state
}
where init_state is the result returned by _sim.as_JSON() before entering the loop, and
final_state is the result returned by _sim.as_JSON() after exiting the loop.
In addition, if the value of sv is true, the simulation must be displayed using the object viewer (see
section "The Object Viewer").*/
		}
	}
}
