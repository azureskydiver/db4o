package com.db4o.devtools.ant;

import java.io.File;

public final class UpdateAssemblyKeyTask extends AbstractAssemblyInfoTask {

	private File _keyFile;
	
	public File getKeyFile() {
		return _keyFile;
	}
	
	public void setKeyFile(File keyFile) {
		this._keyFile = keyFile;
	}

	@Override
	protected String updateAttributes(String contents) {
		contents = updateAttribute(contents, "AssemblyKeyFile", getKeyFile().getAbsolutePath());
		return contents;
	}
	
}
