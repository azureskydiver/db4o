/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */

package com.db4o.omplus.ui.dialog.login.model;

import java.io.*;

public interface CustomConfigSink {
	void customConfig(File[] jarFiles, String[] configClassNames);
}
