/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package decaf.core;

import decaf.config.*;




public enum TargetPlatform {
	
	NONE {
		@Override
		public DecafConfiguration defaultConfig() {
			return new DecafConfiguration();
		}
		
		@Override
		public String appendPlatformId(String orig, String separator) {
			return orig;
		}
		
		@Override
		public boolean isNone() {
			return true;
		}

		@Override
		public IterablePlatformMapping iterablePlatformMapping() {
			return IterablePlatformMapping.JDK12_ITERABLE_MAPPING;
		}
	},
	JDK11 {
		@Override
		public DecafConfiguration defaultConfig() {
			return DecafConfiguration.forJDK11();
		}

		@Override
		public IterablePlatformMapping iterablePlatformMapping() {
			return IterablePlatformMapping.JDK11_ITERABLE_MAPPING;
		}
	},
	JDK12 {
		@Override
		public DecafConfiguration defaultConfig() {
			return DecafConfiguration.forJDK12();
		}

		@Override
		public IterablePlatformMapping iterablePlatformMapping() {
			return IterablePlatformMapping.JDK12_ITERABLE_MAPPING;
		}
	};
	
	public String appendPlatformId(String orig, String separator) {
		return orig + separator + platformId();
	}

	private String platformId() {
		return toString().toLowerCase();
	}

	public abstract DecafConfiguration defaultConfig();

	public abstract IterablePlatformMapping iterablePlatformMapping();
	
	public boolean isNone() {
		return false;
	}

}
