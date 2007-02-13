package com.db4o.objectManager.v2.demo;

import demo.objectmanager.model.DemoPopulator;

import java.awt.Cursor;

import com.db4o.objectManager.v2.util.Log;
import com.db4o.objectManager.v2.Dashboard;

/**
 * User: treeder
 * Date: Dec 20, 2006
 * Time: 7:02:36 PM
 */
public class DemoDbTask {

	public DemoDbTask(Dashboard dashboard) {
		DemoPopulator demoPopulator = new DemoPopulator();
		demoPopulator.start();
	}
}
