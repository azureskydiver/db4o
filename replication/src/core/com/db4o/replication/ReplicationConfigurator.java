package com.db4o.replication;

import com.db4o.replication.hibernate.UpdateEventListener;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsUpdateEventListener;
import com.db4o.replication.hibernate.ref_as_table.ObjectInsertedListener;
import com.db4o.replication.hibernate.ref_as_table.RefAsTableUpdateEventListener;
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
	private static UpdateEventListener refAstablestListener = new RefAsTableUpdateEventListener();

	private static ObjectInsertedListener objectInsertedListener = new ObjectInsertedListener();

	/**
	 * Registers object update event listeners to Configuration.
	 * If required drs tables and columns do not exist, create them automatically.
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

	/**
	 * <b>Use this method in conjuction with the *Use drs without drs columns approach.*</b>
	 *
	 * @param cfg a properly configured Configuration
	 * @see #configure(org.hibernate.cfg.Configuration)
	 */
	public static void refAsTableConfigure(Configuration cfg) {
		refAstablestListener.configure(cfg);
		objectInsertedListener.configure(cfg);
	}

	/**
	 * <b>Use this method in conjuction with the *Use drs without drs columns approach.*</b>
	 *
	 * @param s   a just opened Session
	 * @param cfg a Configuration that has previously been passed to ReplicationConfigurator.configure();
	 * @see #install(org.hibernate.Session, org.hibernate.cfg.Configuration)
	 */
	public static void refAsTableInstall(Session s, Configuration cfg) {
		refAstablestListener.install(s, cfg);
	}
}
