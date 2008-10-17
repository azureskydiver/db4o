/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;


public class SwitchingFilesFromClientTestCase extends ClientServerTestCaseBase {

	/**
	 * @deprecated using deprecated api
	 */
	public void testSwitch() {
        if(isMTOC()){
            // Cast to ExtClient won't work and switching files is 
            // not supported.
            return;
        }
		client().switchToFile(SwitchingFilesFromClientUtil.FILENAME_A);
		client().switchToFile(SwitchingFilesFromClientUtil.FILENAME_B);
		client().switchToMainFile();
		client().switchToFile(SwitchingFilesFromClientUtil.FILENAME_A);
		client().switchToFile(SwitchingFilesFromClientUtil.FILENAME_A);
	}
	
	protected void db4oSetupBeforeStore() throws Exception {
		SwitchingFilesFromClientUtil.deleteFiles();
	}

	protected void db4oTearDownAfterClean() throws Exception {
		SwitchingFilesFromClientUtil.deleteFiles();
	}
}
