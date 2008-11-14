/* Copyright (C) 2008  db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.config;

import com.db4o.messaging.MessageRecipient;



/**
 * @since 7.5
 */
public interface NetworkingConfiguration {
	
	/**
	 * configures the time a client waits for a message response 
	 * from the server. <br>
	 * <br>
	 * Default value: 600000ms (10 minutes)<br>
	 * <br>
     * It is recommended to use the same values for {@link #timeoutClientSocket(int)}
     * and {@link #timeoutServerSocket(int)}.
     * <br>
	 * This setting can be used on both client and server.<br><br> 
	 * @param milliseconds
	 *            time in milliseconds
	 *            
	 * @sharpen.property
	 */
	public void timeoutClientSocket(int milliseconds);

	/**
	 * configures the timeout of the serverside socket. <br>
	 * <br>
	 * The serverside handler waits for messages to arrive from the client.
	 * If no more messages arrive for the duration configured in this
	 * setting, the client will be disconnected.
	 * <br>  
	 * Clients send PING messages to the server at an interval of
	 * Math.min(timeoutClientSocket(), timeoutServerSocket()) / 2 
	 * and the server will respond to keep connections alive.
	 * <br> 
	 * Decrease this setting if you want clients to disconnect faster.
     * <br>
     * Increase this setting if you have a large number of clients and long
     * running queries and you are getting disconnected clients that you 
     * would like to wait even longer for a response from the server. 
     * <br>
	 * Default value: 600000ms (10 minutes)<br>
	 * <br>
	 * It is recommended to use the same values for {@link #timeoutClientSocket(int)}
	 * and {@link #timeoutServerSocket(int)}.
	 * <br>
	 * This setting can be used on both client and server.<br><br>
	 * @param milliseconds
	 *            time in milliseconds
	 *            
	 * @sharpen.property
	 */
	public void timeoutServerSocket(int milliseconds);
	
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
     * 
     * @sharpen.property
     */
    public void singleThreadedClient(boolean flag);

	/**
	 * Configures to batch messages between client and server. By default, batch
	 * mode is enabled.<br><br>
	 * This setting can be used on both client and server.<br><br>
	 * @param flag
	 *            false, to turn message batching off.
	 *            
	 * @sharpen.property
	 */
	public void batchMessages(boolean flag);
	
	/**
	 * Configures the maximum memory buffer size for batched message. If the
	 * size of batched messages is greater than <code>maxSize</code>, batched
	 * messages will be sent to server.<br><br>
	 * This setting can be used on both client and server.<br><br>
	 * @param maxSize
	 * 
	 * @sharpen.property
	 */
	public void maxBatchQueueSize(int maxSize);
	

	/**
	 * sets the MessageRecipient to receive Client Server messages. <br>
	 * <br>
	 * This setting can be used on both client and server.<br><br>
	 * @param messageRecipient
	 *            the MessageRecipient to be used
	 *            
	 * @sharpen.property
	 */
	void messageRecipient(MessageRecipient messageRecipient);
}
