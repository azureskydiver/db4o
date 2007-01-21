package drs30;

import java.io.File;

import org.hibernate.cfg.Configuration;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.HibernateReplication;
import com.db4o.ext.ExtDb4o;

class Drs30 {
	public static void main(String[] args) {
		clean();
		
		configure();

		try {
			roundOne();
			roundTwo();
		} finally {
			clean();
		}
	}

	private static void configure() {
		ExtDb4o.configure().generateUUIDs(Integer.MAX_VALUE);
		ExtDb4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
	}

	private static void roundTwo() {
		ObjectContainer desktop = openDesktop();

		org.hibernate.cfg.Configuration hibernate = new Configuration()
				.configure("drs30/hibernate.cfg.xml");
		ReplicationSession session = HibernateReplication.begin(desktop,
				hibernate);
		ObjectSet changed = session.providerA()
				.objectsChangedSinceLastReplication();
		while (changed.hasNext()) {
			session.replicate(changed.next());
		}
		
		session.commit();
		session.close();

		desktop.close();
	}

	private static void clean() {
		new File("handheld.yap").delete();
		new File("desktop.yap").delete();
	}

	public static void roundOne() {
		Pilot pilot1 = new Pilot("Scott Felton", 200);
		Pilot pilot2 = new Pilot("Frank Green", 120);

		ObjectContainer handheld = Db4o.openFile("handheld.yap");

		handheld.set(pilot1);
		handheld.set(pilot2);

		ObjectContainer desktop = openDesktop();

		ReplicationSession session = Replication.begin(handheld, desktop);

		ObjectSet changedInA = session.providerA()
				.objectsChangedSinceLastReplication();
		while (changedInA.hasNext())
			session.replicate(changedInA.next());

		ObjectSet changedInB = session.providerB()
				.objectsChangedSinceLastReplication();
		while (changedInB.hasNext())
			session.replicate(changedInB.next());

		session.commit();
		session.close();

		handheld.close();
		desktop.close();
	}

	private static ObjectContainer openDesktop() {
		ObjectContainer desktop = Db4o.openFile("desktop.yap");
		return desktop;
	}
}