/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.config;

import java.io.IOException;

import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.ext.DatabaseReadOnlyException;
import com.db4o.foundation.NotSupportedException;
import com.db4o.internal.*;
import com.db4o.io.IoAdapter;

public class LocalConfigurationImpl implements LocalConfiguration {

	private final Config4Impl _config;

	public LocalConfigurationImpl(Config4Impl config) {
		_config = config;
	}

	public void addAlias(Alias alias) {
		_config.addAlias(alias);
	}

	public void removeAlias(Alias alias) {
		_config.removeAlias(alias);
	}

	public void blockSize(int bytes) {
		_config.blockSize(bytes);
	}

	public void databaseGrowthSize(int bytes) {
		_config.databaseGrowthSize(bytes);
	}

	public void disableCommitRecovery() {
		_config.disableCommitRecovery();
	}

	public FreespaceConfiguration freespace() {
		return _config.freespace();
	}

	public void generateUUIDs(ConfigScope setting) {
		_config.generateUUIDs(setting);
	}

	public void generateVersionNumbers(ConfigScope setting) {
		_config.generateVersionNumbers(setting);
	}

	public void io(IoAdapter adapter) throws GlobalOnlyConfigException {
		_config.io(adapter);
	}

	public IoAdapter io() {
		return _config.io();
	}

	public void lockDatabaseFile(boolean flag) {
		_config.lockDatabaseFile(flag);
	}

	public void reserveStorageSpace(long byteCount) throws DatabaseReadOnlyException, NotSupportedException {
		_config.reserveStorageSpace(byteCount);
	}

	public void blobPath(String path) throws IOException {
		_config.setBlobPath(path);
	}


}
