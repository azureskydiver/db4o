package com.db4o.omplus.ws;

import java.rmi.RemoteException;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;


public class WSClienTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		System.setProperty("http.proxyHost", "hjproxy.persistent.co.in");
//		System.setProperty("http.proxyPort", "8080");
		
		com.db4o.omplus.ws.AccountManagementServiceStub stub;
		
		try {
			stub = new com.db4o.omplus.ws.AccountManagementServiceStub();
			HttpTransportProperties.ProxyProperties proxyProperties =
				 new HttpTransportProperties.ProxyProperties();
			
			proxyProperties.setProxyName("hjproxy.persistent.co.in");
			proxyProperties.setProxyPort(8080);
			ServiceClient sc = stub._getServiceClient();
			Options options = sc.getOptions();
			options.setProperty(org.apache.axis2.transport.http.HTTPConstants.PROXY,
									proxyProperties);
//			options.setManageSession(true);
			options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT,true);
			
			String sessionId = stub.Login("gdheer", "db4objects");
			System.out.println(" Session Id is "+sessionId);
			
//			options.setProperty(Constants.CUSTOM_COOKIE_ID,"ASP.NET_SessionId");
			sc.setOptions(options);
				        
	        GetUserInfo ui = new GetUserInfo();
	        UserInfo info = stub.GetUserInfo(ui);
	        System.out.println(" User Info "+info.getEmail()+" "+info.getSalesforceID());
	        
	        SeatAuthorization sa = stub.ReserveSeat(new ReserveSeat());
	        ArrayOfFeaturePermission features = sa.getFeatureAuthorization();
	        FeaturePermission [] permission = features.getFeature();
	        System.out.println(permission[0].getName());
			
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//the default implementation should point to the right endpoint
		catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

       
      

	}

}
