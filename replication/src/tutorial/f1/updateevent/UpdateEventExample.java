package f1.updateevent;

import com.db4o.replication.hibernate.ReplicationConfigurator;

import f1.collection.Car;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class UpdateEventExample {
	public static void main(String[] args) {
		System.out.println("Running UpdateEvent example.");

		Pilot pilot = new Pilot();
		pilot.name = "John";

		Configuration cfg = new Configuration().configure("f1/updateevent/hibernate.cfg.xml");
		ReplicationConfigurator.configure(cfg);

		SessionFactory sessionFactory = cfg.buildSessionFactory();
		Session session = sessionFactory.openSession();
		ReplicationConfigurator.install(session, cfg);

		session.save(pilot);

		pilot.name = "changed";
		session.flush();

		Car car1 = new Car();
		car1.brand = "BMW";
		car1.model = "M3";

		pilot.cars.add(car1);
		session.flush();

		Car car2 = new Car();
		car2.brand = "Mercedes Benz";
		car2.model = "S600SL";
		pilot.cars.add(car2);
		session.flush();

		pilot.cars.remove(car2);
		session.flush();

		session.close();
	}
}