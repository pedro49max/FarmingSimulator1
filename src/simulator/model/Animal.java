package simulator.model;

import simulator.misc.Vector2D;

public abstract class Animal implements Entity, Animalnfo{
	protected String genetic_code;
	protected Diet diet;
	protected State state;
	protected Vector2D pos;
	protected Vector2D dest;
	protected double energy;
	protected double speed;
	protected double age;
	protected double desire;
	protected double sight_range;
	protected Animal mate_target;
	protected Animal baby;
	protected AnimalMapView region_mngr;
	protected SelectionStrategy mate_strategy;
}
