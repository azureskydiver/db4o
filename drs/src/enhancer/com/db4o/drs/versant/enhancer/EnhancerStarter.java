package com.db4o.drs.versant.enhancer;

import java.io.*;
import java.util.*;

import com.db4o.util.*;

public class EnhancerStarter {
	
	private static final String CONNECTION_URL_KEY = "javax.jdo.option.ConnectionURL";
	
	private static final String JDO_METADATA_KEY = "versant.metadata."; 


	private Properties _properties = new Properties();

	
	public EnhancerStarter(String agentArguments) {
		
		addPropertyIfNotExists(CONNECTION_URL_KEY, "versant:test@localhost");
		addPropertyIfNotExists("javax.jdo.PersistenceManagerFactoryClass","com.versant.core.jdo.BootstrapPMF");


		for(String p : agentArguments.split(",")) {
			String packageName = p.replace('.', '/');
			addJdoMetaDataFile(packageName+"/package.jdo");
		}
		
	}
	
	private void addPropertyIfNotExists(String key, String value) {
		if(_properties.containsKey(key)){
			return;
		}
		_properties.setProperty(key, value);
	}

	private void addJdoMetaDataFile(String fileName) {
		
		System.out.println("Adding to class definition: " + fileName);
		try {
			Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName).close();
		} catch (IOException e) {
			System.out.println("      couldnt load it: " + e.getMessage());
		}
		
		final int maxMetadataEntries = 1000;
		final int quitSearchingAfterGap = 5;
		final int notSet = -1;
		
		int freeEntry = notSet;
		int lastOccupied = notSet;
		
		for (int i = 0; i < maxMetadataEntries; i++) {
			String property = _properties.getProperty(JDO_METADATA_KEY + i);
			if(property == null){
				if(freeEntry == notSet){
					freeEntry = i;
				}
				if(i - lastOccupied > quitSearchingAfterGap){
					break;
				}
			} else {
				lastOccupied = i;
				if(fileName.equals(property)){
					return;
				}
			}
		}
		_properties.setProperty(JDO_METADATA_KEY + freeEntry, fileName);
	}

	public void enhance() {
		
		String tempFileName = Path4.getTempFileName();
		File tempFile = new File(tempFileName);
		try{
			FileOutputStream out = new FileOutputStream(tempFile);
			if(EnhancerDebug.verbose){
				_properties.store(System.err, null);
			}
			_properties.store(out, null);
			out.close();
			
			String[] args = new String[]{tempFile.getAbsolutePath()};
			
			ProcessResult processResult = JavaServices.java("com.db4o.drs.versant.enhancer.EnhancerMain", args);
			if(EnhancerDebug.verbose){
				System.out.println(processResult);
			}
		} catch(IOException ioe){
			throw new RuntimeException(ioe);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			tempFile.delete();
		}

	}
}
