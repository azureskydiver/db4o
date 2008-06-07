/**
 * AccountManagementService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.3  Built on : Aug 10, 2007 (04:45:47 LKT)
 */
package com.db4o.omplus.ws;


/*
 *  AccountManagementService java interface
 */
public interface AccountManagementService {
    /**
     * Auto generated method signature
     * @param login
     */
    public java.lang.String Login(java.lang.String username,
        java.lang.String password) throws java.rmi.RemoteException;

    /**
     * Auto generated method signature
     * @param getUserInfo
     */
    public com.db4o.omplus.ws.UserInfo GetUserInfo(
        com.db4o.omplus.ws.GetUserInfo getUserInfo) throws java.rmi.RemoteException;

    /**
     * Auto generated method signature
     * @param getLoginNotice
     */
    public com.db4o.omplus.ws.LoginNotice GetLoginNotice(
        com.db4o.omplus.ws.GetLoginNotice getLoginNotice)
        throws java.rmi.RemoteException;

    /**
     * Auto generated method signature
     * @param reserveSeat
     */
    public com.db4o.omplus.ws.SeatAuthorization ReserveSeat(
        com.db4o.omplus.ws.ReserveSeat reserveSeat) throws java.rmi.RemoteException;

    /**
     * Auto generated method signature
     * @param seatKeepAlive
     */
    public com.db4o.omplus.ws.SeatAuthorization SeatKeepAlive(
        java.lang.String sessionID) throws java.rmi.RemoteException;

    /**
     * Auto generated method signature
     * @param releaseSeat
     */
    public com.db4o.omplus.ws.ReleaseSeatResponse ReleaseSeat(
        java.lang.String sessionID2, java.lang.String machineName,
        java.lang.String machineUsername) throws java.rmi.RemoteException;

    /**
     * Auto generated method signature
     * @param logout
     */
    public com.db4o.omplus.ws.LogoutResponse Logout(java.lang.String sessionID3)
        throws java.rmi.RemoteException;

    //
}
