/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.config;

import com.db4o.messaging.MessageRecipient;
import com.db4o.messaging.MessageSender;

public interface ClientServerConfiguration {
	/**
	 * Sets the number of IDs to be prefetched for an ObjectSet in C/S mode
	 * 
	 * @param prefetchIDCount
	 *            The number of IDs to be prefetched
	 */
	void prefetchIDCount(int prefetchIDCount);

	/**
	 * Sets the number of objects to be prefetched for an ObjectSet in C/S mode
	 * 
	 * @param prefetchObjectCount
	 *            The number of objects to be prefetched
	 */
	void prefetchObjectCount(int prefetchObjectCount);

	/**
	 * sets the MessageRecipient to receive Client Server messages. <br>
	 * <br>
	 * 
	 * @param messageRecipient
	 *            the MessageRecipient to be used
	 */
	public void setMessageRecipient(MessageRecipient messageRecipient);

	/**
	 * returns the MessageSender for this Configuration context.
	 * 
	 * @return MessageSender
	 */
	public MessageSender getMessageSender();

	/**
	 * configures the time a client waits for a message response from the
	 * server. <br>
	 * <br>
	 * Default value: 300000ms (5 minutes)<br>
	 * <br>
	 * 
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
	 * 
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
	 * an "OK" response. After 5 unsuccessful attempts, the client connection is
	 * closed. <br>
	 * <br>
	 * This value may need to be increased for single-threaded clients, since
	 * they can't respond instantaneously. <br>
	 * <br>
	 * Default value: 180000ms (3 minutes)<br>
	 * <br>
	 * 
	 * @param milliseconds
	 *            time in milliseconds
	 * @see #singleThreadedClient
	 */
	public void timeoutPingClients(int milliseconds);

	/**
	 * Configures to batch messages between client and server. By default, batch
	 * mode is disabled.
	 * 
	 * @param flag
	 *            true for batching messages.
	 */
	public void batchMessages(boolean flag);
	
	/**
	 * Configures the maximum memory buffer size for batched message. If the
	 * size of batched messages is greater than <code>maxSize</code>, batched
	 * messages will be sent to server.
	 * 
	 * @param maxSize
	 */
	public void maxBatchQueueSize(int maxSize);

}
