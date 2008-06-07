package com.db4o.omplus.datalayer.webservices;

import com.db4o.omplus.ws.FeaturePermission;

/**
 * The manager class for establising the web
 * @author prameela_nair
 *
 */
public class WebServiceManager 
{
	
	
	private static WebServiceManager instance;
	
	private String sessionId;
	
	private FeaturePermission[] featurePermissions;
	
	private WebServiceManager()
	{
		
	}	
	public static WebServiceManager getInstance()
	{
		if (instance == null)
		{	
			instance =  new WebServiceManager();
		}
		return instance;
	}
	
	
	public FeaturePermission[] getFeaturePermissions() {
		return featurePermissions;
	}

	public void setFeaturePermissions(FeaturePermission[] featurePermissions) 
	{
		this.featurePermissions = featurePermissions;
	}
	
	public static void resetInstance()
	{
		instance = null;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String id) {
		sessionId = id;
	}

}
