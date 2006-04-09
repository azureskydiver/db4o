package com.db4o.replication;

import com.db4o.replication.hibernate.ObjectLifeCycleEventsListener;
import com.db4o.replication.hibernate.impl.ObjectLifeCycleEventsListenerImpl;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;


/**
 * Configures Hibernate object update listeners to
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
 * @version 1.2
 * @since dRS 1.0
 */
public class ReplicationConfigurator {
	private static ObjectLifeCycleEventsListener refAstablestListener = new ObjectLifeCycleEventsListenerImpl();

	/**
	 * Registers object update event listeners to Configuration.
	 * If required drs tables do not exist, create them automatically.
	 * <p/> This method must be called before calling Configuration.buildSessionFactory();
	 *
	 * @param cfg a properly configured Configuration
	 */
	public static void configure(Configuration cfg) {
		refAstablestListener.configure(cfg);
	}

	/**
	 * Install an opened session with this object so as to enbable the update event listeners.
	 * <p/> This method must be called just after calling sessionFactory.openSession();
	 *
	 * @param s   a just opened Session
	 * @param cfg a Configuration that has previously been passed to ReplicationConfigurator.configure();
	 */
	public static void install(Session s, Configuration cfg) {
		refAstablestListener.install(s, cfg);
	}
}
