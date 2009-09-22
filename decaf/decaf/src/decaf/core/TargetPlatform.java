/* Copyright (C) 2008   Versant Inc.   http://www.db4o.com */

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

		@Override
		public CompilerSettings compilerSettings() {
			return new CompilerSettings("1.3", "1.1");
		}
	},
	ANDROID {
		@Override
		public DecafConfiguration defaultConfig() {
			return NONE.defaultConfig();
		}

		@Override
		public IterablePlatformMapping iterablePlatformMapping() {
			throw new IllegalStateException();
		}

		@Override
		public CompilerSettings compilerSettings() {
			return new CompilerSettings("1.5", "1.5");
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

		@Override
		public CompilerSettings compilerSettings() {
			return NONE.compilerSettings();
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

		@Override
		public CompilerSettings compilerSettings() {
			return NONE.compilerSettings();
		}
	},	
	SHARPEN {
		@Override
		public DecafConfiguration defaultConfig() {
			return NONE.defaultConfig();
		}

		@Override
		public IterablePlatformMapping iterablePlatformMapping() {
			throw new IllegalStateException();
		}
		
		@Override
		public CompilerSettings compilerSettings() {
			return new CompilerSettings("1.5", "1.5");
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

	public abstract CompilerSettings compilerSettings();

	public static class CompilerSettings {

		public CompilerSettings(String source, String codeGenTargetPlatform) {
			this.source = source;
			this.codeGenTargetPlatform = codeGenTargetPlatform;
		}
		
		public final String source;
		public final String codeGenTargetPlatform;
	}
	
}
