package f1.collection.list;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.HibernateReplication;
import com.db4o.ext.ExtDb4o;

import f1.collection.Car;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.util.ArrayList;

public class ListExample {
	public static void main(String[] args) {
		new File("ListExample.yap").delete();

		System.out.println("Running List example.");

		ExtDb4o.configure().generateUUIDs(Integer.MAX_VALUE);
		ExtDb4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		ObjectContainer objectContainer = Db4o.openFile("ListExample.yap");

		Pilot pilot = new Pilot();
		pilot.name = "John";

		Car car1 = new Car();
		car1.brand = "BMW";
		car1.model = "M3";

		Car car2 = new Car();
		car2.brand = "Mercedes Benz";
		car2.model = "S600SL";

		pilot.cars = new ArrayList();
		pilot.cars.add(car1);
		pilot.cars.add(car2);

		objectContainer.set(pilot);
		objectContainer.commit();

		Configuration config = new Configuration().configure("f1/collection/list/hibernate.cfg.xml");

		ReplicationSession replication = HibernateReplication.begin(objectContainer, config);

		ObjectSet changed = replication.providerA().objectsChangedSinceLastReplication();

		while (changed.hasNext())
			replication.replicate(changed.next());

		replication.commit();
		replication.close();
		objectContainer.close();

		new File("ListExample.yap").delete();
	}
}