package com.db4o.omplus.datalayer.webservices;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;

import com.db4o.omplus.ws.ArrayOfFeaturePermission;
import com.db4o.omplus.ws.ArrayOfString;
import com.db4o.omplus.ws.FeaturePermission;
import com.db4o.omplus.ws.ReserveSeat;
import com.db4o.omplus.ws.SeatAuthorization;


/**
 * A class which deals with establishing the webservice connection
 * 
 * @author prameela_nair
 *
 */
public class WebServiceConnector 
{
	private static final String ERROR_MESSAGE = "The username and/or password cannot be " +
			"validated, or your"+" account is locked out or has not been approved yet.";
	
	private static final String CONN_TIMEDOUT_MESSAGE = "Connection timed out";
	
	private static final String DISCONNECT_MESSAGE = "Could not connect to the server for user validation.";
	
	private static final String LOGOUT_DISCONNECT_MESSAGE = "Could not connect to the server for user logout.";
//	private static final String PRODUCT_VERSION = "1.0";
//	private static final String PRODUCT_NAME = "OME Java";
	
	@SuppressWarnings("unused")
	public static boolean connectToWebService(String featureName, String username, String password,
				String proxy, int port) throws Exception
	{
		com.db4o.omplus.ws.AccountManagementServiceStub stub;
		try
		{			
			stub = new com.db4o.omplus.ws.AccountManagementServiceStub();
			
			ServiceClient sc = stub._getServiceClient();
			Options options = sc.getOptions();
			
//			Set proxy details
			HttpTransportProperties.ProxyProperties proxyProperties =
				 new HttpTransportProperties.ProxyProperties();
			if(proxy != null && proxy.length() != 0 && port < 65536){
				proxyProperties.setProxyName(proxy);
				proxyProperties.setProxyPort(port);
				options.setProperty(org.apache.axis2.transport.http.HTTPConstants.PROXY,
						proxyProperties);
			}
//			Set reuse http session property
			options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT,true);
			
			// get sessionId
			String sessionId = stub.Login(username, password);
			if( sessionId == null) {
				throw new Exception(ERROR_MESSAGE);
			}
			WebServiceManager mgr = WebServiceManager.getInstance();
			mgr.setSessionId(sessionId);
			sc.setOptions(options);
			SeatAuthorization sa = stub.ReserveSeat(new ReserveSeat());
			ArrayOfFeaturePermission features = sa.getFeatureAuthorization();
	 	    FeaturePermission [] permissions = features.getFeature();
	 	    if(permissions != null){
	 	      	mgr.setFeaturePermissions(permissions);
	 	    }
	 	    if(featureName != null){        
//	 	      TODO: should be in PermissionValidator
	 	    	String []allow = null;
	 	    	for( FeaturePermission fp : permissions)
	 	        {
	 	        	if(fp.getName().equals(featureName))
	 	        	{
	 	        		ArrayOfString strArray = permissions[0].getAllow();
	 	        		if(strArray !=  null)
	 	        		{
	 	        			allow = strArray.getAdd();
	 	        			if(allow.length > 0 && allow[0].equals("Full"))
	 	        				return true;
	 	 	 	        }
	 	        	}
	 	        }
	        }
	        else
	        	return true;
		} catch (AxisFault ex) {
			String str = ex.getMessage();
			if( str.contains(UnknownHostException.class.getName())){
				throw new Exception("No internet connection available.");

			}else if(str.contains(CONN_TIMEDOUT_MESSAGE)){
				throw new Exception(CONN_TIMEDOUT_MESSAGE);

			} else {
				throw new Exception(DISCONNECT_MESSAGE);
			}
		} 
		catch (RemoteException e) {

		}
		return false;
	}

	public static boolean logout(String proxy, int port) throws Exception{
		com.db4o.omplus.ws.AccountManagementServiceStub stub;
		try
		{			
			stub = new com.db4o.omplus.ws.AccountManagementServiceStub();
			
			String sessionId = WebServiceManager.getInstance().getSessionId();
			if(sessionId != null){ // duplication here
				ServiceClient sc = stub._getServiceClient();
				Options options = sc.getOptions();
				
				HttpTransportProperties.ProxyProperties proxyProperties =
					 new HttpTransportProperties.ProxyProperties();
				if(proxy != null && proxy.length() != 0 && port < 65536){
					proxyProperties.setProxyName(proxy);
					proxyProperties.setProxyPort(port);
					options.setProperty(org.apache.axis2.transport.http.HTTPConstants.PROXY,
							proxyProperties);
					
					stub.Logout(sessionId);
				}
			}
				
		} catch (AxisFault ex) {
			String str = ex.getMessage();
			if( str.contains(UnknownHostException.class.getName())){
				throw new Exception("No internet connection available.");

			}else if(str.contains(CONN_TIMEDOUT_MESSAGE)){
				throw new Exception(CONN_TIMEDOUT_MESSAGE);

			} else {
				throw new Exception(LOGOUT_DISCONNECT_MESSAGE);
			}
		} 
		catch (RemoteException e) {
			return false;
		}
		return true;
	}
	
}
