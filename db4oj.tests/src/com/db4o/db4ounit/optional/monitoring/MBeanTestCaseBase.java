/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.optional.monitoring;

import java.util.*;

import javax.management.*;

import com.db4o.config.*;
import com.db4o.internal.config.*;
import com.db4o.monitoring.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Remove
public abstract class MBeanTestCaseBase extends AbstractDb4oTestCase {
	
	public static final class OptimizableQuery extends Predicate<Item> {
		@Override public boolean match(Item candidate) {
			return candidate._id.equals("foo");
		}
	}

	public static final class UnoptimizableQuery extends Predicate<Item> {
		@Override
		public boolean match(Item candidate) {
			return candidate._id.toLowerCase().equals("FOO");
		}
	}

	public static class Item {
		
		public Item(String id) {
			_id = id;
		}

		private String _id;
	}


	protected final transient ClockMock _clock = new ClockMock();
	protected transient MBeanProxy _bean;
	
	@Override
	protected void configure(Configuration legacy) throws Exception {
		CommonConfiguration config = Db4oLegacyConfigurationBridge.asCommonConfiguration(legacy);
		config.environment().add(_clock);
	}

	protected void exercisePerSecondCounter(final String beanAttributeName, final Runnable counterIncrementTrigger) {
		Assert.areEqual(0.0, _bean.getAttribute(beanAttributeName));
		
		for (int i=0; i<3; ++i) {
			counterIncrementTrigger.run();
			counterIncrementTrigger.run();
			_clock.advance(1000);
			Assert.areEqual(2.0, _bean.getAttribute(beanAttributeName));
		}
	}

	protected List<Notification> startCapturingNotifications(final String notificationType)
			throws JMException {
		final List<Notification> notifications = new ArrayList<Notification>();
		
		_bean.addNotificationListener(new NotificationListener() {
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

	protected abstract Class<?> beanInterface();

	@Override
	protected void db4oSetupAfterStore() throws Exception {
		_bean = new MBeanProxy(Db4oMBeans.mBeanNameFor(beanInterface(), beanUri()));
	}

	protected abstract String beanUri();

	protected void triggerOptimizedQuery() {
		db().query(new OptimizableQuery()).toArray();
	}

	protected void triggerUnoptimizedQuery() {
		db().query(unoptimizableQuery()).toArray();
	}

	protected Predicate<Item> unoptimizableQuery() {
		return new UnoptimizableQuery();
	}

}
