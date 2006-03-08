package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.RefConfig;
import org.hibernate.cfg.Configuration;

import java.util.HashMap;

public class RefAsTableConfiguration extends RefConfig {
	private static HashMap<Configuration, RefAsTableConfiguration> cache = new HashMap<Configuration, RefAsTableConfiguration>();

	public static RefAsTableConfiguration produce(Configuration cfg) {
		RefAsTableConfiguration exist = RefAsTableConfiguration.cache.get(cfg);
		if (exist != null)
			return exist;

		RefAsTableConfiguration rc = new RefAsTableConfiguration(cfg);
		RefAsTableConfiguration.cache.put(cfg, rc);
		return rc;
	}

	private RefAsTableConfiguration(Configuration aCfg) {
		configuration = aCfg;
		init();
	}

	protected void addClasses() {
		super.addClasses();
		addClass(ReplicationReference.class);
	}
}
