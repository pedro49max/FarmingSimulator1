package simulator.model;

import java.util.ArrayList;
import java.util.List;

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
		//creates a region R from r_json and calls set_region(row,col,R).
	}
	private void add_animal(Animal a) {
		this.animals.add(a);
		this.region_mngr.register_animal(a);
	}
	public void add_animal(JSONObject a_json){
		//: creates an animal A from a_json and calls add_animal(A)	
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
				this.animals.remove(i);
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
	public JSONObject as_JSON() {
		//returns a JSON structure as follows where t is the current time and s is what is returned by the as_JSON() method of the region manager:
		//{
		//"time": t,
		//"state": s,
		//}
	}
	/*Notice that there are two versions of the add_animal and set_region methods, one receives the input
in JSON format while the other receives the corresponding objects once they have been created. The ones
that receive the objects are private. The purpose of having the 2 versions is to facilitate development and
debugging practice: in your first implementation, before implementing the factories, change those methods
from private to public and use them directly to add animals and regions from outside. When you have
implemented the factories change them back to private again. This way you can debug the program
without having implemented the factories.*/
}
