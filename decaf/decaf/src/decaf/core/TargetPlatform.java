/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package decaf.core;



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
	},
	JDK11 {
		@Override
		public DecafConfiguration defaultConfig() {
			return DecafConfiguration.forJDK11();
		}
	},
	JDK12 {
		@Override
		public DecafConfiguration defaultConfig() {
			return DecafConfiguration.forJDK12();
		}
	};
	
	public String appendPlatformId(String orig, String separator) {
		return orig + separator + platformId();
	}

	private String platformId() {
		return toString().toLowerCase();
	}

	public abstract DecafConfiguration defaultConfig();

	public boolean isNone() {
		return false;
	}
	
	public boolean isJDK11() {
		return isNone() || this.equals(JDK11);
	}
}
