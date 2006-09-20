package f1.collection.map;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.HibernateReplication;
import com.db4o.ext.ExtDb4o;

import f1.collection.Car;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.util.HashMap;

public class MapExample {
	public static void main(String[] args) {
		new File("MapExample.yap").delete();

		System.out.println("Running Map example.");

		ExtDb4o.configure().generateUUIDs(Integer.MAX_VALUE);
		ExtDb4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		ObjectContainer objectContainer = Db4o.openFile("MapExample.yap");

		Pilot pilot = new Pilot();
		pilot.name = "John";

		Car car1 = new Car();
		car1.brand = "BMW";
		car1.model = "M3";

		Car car2 = new Car();
		car2.brand = "Mercedes Benz";
		car2.model = "S600SL";

		pilot.cars = new HashMap();
		pilot.cars.put("car1", car1);
		pilot.cars.put("car2", car2);

		objectContainer.set(pilot);
		objectContainer.commit();

		Configuration config = new Configuration().configure("f1/collection/map/hibernate.cfg.xml");

		ReplicationSession replication = HibernateReplication.begin(objectContainer, config);
		ObjectSet changed = replication.providerA().objectsChangedSinceLastReplication();

		while (changed.hasNext())
			replication.replicate(changed.next());

		replication.commit();
		replication.close();
		objectContainer.close();

		new File("MapExample.yap").delete();
	}
}