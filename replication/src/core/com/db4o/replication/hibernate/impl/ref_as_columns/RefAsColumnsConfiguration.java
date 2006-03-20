package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.replication.hibernate.cfg.RefConfig;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;

public class RefAsColumnsConfiguration extends RefConfig {
// ------------------------------ FIELDS ------------------------------

	private static HashMap<Configuration, RefAsColumnsConfiguration> cache = new HashMap<Configuration, RefAsColumnsConfiguration>();

// -------------------------- STATIC METHODS --------------------------

	public static RefAsColumnsConfiguration produce(Configuration cfg) {
		RefAsColumnsConfiguration exist = cache.get(cfg);
		if (exist != null)
			return exist;

		RefAsColumnsConfiguration rc = new RefAsColumnsConfiguration(cfg);
		cache.put(cfg, rc);
		return rc;
	}

// --------------------------- CONSTRUCTORS ---------------------------

	private RefAsColumnsConfiguration(Configuration aCfg) {
		configuration = aCfg;
		init();
	}
}
