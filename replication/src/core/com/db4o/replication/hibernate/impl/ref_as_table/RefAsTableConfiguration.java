package com.db4o.replication.hibernate.impl.ref_as_table;

import com.db4o.replication.hibernate.cfg.RefConfig;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;

public class RefAsTableConfiguration extends RefConfig {
// ------------------------------ FIELDS ------------------------------

	private static HashMap<Configuration, RefAsTableConfiguration> cache = new HashMap<Configuration, RefAsTableConfiguration>();

// -------------------------- STATIC METHODS --------------------------

	public static RefAsTableConfiguration produce(Configuration cfg) {
		RefAsTableConfiguration exist = RefAsTableConfiguration.cache.get(cfg);
		if (exist != null)
			return exist;

		RefAsTableConfiguration rc = new RefAsTableConfiguration(cfg);
		RefAsTableConfiguration.cache.put(cfg, rc);
		return rc;
	}

// --------------------------- CONSTRUCTORS ---------------------------

	private RefAsTableConfiguration(Configuration aCfg) {
		configuration = aCfg;
		init();
	}

	protected void addClasses() {
		super.addClasses();
		Util.addClass(configuration, ObjectReference.class);
	}
}
