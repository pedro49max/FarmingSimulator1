package simulator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import simulator.misc.Vector2D;

import java.util.HashMap;

public class RegionManager implements AnimalMapView{
	private int mapWidth;
	private int mapHeight;
	private int rows;
	private int colums;
	private int regWidth;
	private int regHeight;
	private Region[][] regions;
	private Map<Animal,Region> animal_region;
	
	public RegionManager(int cols, int rows, int width, int height) {
		this.rows = rows;
		this.colums = cols;
		this.mapWidth = width;
		this.mapHeight = height;
		this.regHeight = this.mapHeight/this.rows;
		this.regWidth = this.mapWidth/this.colums;
		regions = new Region[rows][colums];
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < cols; c++) {
				regions[r][c] = new DefaultRegion();
			}
		animal_region = new HashMap<>();
	}
	void set_region(int row, int col, Region r) {
		List<Animal> animals = r.getAnimals();
		Region newRegion = r;
		this.animal_region.remove(r);
		for(int i = 0; i < animals.size(); i++) {
			newRegion.add_animal(animals.get(i));
			this.animal_region.put(animals.get(i), newRegion);
		}
		regions[row][col] = newRegion;	
	}
	void register_animal(Animal a) {
		Vector2D pos= a.get_position();
		int newCol = (int) (pos.getX() / this.regWidth);
		int newRow = (int) (pos.getY() / this.regHeight);
		regions[newRow][newCol].add_animal(a);
		this.animal_region.put(a, regions[newRow][newCol]);
		a.init(this);
	}
	void unregister_animal(Animal a) {
		Vector2D pos= a.get_position();
		int newCol = (int) (pos.getX() / this.regWidth);
		int newRow = (int) (pos.getY() / this.regHeight);
		regions[newRow][newCol].remove_animal(a);
		this.animal_region.remove(a, regions[newRow][newCol]);
	}
	void update_animal_region(Animal a) {
		int nCol = 0;
		int nRow = 0;
		int i = 0;
		boolean found = false;
		while(!found && nRow < this.rows) {
			while(!found && nCol < this.rows) {
				List<Animal> animals = regions[nRow][nCol].getAnimals();
				while(!found && i < animals.size()) {
					if(animals.get(i) == a)
						found = true;
					else
						i++;
				}
				if(!found) {
					i = 0;
					nCol++;
				}
			}
			if(!found) {
				nCol = 0;
				nRow++;
			}
		}
		if(!found)
			this.register_animal(a);
		else {
			Vector2D pos= a.get_position();
			int newCol = (int) (pos.getX() / this.regWidth);
			int newRow = (int) (pos.getY() / this.regHeight);
			if(nCol != newCol || nRow != newRow) {
				this.unregister_animal(a);
				this.register_animal(a);
			}
			
		}
	}
	public double get_food(Animal a, double dt) {
		double food;
		Vector2D pos= a.get_position();
		int newCol = (int) (pos.getX() / this.regWidth);
		int newRow = (int) (pos.getY() / this.regHeight);
		food = regions[newRow][newCol].get_food(a, dt);
		return food;
	}
	void update_all_regions(double dt) {
		for(int r = 0; r < this.rows; r++) 
			for(int c = 0; c < this.get_cols(); c++)
				regions[r][c].update(dt);
	}
	public List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter){
		List<Animal> animals = new ArrayList<>();
		Vector2D pos= a.get_position();
		int right = (int) ((pos.getX() + a.get_sight_range()) / this.regWidth);
		int left = (int) ((pos.getX() - a.get_sight_range()) / this.regWidth);
		int up = (int) ((pos.getY() - a.get_sight_range())/ this.regHeight);
		int down = (int) ((pos.getY() + a.get_sight_range())/ this.regHeight);
		if(right >= this.colums)
			right = this.colums - 1;
		if(left < 0)
			left = 0;
		if(down >= this.rows)
			down = this.rows - 1;
		if(up < 0)
			up = 0;
		for(int r = up; r <= down;r++)
			for(int c = left; c <= right; c++) {
				List<Animal> regAnimals = regions[r][c].getAnimals();
				for(int i = 0; i < regAnimals.size();i++) {
					Animal regAnimal = animals.get(i);
					if(a.pos.distanceTo(regAnimal.pos) <= a.get_sight_range())
						animals.add(regAnimal);
				}
			}
		return animals;
	}
	public JSONObject as_JSON() {
		//{
			//"regions":[o1,o2,...],
		//	}
		//	where oi is a JSON structure corresponding to a region and has the following form
		//	{
			//"row: i,
		//	"col": j,
		//	"data": r
		//	}
		//	where r is what is returned by the as_JSON() method of the region in row i and column j.
	}
	@Override
	public int get_cols() {
		return this.colums;
	}
	@Override
	public int get_rows() {
		return this.rows;
	}
	@Override
	public int get_width() {
		return this.mapWidth;
	}
	@Override
	public int get_height() {
		return this.mapHeight;
	}
	@Override
	public int get_region_width() {
		return this.regWidth;
	}
	@Override
	public int get_region_height() {
		return this.regHeight;
	}
}
