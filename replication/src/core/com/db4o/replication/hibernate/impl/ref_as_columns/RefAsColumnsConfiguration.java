package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.replication.hibernate.cfg.RefConfig;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;

public class RefAsColumnsConfiguration extends RefConfig {
	private static HashMap<Configuration, RefAsColumnsConfiguration> cache = new HashMap<Configuration, RefAsColumnsConfiguration>();

	public static RefAsColumnsConfiguration produce(Configuration cfg) {
		RefAsColumnsConfiguration exist = cache.get(cfg);
		if (exist != null)
			return exist;

		RefAsColumnsConfiguration rc = new RefAsColumnsConfiguration(cfg);
		cache.put(cfg, rc);
		return rc;
	}

	private RefAsColumnsConfiguration(Configuration aCfg) {
		configuration = aCfg;
		init();
	}
}
