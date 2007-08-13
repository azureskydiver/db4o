package com.db4o.db4ounit.jre12.blobs;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.types.*;

import db4ounit.extensions.*;

public class BlobThreadCloseTestCase extends Db4oClientServerTestCase {
	public static void main(String[] args) {
		new BlobThreadCloseTestCase().runClientServer();
	}

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
		new File("test.yap").delete();
	}

	public void test() throws Exception {
		if (isEmbeddedClientServer()) {
			return;
		}
		((ExtClient) db()).switchToFile("test.yap");
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