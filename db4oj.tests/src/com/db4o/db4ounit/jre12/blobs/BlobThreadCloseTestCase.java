package com.db4o.db4ounit.jre12.blobs;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.io.*;
import com.db4o.types.*;

import db4ounit.extensions.*;
import db4ounit.extensions.util.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class BlobThreadCloseTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new BlobThreadCloseTestCase().runClientServer();
	}

	private static final String TEST_FILE = "test.db4o";
	
	private static class Data {
		private Blob _blob;

		public Data() {
			_blob = null;
		}

		public Blob blob() {
			return _blob;
		}
	}

	protected void db4oTearDownAfterClean() throws Exception {
		File4.delete(TEST_FILE);
		IOUtil.deleteDir("blobs");
	}

	/**
	 * @deprecated using deprecated api
	 */
	public void test() throws Exception {
		if (isEmbeddedClientServer()) {
			return;
		}
		((ExtClient) db()).switchToFile(TEST_FILE);
		store(new Data());
//		((ExtClient) db()).switchToFile("test.yap");

		Data data = (Data) retrieveOnlyInstance(Data.class);
		data.blob().readFrom(
				new File(BlobThreadCloseTestCase.class.getResource(
						"BlobThreadCloseTestCase.class").getFile()));
		while (data.blob().getStatus() > Status.COMPLETED) {
			Thread.sleep(50);
		}
	}
}