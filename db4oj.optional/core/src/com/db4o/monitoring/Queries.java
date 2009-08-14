/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import javax.management.*;

import com.db4o.diagnostic.*;
import com.db4o.internal.query.*;
import com.db4o.monitoring.internal.*;
import com.db4o.query.*;

@decaf.Ignore
class Queries extends MBeanRegistrationSupport implements QueriesMBean, NotificationEmitter {

	private final TimedReading _classIndexScans = TimedReading.newPerSecond();
	private final TimedReading _unoptimizedNativeQueries = TimedReading.newPerSecond();
	private final TimedReading _nativeQueries = TimedReading.newPerSecond();
	private final TimedReading _queries = TimedReading.newPerSecond();
	private final AveragingTimedReading _queryExecutionTime = new AveragingTimedReading();
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

	public double getAverageQueryExecutionTime() {
		return _queryExecutionTime.read();
	}

	public double getQueriesPerSecond() {
		return _queries.read();
	}
	
	public double getUnoptimizedNativeQueriesPerSecond() {
		return _unoptimizedNativeQueries.read();
	}
	
	public double getNativeQueriesPerSecond() {
		return _nativeQueries.read();
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

	public void notifyClassIndexScan(LoadedFromClassIndex d) {
		
		_classIndexScans.increment();
		
		sendNotification(classIndexScanNotificationType(), d.problem(), d.reason());
	}

	public void notifyNativeQuery(NQOptimizationInfo info) {
		
		if (info.message().equals(NativeQueryHandler.UNOPTIMIZED)) {
			notifyUnoptimized(info.predicate());
		}
		
		_nativeQueries.increment();
	}
	
	private void notifyUnoptimized(Predicate predicate) {
		
		_unoptimizedNativeQueries.increment();
		sendNotification(unoptimizedQueryNotificationType(), "Unoptimized native query.", predicate.getClass().getName());
		
	}

	public void notifyQueryStarted() {
		_queries.increment();
		
		_queryExecutionTime.eventStarted();
	}	
	
	public void notifyQueryFinished() {
		
		_queryExecutionTime.eventFinished();
	}
}
