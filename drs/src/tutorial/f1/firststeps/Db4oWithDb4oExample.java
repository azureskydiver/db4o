package f1.firststeps;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.Replication;
import com.db4o.drs.ReplicationSession;

import java.io.File;

public class Db4oWithDb4oExample {
	public static void main(String[] args) {
		Pilot pilot1 = new Pilot("Scott Felton", 200);
		Pilot pilot2 = new Pilot("Frank Green", 120);

		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);

		ObjectContainer source = Db4o.openFile("source.yap");

		source.set(pilot1);
		source.set(pilot2);

		ObjectContainer destination = Db4o.openFile("destination.yap");

		ReplicationSession session = Replication.begin(source, destination);

		ObjectSet changedInA = session.providerA().objectsChangedSinceLastReplication();
		while (changedInA.hasNext())
			session.replicate(changedInA.next());

		ObjectSet changedInB = session.providerB().objectsChangedSinceLastReplication();
		while (changedInB.hasNext())
			session.replicate(changedInB.next());

		session.commit();
		session.close();

		source.close();
		destination.close();

		new File("source.yap").delete();
		new File("destination.yap").delete();
	}
}

