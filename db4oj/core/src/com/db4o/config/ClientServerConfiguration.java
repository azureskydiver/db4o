/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.config;

import com.db4o.messaging.MessageRecipient;
import com.db4o.messaging.MessageSender;

/**
 * Client/Server configuration interface. 
 */
public interface ClientServerConfiguration {
	/**
	 * Sets the number of IDs to be pre-allocated in the database for new 
	 * objects created on the client.
	 * This setting should be used on the client side. In embedded mode this setting
	 * has no effect.
	 * @param prefetchIDCount
	 *            The number of IDs to be prefetched
	 */
	void prefetchIDCount(int prefetchIDCount);

	/**
	 * Sets the number of objects to be prefetched for an ObjectSet in C/S mode.
	 * This setting should be used on the server side. In embedded mode this setting
	 * has no effect.
	 * @param prefetchObjectCount
	 *            The number of objects to be prefetched
	 */
	void prefetchObjectCount(int prefetchObjectCount);

	/**
	 * sets the MessageRecipient to receive Client Server messages. <br>
	 * <br>
	 * This setting should be used on the server side.<br><br>
	 * @param messageRecipient
	 *            the MessageRecipient to be used
	 */
	public void setMessageRecipient(MessageRecipient messageRecipient);

	/**
	 * returns the MessageSender for this Configuration context.
	 * This setting should be used on the client side.
	 * @return MessageSender
	 */
	public MessageSender getMessageSender();

	/**
	 * configures the time a client waits for a message response from the
	 * server. <br>
	 * <br>
	 * Default value: 300000ms (5 minutes)<br>
	 * <br>
	 * This setting can be used on both client and server.<br><br> 
	 * @param milliseconds
	 *            time in milliseconds
	 */
	public void timeoutClientSocket(int milliseconds);

	/**
	 * configures the timeout of the serverside socket. <br>
	 * <br>
	 * All server connection threads jump out of the socket read statement on a
	 * regular interval to check if the server was shut down. Use this method to
	 * configure the duration of the interval.<br>
	 * <br>
	 * Default value: 5000ms (5 seconds)<br>
	 * <br>
	 * This setting can be used on both client and server.<br><br>
	 * @param milliseconds
	 *            time in milliseconds
	 */
	public void timeoutServerSocket(int milliseconds);

	/**
	 * configures the delay time after which the server starts pinging connected
	 * clients to check the connection. <br>
	 * <br>
	 * If no client messages are received by the server for the configured
	 * interval, the server sends a "PING" message to the client and wait's for
	 * an "PONG" response. <br>
	 * <br>
	 * This value may need to be increased for single-threaded clients, since
	 * they can't respond instantaneously. <br>
	 * <br>
	 * Default value: 180000ms (3 minutes)<br>
	 * <br>
	 * This setting can be used on both client and server.<br><br> 
	 * @param milliseconds
	 *            time in milliseconds
	 * @see #singleThreadedClient
	 */
	public void pingInterval(int milliseconds);
	
	/**
     * configures the client messaging system to be single threaded 
     * or multithreaded.
     * <br><br>Recommended settings:<br>
     * - <code>true</code> for low resource systems.<br>
     * - <code>false</code> for best asynchronous performance and fast
     * GUI response.
     * <br><br>Default value:<br>
     * - .NET Compactframework: <code>true</code><br>
     * - all other platforms: <code>false</code><br><br>
     * This setting can be used on both client and server.<br><br>
     * @param flag the desired setting
     */
    public void singleThreadedClient(boolean flag);


	/**
	 * Configures to batch messages between client and server. By default, batch
	 * mode is enabled.<br><br>
	 * This setting can be used on both client and server.<br><br>
	 * @param flag
	 *            false, to turn message batching off.
	 */
	public void batchMessages(boolean flag);
	
	/**
	 * Configures the maximum memory buffer size for batched message. If the
	 * size of batched messages is greater than <code>maxSize</code>, batched
	 * messages will be sent to server.<br><br>
	 * This setting can be used on both client and server.<br><br>
	 * @param maxSize
	 */
	public void maxBatchQueueSize(int maxSize);

}
