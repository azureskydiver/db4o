package com.db4o.omplus.datalayer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;

import com.db4o.omplus.crypto.UserDetailsEncrypter;
import com.db4o.omplus.datalayer.webservices.connection.UserWebServiceCredentials;


public class FileDataStore {
	
	private final static String USR_HOME_DIR = "user.home";
	private final static String OME_UI_STORE = "OMEUI.store";
	private static final String CKEY = "db4objects_OME_Eclipse_3.3.1_PlugIn";
	
	private static transient final File userInfoFile = new File(new File(System
			.getProperty(USR_HOME_DIR)), OME_UI_STORE);

	public void cacheUserCredentials(String userName, String password) throws Exception{
		if(userName != null && password != null){
			UserDetailsEncrypter userCodePasswordCrypter=new UserDetailsEncrypter(userName,password,CKEY);

			FileWriter fileWriter=new FileWriter(userInfoFile,false);
			fileWriter.write(userCodePasswordCrypter.getEncryptedString());

			fileWriter.flush();
			fileWriter.close();
		}
	}

	public UserWebServiceCredentials getCachedUserCredentials() throws Exception{

		FileReader fileReader=new FileReader(userInfoFile);
		LineNumberReader lineNumberReader=new LineNumberReader(fileReader);
		UserDetailsEncrypter userCodePasswordCrypter=new UserDetailsEncrypter(lineNumberReader.readLine(),CKEY);
		fileReader.close();
		lineNumberReader.close();
		UserWebServiceCredentials.resetInstance();
		UserWebServiceCredentials details = UserWebServiceCredentials.getInstance();
		
		if(userCodePasswordCrypter != null) {
			details.setPassword(userCodePasswordCrypter.getPassword());
			details.setUsername(userCodePasswordCrypter.getUserCode());
		}
		return details;
	}
	
	public static boolean isUserCredentialSaved(){
		return new File(userInfoFile.getAbsolutePath()).exists();
	}
	
	public static boolean deleteUserCredentials(){
		if(isUserCredentialSaved())
			try{
				return userInfoFile.delete();
			}
			catch(SecurityException ex){
				
			}
		return false;
	}

}
