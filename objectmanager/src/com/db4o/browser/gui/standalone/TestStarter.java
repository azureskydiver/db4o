package com.db4o.browser.gui.standalone;

import java.util.ArrayList;
import java.util.List;

import com.db4o.Db4o;
import com.db4o.reflect.jdk.JdkReflector;
import com.db4o.test.util.ExcludingClassLoader;

public class TestStarter {
	public static void main(String[] args) {
		List excluded=new ArrayList();
		for (int argidx = 0; argidx < args.length; argidx++) {
			excluded.add(args[argidx]);
		}
		ClassLoader excLoader=new ExcludingClassLoader(TestStarter.class.getClassLoader(),excluded);
		Db4o.configure().reflectWith(new JdkReflector(excLoader));
		StandaloneBrowser.main(new String[]{});
	}
}
