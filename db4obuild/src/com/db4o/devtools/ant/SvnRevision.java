/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.devtools.ant;

import java.io.*;

import org.apache.tools.ant.*;

/**
 * Gets the svn revision from a specified resource and puts it
 * into the specified property.
 * 
 * If no resource is specified then the project's directory is
 * assumed to be the resource.
 * 
 * The 'svn' command is assumed to be in the path. Alternatively
 * the path to the svn utility can be specified through the
 * 'svn.exe' build property. 
 */
public class SvnRevision extends Task {
	
	private String _property;
	private File _resource;
	
	public void setProperty(String property) {
		_property = property;
	}
	
	public void setResource(File resource) {
		_resource = resource;
	}
	
	@Override
	public void execute() throws BuildException {
		try {
			getProject().setProperty(_property, resourceRevision());
		} catch (IOException e) {
			throw new BuildException(e, getLocation());
		}
	}

	private String resourceRevision() throws IOException {
		final Process p = svnInfo();
		final String revision = scanForRevision(stdout(p));
		if (null != revision) return revision;
		
		throw new BuildException("Revision not found.", getLocation());
	}

	private BufferedReader stdout(final Process p) {
		return new BufferedReader(new InputStreamReader(p.getInputStream()));
	}

	private String scanForRevision(final BufferedReader reader)
			throws IOException {
		String line = null;
		while (null != (line = reader.readLine())) {
			final String prefix = "Revision: ";
			if (line.startsWith(prefix)) {
				return line.substring(prefix.length());
			}
		}
		return null;
	}

	private Process svnInfo() throws IOException {
		String[] cmd = new String[] {
			svn(), "info", resourceName() 
		};
		final Process p = Runtime.getRuntime().exec(cmd, null, resourceDir());
		return p;
	}

	private File resourceDir() {
		if (null == _resource) return getProject().getBaseDir();
		return _resource.getParentFile();
	}

	private String resourceName() {
		if (_resource == null) return ".";
		return _resource.getName();
	}

	private String svn() {
		final String svn = getProject().getProperty("svn.exe");
		if (null != svn) return svn;
		return "svn";
	}

}
