package f1.stepbystep;

public class Pilot {
	String name;
	Car car;

	public Pilot() {
	}

	public Pilot(String name, Car car) {
		this.name = name;
		this.car = car;
	}

	public String toString() {
		return "name = " + name;
	}
}
