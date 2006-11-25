package com.db4o.objectManager.v2.maint;

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

	public DefragTask(Db4oConnectionSpec connectionSpec) throws IOException {
		DefragmentConfig config = new DefragmentConfig(connectionSpec.getFullPath());
		config.forceBackupDelete(true);
		//if(true)throw new IOException("test");
		Defragment.defrag(config);

	}
}
