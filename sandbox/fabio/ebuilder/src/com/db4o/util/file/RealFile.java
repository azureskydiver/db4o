package com.db4o.util.file;

import java.io.*;

public class RealFile implements IFile {

	private final RealFile parent;
	private final File realFile;
	
	public RealFile(RealFile parent, File file) {
		this.parent = parent;
		this.realFile = file;
	}
	
	public RealFile(String name) {
		this(new File(name));
	}
	
	public RealFile(File file) {
		this(null, file);
	}
	
	@Override
	public String toString() {
		return "RealFile["+getAbsolutePath()+"]";
	}

	public IFile file(String name) {
		
		int t = name.indexOf('/');
		
		if (t != -1) {
			String first = name.substring(0, t);
			return (t == 0 ? this : file(first)).file(name.substring(t+1));
		}
		
		if ("..".equals(name)) {
			return parent;
		}
		
		return new RealFile(this, new File(realFile(), name));
	}

	public XMLParser xml() {
		return new XMLParserImpl(this);
	}

	private File realFile() {
		return realFile;
	}

	public InputStream openInputStream() {
		mkParents();
		try {
			return new FileInputStream(realFile());
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void mkParents() {
		if (parent != null) {
			parent.mkdir();
		}
	}

	private void mkdir() {
		if (parent != null) {
			parent.mkdir();
		}
		if (!realFile().exists()) {
			realFile().mkdirs();
		}
	}

	public String getAbsolutePath() {
		try {
			return realFile().getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
//		return file.getAbsolutePath();
	}

	public String name() {
		return realFile().getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((realFile == null) ? 0 : realFile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RealFile other = (RealFile) obj;
		if (realFile == null) {
			if (other.realFile != null)
				return false;
		} else if (!realFile.equals(other.realFile))
			return false;
		return true;
	}

	@Override
	public OutputStream openOutputStream(boolean append) {
		mkParents();

		try {
			return new FileOutputStream(realFile(), append);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RandomAccessBuffer asBuffer() {
		mkParents();

		try {
			return new RandomAccessRealFile(realFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	

}
