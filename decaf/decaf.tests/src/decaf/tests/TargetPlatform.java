/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */

package decaf.tests;

import decaf.builder.*;

public enum TargetPlatform {
	
	NONE(null) {
		@Override
		public DecafConfiguration config() {
			return new DecafConfiguration();
		}
	},
	JDK11("jdk11") {
		@Override
		public DecafConfiguration config() {
			return DecafConfiguration.forJDK11();
		}
	},
	JDK12("jdk12") {
		@Override
		public DecafConfiguration config() {
			return DecafConfiguration.forJDK12();
		}
	};
	
	private String _fileIDPart;

	TargetPlatform(String fileIDPart) {
		_fileIDPart = fileIDPart;
	}
	
	public String fileIDPart() {
		return _fileIDPart;
	}

	public abstract DecafConfiguration config();
	
	public boolean hasFileIDPart() {
		return _fileIDPart != null;
	}
}
