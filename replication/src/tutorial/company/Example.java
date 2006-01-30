package company;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.replication.*;
import org.hibernate.cfg.Configuration;

public class Example {
	public static void main(String[] args) {

		//Open the db4o database
		ObjectContainer objectContainer = Db4o.openFile("company.yap");

		//Read the Hibernate Config file (in the classpath)
		Configuration hibernateConfiguration = new Configuration().configure("hibernate.cfg.xml");

		//Start a Replication Session
		ReplicationSession replication = Replication.begin(objectContainer, hibernateConfiguration);

		//Query for changed objects
		ObjectSet changedObjects = replication.providerB().objectsChangedSinceLastReplication();

		//One-line-of-code replication
		while (changedObjects.hasNext())
			replication.replicate(changedObjects.next());

		//Commit
		replication.commit();
	}


}
