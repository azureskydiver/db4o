package com.db4o.devtools.ant;

import java.io.File;
import com.db4o.devtools.ant.AssemblyInfo;


public class UpdateAssemblyInfoTask extends AbstractAssemblyInfoTask {

	private String _version;
	
	private File _keyFile;
	
	private String _configuration;
	
	private String _title;
	
	private String _product;
	
	private AssemblyInfo assemblyType = AssemblyInfo.DB4O();;
	
	public File getKeyFile() {
		return _keyFile;
	}
	
	public void setKeyFile(File keyFile) {
		_keyFile = keyFile;
	}
	
	public void setVersion(String version) {
		_version = version;
	}
	
	public void setConfiguration(String configuration) {
		_configuration = configuration;
	}
	
	public void setTitle(String title) {
		_title = title;
	}
	
	public void setProduct(String product) {
	    _product = product;
	}
	
	@Override
	protected String updateAttributes(String contents) {
	    if (_title != null) {
	        assemblyType = new AssemblyInfo(_title, _product);
	    }
		contents = updateAttribute(contents, "AssemblyTitle", assemblyType.title());
		contents = updateAttribute(contents, "AssemblyVersion", _version);
		contents = updateAttribute(contents, "AssemblyProduct", assemblyType.product());
		contents = updateAttribute(contents, "AssemblyCompany", AssemblyInfo.COMPANY);
		contents = updateAttribute(contents, "AssemblyCopyright", AssemblyInfo.COPYRIGHT);
		if (null != _keyFile) {
			contents = updateAttribute(contents, "AssemblyKeyFile", _keyFile.getAbsolutePath().replace('\\', '/'));
		}
		if (null != _configuration) {
			contents = updateAttribute(contents, "AssemblyConfiguration", _configuration);
			contents = updateAttribute(contents, "AssemblyDescription", assemblyType.title() + " " + _version + " (" + _configuration + ")");
		}
		return contents;
	}
}
