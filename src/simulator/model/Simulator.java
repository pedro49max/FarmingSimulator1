package simulator.model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import simulator.factories.Factory;

public class Simulator implements JSONable{
	private Factory<Animal> animals_factory;
	private Factory<Region> regions_factory;
	private RegionManager region_mngr;
	private List<Animal> animals;
	private double time;
	
	public Simulator(int cols, int rows, int width, int height,Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		this.animals_factory = animals_factory;
		this.regions_factory = regions_factory;
		this.region_mngr = new RegionManager(cols, rows, width, height);
		this.animals = new ArrayList<>();
		this.time = 0.0;
		
	}
	private void set_region(int row, int col, Region r) {
		this.region_mngr.set_region(row, col, r);
	}
	public void set_region(int row, int col, JSONObject r_json) {
		Region r = this.regions_factory.create_instance(r_json); // Create a region from the JSON representation
        set_region(row, col, r); // Call the private method to set the region.
	}
	private void add_animal(Animal a) {
		this.animals.add(a);
		this.region_mngr.register_animal(a);
	}
	public void add_animal(JSONObject a_json){
		Animal animal = animals_factory.create_instance(a_json); // Create an animal from the JSON representation
        add_animal(animal); // Call the private method to add the animal to the simulation
    }
	
	public MapInfo get_map_info() {
		return this.region_mngr;
	}
	public List<? extends Animalnfo> get_animals(){
		final List<Animal> copy = this.animals;
		return copy;
	}
	public double get_time() {
		return this.time;
	}
	public void advance(double dt) {
		this.time += dt;
		for(int i = 0; i < this.animals.size(); i++) {
			Animal animal = animals.get(i);
			if(animal.get_state() == State.DEAD) {
				this.animals.remove(animal);
				this.region_mngr.unregister_animal(animal);
				i--;
			}
			else {
				animal.update(dt);
				this.region_mngr.update_animal_region(animal);
			}			
		}
		this.region_mngr.update_all_regions(dt);
		for(int i = 0; i < this.animals.size(); i++) {
			Animal animal = animals.get(i);
			Animal baby;
			if(animal.is_pregnant()) {
				baby = animal.deliver_baby();
				this.add_animal(baby);
			}						
		}
	}
	@Override
	public JSONObject as_JSON() {
		 JSONObject json = new JSONObject();
	        // Add current time
	        json.put("time", time);

	        // Add state of the region manager
	        json.put("state", region_mngr.as_JSON());

	        return json;
	}
	/*Notice that there are two versions of the add_animal and set_region methods, one receives the input
in JSON format while the other receives the corresponding objects once they have been created. The ones
that receive the objects are private. The purpose of having the 2 versions is to facilitate development and
debugging practice: in your first implementation, before implementing the factories, change those methods
from private to public and use them directly to add animals and regions from outside. When you have
implemented the factories change them back to private again. This way you can debug the program
without having implemented the factories.*/
}
