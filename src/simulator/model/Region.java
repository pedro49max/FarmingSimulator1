package simulator.model;

import java.util.List;
import java.util.ArrayList;

public abstract class Region implements Entity, FoodSupplier, RegionInfo{
	protected List<Animal> animals;
	
	public Region() {
		animals = new ArrayList<>();
	}
	final void add_animal(Animal a) {
		animals.add(a);
	}
	final void remove_animal(Animal a) {
		int i = 0;
		boolean found = false;
		while(!found && i < animals.size()) {
			if(animals.get(i).equals(a))
				found = true;
			else
				i++;
		}
		animals.remove(0);
	}
	final List<Animal> getAnimals(){
		final List<Animal> animalss;
		animalss = animals;
		return animalss;
	}
	public JSONObject as_JSON() {//returns a JSON structure as follows where ai is what is returned by as_JSON() of the corresponding animal:
		//"animals":[a1,a2,...],
	}
}
