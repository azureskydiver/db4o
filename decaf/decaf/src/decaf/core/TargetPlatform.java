/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package decaf.core;



public enum TargetPlatform {
	
	NONE {
		@Override
		public DecafConfiguration config() {
			return new DecafConfiguration();
		}
		
		@Override
		public String appendPlatformId(String orig, String separator) {
			return orig;
		}
	},
	JDK11 {
		@Override
		public DecafConfiguration config() {
			return DecafConfiguration.forJDK11();
		}
	},
	JDK12 {
		@Override
		public DecafConfiguration config() {
			return DecafConfiguration.forJDK12();
		}
	};
	
	public String appendPlatformId(String orig, String separator) {
		return orig + separator + platformId();
	}

	private String platformId() {
		return toString().toLowerCase();
	}

	public abstract DecafConfiguration config();
}
