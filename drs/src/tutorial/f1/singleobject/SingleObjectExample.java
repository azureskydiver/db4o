package f1.singleobject;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.ExtDb4o;
import com.db4o.replication.ReplicationSession;
import com.db4o.replication.hibernate.HibernateReplication;

import org.hibernate.cfg.Configuration;

import java.io.File;

public class SingleObjectExample {

	public static void main(String[] args) {
		new File("SingleObjectExample.yap").delete();

		System.out.println("Running SingleObjectExample example.");
		ExtDb4o.configure().generateUUIDs(Integer.MAX_VALUE);
		ExtDb4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		ObjectContainer objectContainer = Db4o.openFile("SingleObjectExample.yap");

		Pilot pilot = new Pilot();
		pilot.name = "John";
		pilot.points = 9;
		objectContainer.set(pilot);
		objectContainer.commit();

		Configuration config = new Configuration().configure("f1/singleobject/hibernate.cfg.xml");

		ReplicationSession replication = HibernateReplication.begin(objectContainer, config);
		ObjectSet it = replication.providerA().objectsChangedSinceLastReplication();

		while (it.hasNext()) {
			Object o = it.next();
			replication.replicate(o);
		}

		replication.commit();

		objectContainer.close();

		new File("SingleObjectExample.yap").delete();
	}
}
