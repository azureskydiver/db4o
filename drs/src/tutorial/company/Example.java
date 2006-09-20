package company;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.drs.ObjectState;
import com.db4o.drs.ReplicationEvent;
import com.db4o.drs.ReplicationEventListener;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.hibernate.HibernateReplication;

import org.hibernate.cfg.Configuration;

import java.util.List;

public class Example {
	public static void main(String[] args) {

		//Open the db4o database
		ObjectContainer objectContainer = Db4o.openFile("company.yap");

		//Read the Hibernate Config file (in the classpath)
		Configuration hibernateConfiguration = new Configuration().configure("hibernate.cfg.xml");

//Start a Replication Session
		ReplicationEventListener listener;
		listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent event) {
				if (event.stateInProviderA().getObject() instanceof List)
					event.stopTraversal();
			}
		};

		listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent event) {
				if (event.isConflict()) {
					ObjectState chosenObjectState = event.stateInProviderA();
					event.overrideWith(chosenObjectState);
				}
			}
		};

		ReplicationSession replication = HibernateReplication.begin(objectContainer, hibernateConfiguration, listener);

		//Query for changed objects
		ObjectSet changedObjects = replication.providerB().objectsChangedSinceLastReplication();

		//One-line-of-code replication
		while (changedObjects.hasNext())
			replication.replicate(changedObjects.next());

		//Commit
		replication.commit();
	}


}
