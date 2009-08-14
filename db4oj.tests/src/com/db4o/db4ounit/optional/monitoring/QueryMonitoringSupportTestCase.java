/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.optional.monitoring;

import java.util.*;

import javax.management.*;

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.foundation.*;
import com.db4o.internal.config.*;
import com.db4o.monitoring.*;
import com.db4o.query.*;
import com.db4o.query.Query;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

@decaf.Remove
public class QueryMonitoringSupportTestCase extends AbstractDb4oTestCase implements CustomClientServerConfiguration {
	
	private final ClockMock _clock = new ClockMock();
	private MBeanProxy _queriesBean;
	
	@Override
	protected void configure(Configuration legacy) throws Exception {
		
		CommonConfiguration config = Db4oLegacyConfigurationBridge.asCommonConfiguration(legacy);
		config.add(new QueryMonitoringSupport());
		config.environment().add(_clock);
		
	}

	public void configureClient(Configuration config) throws Exception {
	}

	public void configureServer(Configuration config) throws Exception {
		configure(config);
	}
	
	@Override
	protected void db4oSetupAfterStore() throws Exception {
		_queriesBean = new MBeanProxy(Db4oMBeans.mBeanNameFor(QueriesMBean.class, fileSession().toString()));
	}
	
	public void testClassIndexScan() throws Exception {
		
		exercisePerSecondCounter("ClassIndexScansPerSecond", new Runnable() { public void run() {
			triggerClassIndexScan();
		}});
		
	}
	
	public void testUnoptimizedNativeQueriesPerSecond() {
		
		exercisePerSecondCounter("UnoptimizedNativeQueriesPerSecond", new Runnable() { public void run() {
			triggerUnoptimizedQuery();
		}});
		
	}
	
	public void testAverageQueryExecutionTime() {
		
		Assert.areEqual(0.0, _queriesBean.getAttribute("AverageQueryExecutionTime"));
		
		triggerQueryExecutionTime(1000);
		Assert.areEqual(1000.0, _queriesBean.getAttribute("AverageQueryExecutionTime"));
		
		triggerQueryExecutionTime(200);
		triggerQueryExecutionTime(500);
		Assert.areEqual(350.0, _queriesBean.getAttribute("AverageQueryExecutionTime"));
		
	}

	private void triggerQueryExecutionTime(final int executionTime) {
		final Query query = newQuery(Item.class);
		stream().callbacks().queryOnStarted(trans(), query);
		_clock.advance(executionTime);
		stream().callbacks().queryOnFinished(trans(), query);
	}
	
	public void testNativeQueriesPerSecond() {
		
		final ByRef<Boolean> optimized = ByRef.newInstance(true);
		
		exercisePerSecondCounter("NativeQueriesPerSecond", new Runnable() { public void run() {
			if (optimized.value) {
				triggerOptimizedQuery();
			} else {
				triggerUnoptimizedQuery();
			}
			optimized.value = !optimized.value;
		}});
		
	}
	
	public void testQueriesPerSecond() {
		
		final ByRef<Integer> queryMode = ByRef.newInstance(0);
		
		exercisePerSecondCounter("QueriesPerSecond", new Runnable() { public void run() {
			switch (queryMode.value % 3) {
			case 0:
				triggerOptimizedQuery();
				break;
			case 1:
				triggerUnoptimizedQuery();
				break;
			case 2:
				triggerSodaQuery();
				break;
			}
			++queryMode.value;
		}});
	}

	protected void triggerOptimizedQuery() {
		db().query(new Predicate<Item>() { @Override public boolean match(Item candidate) {
			return candidate._id.equals("foo");
		}});
	}

	private void exercisePerSecondCounter(final String beanAttributeName,
			final Runnable counterIncrementTrigger) {
		Assert.areEqual(0.0, _queriesBean.getAttribute(beanAttributeName));
		
		for (int i=0; i<3; ++i) {
			counterIncrementTrigger.run();
			counterIncrementTrigger.run();
			_clock.advance(1000);
			Assert.areEqual(2.0, _queriesBean.getAttribute(beanAttributeName));
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
	
	private void triggerSodaQuery() {
		newQuery(Item.class).execute();
	}

	public static class Item {
		
		public Item(String id) {
			_id = id;
		}

		private String _id;
	}

}
