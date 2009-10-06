package com.db4o.monitoring.cs;

import javax.management.*;

/**
 * @exclude
 */
@decaf.Ignore
public class SynchronizedNetworking extends Networking {

	public SynchronizedNetworking(ObjectName objectName) throws JMException {
		super(objectName);
	}
	
	@Override
	public synchronized double getBytesReceivedPerSecond() {
		return super.getBytesReceivedPerSecond();
	}
	
	@Override
	public synchronized double getBytesSentPerSecond() {
		return super.getBytesSentPerSecond();
	}
	
	@Override
	public synchronized double getMessagesSentPerSecond() {
		return super.getMessagesSentPerSecond();
	}

	@Override
	public synchronized void notifyRead(int count) {
		super.notifyRead(count);
	}
	
	@Override
	public synchronized void notifyWrite(int count) {
		super.notifyWrite(count);
	}
	
	@Override
	public synchronized void resetCounters() {
		super.resetCounters();
	}
}
