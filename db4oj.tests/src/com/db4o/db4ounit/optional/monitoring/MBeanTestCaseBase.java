/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.optional.monitoring;

import java.util.*;

import javax.management.*;

import com.db4o.config.*;
import com.db4o.internal.config.Db4oLegacyConfigurationBridge;
import com.db4o.monitoring.Db4oMBeans;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

@decaf.Remove
public abstract class MBeanTestCaseBase extends AbstractDb4oTestCase {

	public static class Item {
		
		public Item(String id) {
			_id = id;
		}

		public String _id;
	}

	protected final transient ClockMock _clock = new ClockMock();
	protected transient MBeanProxy _bean;
	
	@Override
	protected void configure(Configuration legacy) throws Exception {
		CommonConfiguration config = Db4oLegacyConfigurationBridge.asCommonConfiguration(legacy);
		config.environment().add(_clock);
	}

	protected void exercisePerSecondCounter(final String beanAttributeName, final Runnable counterIncrementTrigger) {
		Assert.areEqual(0.0, bean().getAttribute(beanAttributeName));
		
		for (int i=0; i<3; ++i) {
			counterIncrementTrigger.run();
			counterIncrementTrigger.run();
			_clock.advance(1000);
			Assert.areEqual(2.0, bean().getAttribute(beanAttributeName));
		}
	}

	protected List<Notification> startCapturingNotifications(final String notificationType) throws JMException {
		final List<Notification> notifications = new ArrayList<Notification>();
		
		bean().addNotificationListener(new NotificationListener() {
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
	protected abstract String beanUri();

	protected MBeanProxy bean() {
		if (_bean == null) {
			_bean = new MBeanProxy(Db4oMBeans.mBeanNameFor(beanInterface(), beanUri()));
		}
		return _bean;
	}	
}
