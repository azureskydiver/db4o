/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import javax.management.*;

import com.db4o.monitoring.*;
import com.db4o.monitoring.internal.*;

@decaf.Ignore
public class Networking extends MBeanRegistrationSupport implements NetworkingMBean {

	public Networking(ObjectName objectName) throws JMException {
		super(objectName);
	}

	public double getBytesSentPerSecond() {
		return bytesSent().read();
	}

	public double getBytesReceivedPerSecond() {
		return bytesReceived().read();
	}
	
	public double getMessagesSentPerSecond() {
		return messagesSent().read();
	}
	
	public void notifyWrite(int count) {
		bytesSent().incrementBy(count);
		messagesSent().incrementBy(1);
	}
	
	public void notifyRead(int count) {
		bytesReceived().incrementBy(count);
	}	
	
	private TimedReading messagesSent() {
		if (null == _messagesSent){
			_messagesSent = TimedReading.newPerSecond();
		}
		
		return _messagesSent;
	}
	
	private TimedReading bytesReceived() {
		if (null == _bytesReceived) {
			_bytesReceived = TimedReading.newPerSecond();
		}
		
		return _bytesReceived;
	}

	private TimedReading bytesSent() {
		if (null == _bytesSent) {
			_bytesSent = TimedReading.newPerSecond();
		}
		
		return _bytesSent;
	}
	
	@Override
	public String toString() {
		return _objectName.toString();
	}
	
	public void resetCounters() {
		reset(_bytesSent);
		reset(_bytesReceived);
		reset(_messagesSent);
	}

	private void reset(TimedReading counter) {
		if (null != counter) {
			counter.resetCount();
		}
	}
	
	private TimedReading _bytesSent;
	private TimedReading _bytesReceived;
	private TimedReading _messagesSent;
}
