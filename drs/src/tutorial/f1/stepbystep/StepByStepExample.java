package f1.stepbystep;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.HibernateReplication;
import com.db4o.drs.hibernate.ReplicationConfigurator;
import com.db4o.ext.ExtDb4o;
import com.db4o.query.Predicate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import java.io.File;

public class StepByStepExample {
	protected String db4oFileName = "StepByStepExample.yap";
	protected final String hibernateConfigurationFileName = "f1/stepbystep/hibernate.cfg.xml";

	public static void main(String[] args) {
		new StepByStepExample().run();
	}

	public void run() {
		new File(db4oFileName).delete();

		System.out.println("Running StepByStep Example.");

		System.out.println("Configuring db4o to generate uuids and version numbers for objects.");
		ExtDb4o.configure().generateUUIDs(Integer.MAX_VALUE);
		ExtDb4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		storePilotAndCarToDb4o();

		replicateAllToHibernate();

		modifyPilotInHibernate();

		replicateChangesToDb4o();

		modifyPilotAndCarInDb4o();

		replicatePilotToHibernate();

		System.out.println("StepByStep Example completed");

		new File(db4oFileName).delete();
	}

	private void storePilotAndCarToDb4o() {
		System.out.println("Creating pilot and car objects");
		System.out.println("Car brand = BMW, model = M3");
		Car car = new Car("BMW", "M3");

		System.out.println("Pilot name = John");
		Pilot pilot = new Pilot("John", car);

		System.out.println("Opening the db4o database");
		ObjectContainer db4o = Db4o.openFile(db4oFileName);
		System.out.println("Saving pilot and car to db4o");
		db4o.set(pilot);
		db4o.commit();
		db4o.close();
		System.out.println("Committed and closed db4o");
	}

	private void replicateAllToHibernate() {
		System.out.println("Re-opening db4o");
		ObjectContainer db4o = Db4o.openFile(db4oFileName);

		System.out.println("Reading the Hibernate configuration file");
		Configuration hibernate = new Configuration().configure(hibernateConfigurationFileName);

		System.out.println("Starting the first round of replication between db4o and Hibernate");
		ReplicationSession replication = HibernateReplication.begin(db4o, hibernate);

		ObjectSet allObjects = replication.providerA().objectsChangedSinceLastReplication();
		System.out.println("Iterating all objects changed in db4o, replicating them to Hibernate");
		while (allObjects.hasNext())
			replication.replicate(allObjects.next());

		replication.commit();
		System.out.println("Commited the replication.");
		replication.close();
		db4o.close();
		System.out.println("Replication is successful.");
	}

	private void modifyPilotInHibernate() {
		System.out.println("Reading the Hibernate configuration file");
		Configuration cfg = new Configuration().configure(hibernateConfigurationFileName);

		System.out.println("Configuring the Hibernate Configuration to listen to object update events");
		ReplicationConfigurator.configure(cfg);

		System.out.println("Creating a SessionFactory");
		SessionFactory sessionFactory = cfg.buildSessionFactory();

		System.out.println("Creating a Session");
		Session session = sessionFactory.openSession();

		System.out.println("Installing the object update events listeners to the session");
		ReplicationConfigurator.install(session, cfg);

		Transaction tx = session.beginTransaction();

		System.out.println("Finding Pilot John in Hibernate.");
		Pilot john = (Pilot) session.createCriteria(Pilot.class) .add(Restrictions.eq("name", "John")).list().get(0);

		System.out.println("Changing the name of Pilot from 'John' to 'Anna'");
		john.name = "Anna";

		session.flush();    //Remember to call flush() before commit()
		tx.commit();

		session.close();
		sessionFactory.close();

		System.out.println("The change is commited and the version number of Anna is incremented.");

	}

	private void replicateChangesToDb4o() {
		System.out.println("Re-opening db4o");
		ObjectContainer db4o = Db4o.openFile(db4oFileName);

		System.out.println("Reading the Hibernate configuration file");
		Configuration hibernate = new Configuration().configure(hibernateConfigurationFileName);

		System.out.println("Starting the second round of replication between db4o and Hibernate");
		ReplicationSession replication = HibernateReplication.begin(db4o, hibernate);

		ObjectSet allObjects = replication.providerB().objectsChangedSinceLastReplication();
		System.out.println("Iterating all objects changed in Hibernate, replicating them to db4o");
		while (allObjects.hasNext())
			replication.replicate(allObjects.next());

		replication.commit();
		System.out.println("Commited the replication.");
		replication.close();
		db4o.close();
		System.out.println("Replication is successful.");
	}

	private void modifyPilotAndCarInDb4o() {
		System.out.println("Re-opening the db4o database");
		ObjectContainer db4o = Db4o.openFile(db4oFileName);

		System.out.println("Finding Pilot 'Anna' in Db4o.");

		Pilot anna = db4o.query(new Predicate<Pilot>() {
			public boolean match(Pilot p) {
				return p.name.equals("Anna");
			}
		}).next();

		System.out.println("Changing the name of Pilot from 'Anna' to 'Eric' ");
		anna.name = "Eric";
		System.out.println("Changing the brand of Car from 'BMW' to 'Honda' and model from 'M3' to 'Fit' ");
		anna.car.brand = "Honda";
		anna.car.brand = "Fit";
		db4o.set(anna);

		db4o.commit();
		db4o.close();
		System.out.println("Committed and closed db4o");
	}

	private void replicatePilotToHibernate() {
		System.out.println("Re-opening db4o");
		ObjectContainer db4o = Db4o.openFile(db4oFileName);

		System.out.println("Reading the Hibernate configuration file");
		Configuration hibernate = new Configuration().configure(hibernateConfigurationFileName);

		System.out.println("Starting the final round of replication between db4o and Hibernate");
		ReplicationSession replication = HibernateReplication.begin(db4o, hibernate);

		ObjectSet allObjects = replication.providerA().objectsChangedSinceLastReplication(Pilot.class);
		System.out.println("Iterating all Pilots changed in db4o, replicating them to Hibernate");
		while (allObjects.hasNext())
			replication.replicate(allObjects.next());

		replication.commit();
		System.out.println("Commited the replication.");
		replication.close();
		db4o.close();

		System.out.println("Replication is successful.");
	}
}
