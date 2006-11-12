/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

/**
 * Configuration for a defragmentation run.
 * 
 * @see Defragment
 */
public class DefragmentConfig {
	
	public static final boolean DEBUG = false;
	
	public final static String BACKUP_SUFFIX="backup";
	public final static String MAPPING_SUFFIX="mapping";
	
	private String _origPath;
	private String _backupPath;
	private String _mappingPath;
	
	private StoredClassFilter _storedClassFilter=null;
	private boolean _forceBackupDelete=false;

	/**
	 * Creates a configuration for a defragmentation run. The backup and mapping
	 * file paths are generated from the original path by appending the default
	 * suffixes. All properties other than the provided paths are set to FALSE
	 * by default.
	 * 
	 * @param origPath The path to the file to be defragmented. Must exist and must be
	 *         a valid yap file.
	 */
	public DefragmentConfig(String origPath) {
		this(origPath,origPath+"."+BACKUP_SUFFIX);
	}

	/**
	 * Creates a configuration for a defragmentation run. The mapping file path
	 * is generated from the original path by appending the default suffix.
	 * All properties other than the provided paths are set to FALSE by default.
	 * 
	 * @param origPath The path to the file to be defragmented. Must exist and must be
	 *         a valid yap file.
	 * @param backupPath The path to the backup of the original file. No file should
	 *         exist at this position, otherwise it will be OVERWRITTEN if forceBackupDelete()
	 *         is set to true!
	 */
	public DefragmentConfig(String origPath, String backupPath) {
		this(origPath,backupPath,origPath+"."+MAPPING_SUFFIX);
	}

	/**
	 * Creates a configuration for a defragmentation run. All properties other
	 * than the provided paths are set to FALSE by default.
	 * 
	 * @param origPath The path to the file to be defragmented. Must exist and must be
	 *         a valid yap file.
	 * @param backupPath The path to the backup of the original file. No file should
	 *         exist at this position, otherwise it will be OVERWRITTEN if forceBackupDelete()
	 *         is set to true!
	 * @param mappingPath The path for an intermediate mapping file used internally.
	 *         No file should exist at this position, otherwise it will be DELETED!
	 */
	public DefragmentConfig(String origPath, String backupPath,String mappingPath) {
		_origPath = origPath;
		_backupPath = backupPath;
		_mappingPath = mappingPath;
	}

	public String origPath() {
		return _origPath;
	}

	public String backupPath() {
		return _backupPath;
	}

	public String mappingPath() {
		return _mappingPath;
	}
	
	public StoredClassFilter storedClassFilter() {
		return (_storedClassFilter==null ? NULLFILTER : _storedClassFilter);
	}
	
	public void storedClassFilter(StoredClassFilter storedClassFilter) {
		_storedClassFilter=storedClassFilter;
	}

	public boolean forceBackupDelete() {
		return _forceBackupDelete;
	}
	
	public void forceBackupDelete(boolean forceBackupDelete) {
		_forceBackupDelete=forceBackupDelete;
	}
	
	static class NullFilter implements StoredClassFilter {
		public boolean accept(StoredClass storedClass) {
			return true;
		}
	}
	
	private final static StoredClassFilter NULLFILTER=new NullFilter();
	
	public static Configuration db4oConfig(){
		Configuration config = Db4o.newConfiguration();
		config.weakReferences(false);
		return config;
	}
	
}
