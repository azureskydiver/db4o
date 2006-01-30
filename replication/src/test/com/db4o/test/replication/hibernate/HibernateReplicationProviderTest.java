package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.*;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.provider.Car;
import com.db4o.test.replication.provider.Pilot;
import com.db4o.test.replication.provider.ReplicationProviderTest;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

import java.io.File;

public class HibernateReplicationProviderTest extends ReplicationProviderTest {
	
    protected TestableReplicationProviderInside prepareSubject() {
		Configuration configuration = createConfig();

		return new HibernateReplicationProviderImpl(configuration);
	}

	protected Configuration createConfig() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();

		configuration.addClass(Car.class);
		configuration.addClass(Pilot.class);
		return configuration;
	}

	public void testReplicationProvider() {
		clean();
		super.testReplicationProvider();
		tstProduceReferenceByUUID();
	}

	private void tstProduceReferenceByUUID() {
		Configuration configuration = createConfig();
		Session session = configuration.buildSessionFactory().openSession();
		Car car = new Car();
		session.save(car);
		TestableReplicationProviderInside subject = prepareSubject();

		//subject.stor

	}


	private void clean() {
		File hsqlDir = new File("hsql");
		if (hsqlDir.exists()) {
			if (!deleteDir(hsqlDir)) {
				System.out.println("Cannot delete" + hsqlDir);
			} else {
				System.out.println(hsqlDir + " deleted");
			}
		}
	}

	/**
	 * Deletes all files and subdirectories under dir. Returns true if all
	 * deletions were successful. If a deletion fails, the method stops attempting
	 * to delete and returns false.
	 *
	 * @param dir
	 */
	protected static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}

	protected boolean subjectSupportsRollback() {
		return true;
	}
}