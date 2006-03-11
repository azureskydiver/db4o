package com.db4o.replication;

import com.db4o.replication.hibernate.UpdateEventListener;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsUpdateEventListener;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;


/**
 * Utility class to configure Hibernate object update listeners to
 * generate object version numbers in everyday day usage.
 * <p/>
 * Version numbers are required for replication to identify modified
 * objects.
 * <p/>
 * Please install the replication configuration as follows:
 * <pre>
 * // Read or create the Configuration as usual
 * Configuration cfg = new Configuration().configure("your-hibernate.cfg.xml");
 * // Let the ReplicationConfigurator adjust the configuration
 * ReplicationConfigurator.configure(cfg);
 * // Create the SessionFactory as usual
 * SessionFactory sessionFactory = cfg.buildSessionFactory();
 * // Create the Session as usual
 * Session session = sessionFactory.openSession();
 * // Let the ReplicationConfigurator install the listeners to the Session
 * ReplicationConfigurator.install(session, cfg);
 * </pre>
 *
 * @author Albert Kwan
 * @version 1.0
 * @since dRS 1.0
 */
public class ReplicationConfigurator {
	private static UpdateEventListener refAsColumnstListener = new RefAsColumnsUpdateEventListener();

	/**
	 * Registers object update event listeners to Configuration.
	 * Checks each entity table, if the version and uuid columns do not exist,
	 * this method will create them. This method will also create
	 * the "uuid Long part sequence table" if it does not exist.
	 * <p/> This method must be called before calling Configuration.buildSessionFactory();
	 *
	 * @param cfg a properly configured Configuration
	 */
	public static void configure(Configuration cfg) {
		refAsColumnstListener.configure(cfg);
	}

	/**
	 * Install an opened session with this object so as to enbable the update event listeners.
	 * <p/> This method must be called just after calling sessionFactory.openSession();
	 *
	 * @param s   a just opened Session
	 * @param cfg a Configuration that has previously been passed to ReplicationConfigurator.configure();
	 */
	public static void install(Session s, Configuration cfg) {
		refAsColumnstListener.install(s, cfg);
	}
}
