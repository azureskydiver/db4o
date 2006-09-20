package f1.one_to_one;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.HibernateReplication;
import com.db4o.ext.ExtDb4o;

import org.hibernate.cfg.Configuration;

import java.io.File;

public class OneToOneExample {
	public static void main(String[] args) {
		new File("OneToOneExample.yap").delete();

		System.out.println("Running OneToOneExample example.");

		ExtDb4o.configure().generateUUIDs(Integer.MAX_VALUE);
		ExtDb4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		ObjectContainer objectContainer = Db4o.openFile("OneToOneExample.yap");

		Helmet helmet = new Helmet();
		helmet.model = "Robuster";

		Pilot pilot = new Pilot();
		pilot.name = "John";
		pilot.helmet = helmet;

		objectContainer.set(pilot);
		objectContainer.commit();

		Configuration config = new Configuration().configure("f1/one_to_one/hibernate.cfg.xml");

		ReplicationSession replication = HibernateReplication.begin(objectContainer, config);
		ObjectSet changed = replication.providerA().objectsChangedSinceLastReplication();

		while (changed.hasNext())
			replication.replicate(changed.next());

		replication.commit();
		replication.close();
		objectContainer.close();

		new File("OneToOneExample.yap").delete();
	}
}