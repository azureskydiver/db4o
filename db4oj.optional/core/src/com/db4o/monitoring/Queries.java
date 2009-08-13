/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import javax.management.*;

import com.db4o.diagnostic.*;
import com.db4o.monitoring.internal.*;
import com.db4o.query.*;

@decaf.Ignore
class Queries extends MBeanRegistrationSupport implements QueriesMBean, NotificationEmitter {

	private final TimedReading _classIndexScans = TimedReading.newPerSecond();
	private final TimedReading _unoptimizedQueries = TimedReading.newPerSecond();
	private final NotificationBroadcasterSupport _notificationSupport = new NotificationBroadcasterSupport();

	public Queries(ObjectName objectName) throws JMException {
		super(objectName);
	}

	private static String classIndexScanNotificationType() {
		return LoadedFromClassIndex.class.getName();
	}

	private void sendNotification(final String notificationType,
			final String message, final Object userData) {
		final Notification notification = new Notification(notificationType, objectName(), 0, message);
		notification.setUserData(userData);
		_notificationSupport.sendNotification(notification);
	}

	public double getClassIndexScansPerSecond() {
		return _classIndexScans.read();
	}
	
	public double getUnoptimizedQueriesPerSecond() {
		return _unoptimizedQueries.read();
	}

	public void removeNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws ListenerNotFoundException {
		_notificationSupport.removeNotificationListener(listener, filter, handback);
	}

	public void addNotificationListener(NotificationListener listener,
			NotificationFilter filter, Object handback)
			throws IllegalArgumentException {
		_notificationSupport.addNotificationListener(listener, filter, handback);
	}

	public MBeanNotificationInfo[] getNotificationInfo() {
		return new MBeanNotificationInfo[] {
			new MBeanNotificationInfo(
					new String[] { classIndexScanNotificationType() },
					Notification.class.getName(),
					"Notification about class index scans."),
					
			new MBeanNotificationInfo(
					new String[] { unoptimizedQueryNotificationType() },
					Notification.class.getName(),
					"Notification about unoptimized native query execution."),
			
		};
	}

	private String unoptimizedQueryNotificationType() {
		return NativeQueryNotOptimized.class.getName();
	}
	
	public void removeNotificationListener(NotificationListener listener)
			throws ListenerNotFoundException {
		_notificationSupport.removeNotificationListener(listener);
	}

	public void notifyUnoptimized(Predicate predicate) {
		
		_unoptimizedQueries.increment();
		sendNotification(unoptimizedQueryNotificationType(), "Unoptimized native query.", predicate.getClass().getName());
		
	}
	
	public void notifyClassIndexScan(LoadedFromClassIndex d) {
		
		_classIndexScans.increment();
		
		sendNotification(classIndexScanNotificationType(), d.problem(), d.reason());
	}
}
