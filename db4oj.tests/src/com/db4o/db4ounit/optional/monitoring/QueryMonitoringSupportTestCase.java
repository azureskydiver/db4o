/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.optional.monitoring;

import java.util.*;

import javax.management.*;

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.internal.config.*;
import com.db4o.monitoring.*;
import com.db4o.query.*;
import com.db4o.query.Query;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Remove
public class QueryMonitoringSupportTestCase extends AbstractDb4oTestCase {
	
	private final ClockMock _clock = new ClockMock();
	private MBeanProxy _queriesBean;
	
	@Override
	protected void configure(Configuration legacy) throws Exception {
		
		CommonConfiguration config = Db4oLegacyConfigurationBridge.asCommonConfiguration(legacy);
		config.add(new QueryMonitoringSupport());
		config.environment().add(_clock);
		
	}
	
	@Override
	protected void db4oSetupAfterStore() throws Exception {
		_queriesBean = new MBeanProxy(Db4oMBeans.mBeanNameFor(QueriesMBean.class, db().toString()));
	}
	
	public void testClassIndexScan() throws Exception {
		
		Assert.areEqual(0.0, _queriesBean.getAttribute("ClassIndexScansPerSecond"));
		
		for (int i=0; i<3; ++i) {
			triggerClassIndexScan();
			triggerClassIndexScan();
			_clock.advance(1000);
			Assert.areEqual(2.0, _queriesBean.getAttribute("ClassIndexScansPerSecond"));
		}
		
	}
	
	public void testUnoptimizedQueries() {
		
		Assert.areEqual(0.0, _queriesBean.getAttribute("UnoptimizedQueriesPerSecond"));
		
		for (int i=0; i<3; ++i) {
			triggerUnoptimizedQuery();
			triggerUnoptimizedQuery();
			_clock.advance(1000);
			Assert.areEqual(2.0, _queriesBean.getAttribute("UnoptimizedQueriesPerSecond"));
		}
		
	}
	
	public void testUnoptimizedQueryNotification() throws Exception {
		
		final List<Notification> notifications = startCapturingNotifications(unoptimizedQueryNotificationType());
		
		triggerUnoptimizedQuery();
		
		Assert.areEqual(1, notifications.size());
		
		final Notification notification = notifications.get(0);
		Assert.areEqual(unoptimizedQueryNotificationType(), notification.getType());
		Assert.areEqual(unoptimizableQuery().getClass().getName(), notification.getUserData());
		
	}
	
	public void testClassIndexScanNotifications() throws Exception {
		
		final List<Notification> notifications = startCapturingNotifications(classIndexScanNotificationType());
		
		triggerClassIndexScan();
		
		Assert.areEqual(1, notifications.size());
		
		final Notification notification = notifications.get(0);
		Assert.areEqual(classIndexScanNotificationType(), notification.getType());
		Assert.areEqual(Item.class.getName(), notification.getUserData());
	}

	private List<Notification> startCapturingNotifications(
			final String notificationType) throws JMException {
		final List<Notification> notifications = new ArrayList<Notification>();
		
		_queriesBean.addNotificationListener(new NotificationListener() {
			public void handleNotification(Notification notification, Object handback) {
				notifications.add(notification);
			}
		}, new NotificationFilter() {
			
			public boolean isNotificationEnabled(Notification notification) {
				return notificationType.equals(notification.getType());
			}
		});
		
		return notifications;
	}
	
	private String classIndexScanNotificationType() {
		return LoadedFromClassIndex.class.getName();
	}


	private String unoptimizedQueryNotificationType() {
		return NativeQueryNotOptimized.class.getName();
	}

	private void triggerUnoptimizedQuery() {
		db().query(unoptimizableQuery());
	}

	private Predicate<Item> unoptimizableQuery() {
		return new Predicate<Item>() {
			@Override
			public boolean match(Item candidate) {
				return candidate._id.toLowerCase().equals("FOO");
			}
		};
	}

	private void triggerClassIndexScan() {
		final Query query = newQuery(Item.class);
		query.descend("_id").constrain("foo");
		query.execute().toArray();
	}
	
	public static class Item {
		
		public Item(String id) {
			_id = id;
		}

		private String _id;
	}

}
