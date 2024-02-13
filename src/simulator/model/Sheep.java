package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal{
	private Animal danger_source;
	private SelectionStrategy danger_strategy;
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("Sheep", Diet.HERBIVORE, 40.0, 35.0, mate_strategy, pos);
		this.danger_strategy = danger_strategy;
	}
	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this.danger_strategy = p1.danger_strategy;
	}
	public void update(double dt) {
		if(this.state == State.DEAD)
			return;
		else if(this.state == State.NORMAL) {
			if(this.pos.dot(dest) < 8)
				this.dest = Vector2D.get_random_vectorXY(0, region_mngr.get_width()-1, 0, region_mngr.height()-1);
			this.move(speed*dt*Math.exp((energy - 100.0)*0.007));
			this.age += dt;
			this.energy -= 20*dt;
			if(energy < 0)
				energy = 0;
			else if(energy > 100)
				energy = 100;
			this.desire += 40*dt;
			if(desire < 0)
				desire = 0;
			else if(desire > 100)
				desire = 100;
			if(danger_source == null) {
				//find dangerous animal
				danger_source= wolf;
			}
			if(danger_source != null) {
				this.state = State.DANGER;
			}
			else if(danger_source != null && this.desire > 65)
				this.state = State.MATE;
				
		}
		else if(this.state == State.DANGER) {
			if(danger_source != null && danger_source.get_state() == State.DEAD)
				danger_source = null;
			if(danger_source == null) {
				this.dest = this.pos.plus(pos.minus(danger_source.get_position()).direction());
				this.move(2*speed*dt*Math.exp((energy - 100.0)*0.007));
				this.age += dt;
				this.energy -= 1.2*20*dt;
				if(energy < 0)
					energy = 0;
				else if(energy > 100)
					energy = 100;
				this.desire += 40*dt;
				if(desire < 0)
					desire = 0;
				else if(desire > 100)
					desire = 100;
			}
			if(danger_source == null || pos.distanceTo(danger_source.get_position()) > this.sight_range) {
				//find dangerous animal
				danger_source= wolf;
				if(danger_source == null) {
					if(this.desire < 65)
						this.state = State.NORMAL;
					else
						this.state = State.MATE;
				}				
			}
		}
		else if(this.state == State.MATE) {
			if(mate_target != null && mate_target.get_state() == State.DEAD)
				mate_target = null;
			if(mate_target == null) {
				//find one mate target alive with while
				mate_target = sheep;
			}
			if(mate_target != null) {
				this.dest = mate_target.get_position();
				this.move(2*speed*dt*Math.exp((energy - 100.0)*0.007));
				this.age += dt;
				this.energy -= 1.2*20*dt;
				if(energy < 0)
					energy = 0;
				else if(energy > 100)
					energy = 100;
				this.desire += 40*dt;
				if(desire < 0)
					desire = 0;
				else if(desire > 100)
					desire = 100;
				if(this.pos.distanceTo(mate_target.get_position()) < 8) {
					this.desire = 0;
					mate_target.desire = 0;
					if(this.baby == null) 
						if(Utils._rand.nextDouble(9) != 7)
							this.baby = new Sheep(this, mate_target);
					mate_target = null;
				}
			}
			if(danger_source == null) {
				//find dangerous animal
				danger_source= wolf;
			}
			if(danger_source != null)
				this.state = State.DANGER;
			else if(danger_source == null && desire < 65)
				this.state = State.NORMAL;
		}
	}
	@Override
	public State get_state() {
		return this.state;
	}
	@Override
	public Vector2D get_position() {
		return this.pos;
	}
	@Override
	public String get_genetic_code() {
		return this.genetic_code;
	}
	@Override
	public Diet get_diet() {
		return this.diet;
	}
	@Override
	public double get_speed() {
		return this.speed;
	}
	@Override
	public double get_sight_range() {
		return this.sight_range;
	}
	@Override
	public double get_energy() {
		return this.energy;
	}
	@Override
	public double get_age() {
		return this.age;
	}
	@Override
	public Vector2D get_destination() {
		return this.dest;
	}
	@Override
	public boolean is_pregnant() {
		boolean pregnant = true;
		if(this.baby == null)
			pregnant = false;
		return pregnant;
	}
}
