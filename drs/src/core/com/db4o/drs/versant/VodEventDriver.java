/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant;

import java.io.*;

import com.db4o.drs.inside.*;
import com.db4o.util.*;
import com.db4o.util.IOServices.*;


public class VodEventDriver {

	private final String _databaseName;
	
	private final File _configFile;
	
	private ProcessRunner _process;

	public VodEventDriver(String databaseName, File configFile) {
		_databaseName = databaseName;
		_configFile = configFile;
	}

	public boolean start() {
		if(_process != null){
			throw new IllegalStateException();
		}
		try {
			_process = IOServices.start(VodDatabase.VED_DRIVER, new String[]{
					_databaseName, _configFile.getAbsolutePath() 
			});
		} catch (IOException e) {
			e.printStackTrace();
			destroyProcess();
			return false;
		}
		try{
			_process.checkIfStarted(_databaseName, 10000);
		} catch (RuntimeException ex){
			ex.printStackTrace();
			destroyProcess();
			return false;
		}
		return true;
	}

	private void destroyProcess() {
		if(_process != null){
			try{
				_process.destroy();
			} catch(RuntimeException rex){
				rex.printStackTrace();
			}
			if(DrsDebug.verbose){
				System.out.println(_process.processResult());
			}
			_process = null;
		}
	}

	public void stop() {
		if(_process == null){
			throw new IllegalStateException();
		}
		destroyProcess();
	}

}
