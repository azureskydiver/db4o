package com.db4o.omplus.datalayer.webservices.connection;

/**
 * Singleton class that stores user's web service realted username and password info
 * 
 * @author prameela_nair
 *
 */
public class UserWebServiceCredentials 
{
	private String username;
	private String password;
	private static UserWebServiceCredentials instance;
	
	/**
	 * private constructor
	 */
	private UserWebServiceCredentials()
	{
		
	}
	
	public static UserWebServiceCredentials getInstance()
	{
		if(instance == null)
		{
			instance = new UserWebServiceCredentials();
		}	
		return instance;
	}
	
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Needed to create a new instance of this UserCredential
	 */
	public static void resetInstance()
	{
		instance = null;		
	}
	
}
