package simulator.model;

public interface SelectionStrategy {
	Animal select(Animal a, List<Animal> as);
	Animal selectFirst(Animal a, List<Animal> as);
	Animal selectClosest(Animal a, List<Animal> as);
	Animal selectYoungest(Animal a, List<Animal> as);
}
