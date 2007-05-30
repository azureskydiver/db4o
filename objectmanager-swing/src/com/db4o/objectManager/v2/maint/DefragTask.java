package com.db4o.objectManager.v2.maint;

import com.db4o.Db4o;
import com.db4o.config.Configuration;
import com.db4o.config.DotnetSupport;
import com.db4o.defragment.DefragmentConfig;
import com.db4o.defragment.Defragment;
import com.db4o.objectmanager.model.Db4oConnectionSpec;

import java.io.IOException;

/**
 * User: treeder
 * Date: Nov 25, 2006
 * Time: 1:35:51 PM
 */
public class DefragTask {
	
	private static final int DEFAULT_OBJECT_COMMIT_FREQUENCY = 500000;

	public DefragTask(Db4oConnectionSpec connectionSpec) throws IOException {
		DefragmentConfig defragConfig = new DefragmentConfig(connectionSpec.getFullPath());

		defragConfig.db4oConfig(connectionSpec.newConfiguration());
		
		defragConfig.forceBackupDelete(true);
		defragConfig.objectCommitFrequency(DEFAULT_OBJECT_COMMIT_FREQUENCY);
		//if(true)throw new IOException("test");
		Defragment.defrag(defragConfig);

	}
}
