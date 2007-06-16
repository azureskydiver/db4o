/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.blobs;

import java.io.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.test.lib.*;
import com.db4o.types.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ExternalBlobsTestCase extends AbstractDb4oTestCase implements OptOutDefragSolo {

	public static void main(String[] args) {
		new ExternalBlobsTestCase().runSolo();
	}
	
	private static final String BLOB_PATH = "test/TEMP/db4oTestBlobs";
	private static final String BLOB_FILE_IN = BLOB_PATH + "/regressionBlobIn.txt"; 
	private static final String BLOB_FILE_OUT = BLOB_PATH + "/regressionBlobOut.txt"; 

	private static class Data {
		private Blob _blob;

		public Data() {
			_blob = null;
		}
		
		public Blob blob() {
			return _blob;
		}
	}

	protected void db4oSetupBeforeStore() throws Exception {
		deleteFiles();
	}
	
	protected void db4oCustomTearDown() throws Exception {
		deleteFiles();
	}
	
	protected void configure(Configuration config) throws IOException {
		config.setBlobPath(BLOB_PATH);
	}

	protected void store() throws Exception {
		store(new Data());
	}
	
	public void test() throws Exception {
		Data data = (Data) retrieveOnlyInstance(Data.class);
		Assert.isTrue(new File(BLOB_PATH).exists());
		char[] chout = new char[] { 'H', 'i', ' ', 'f', 'o', 'l', 'k', 's' };
		FileWriter fw = new FileWriter(BLOB_FILE_IN);
		fw.write(chout);
		fw.flush();
		fw.close();
		data.blob().readFrom(new File(BLOB_FILE_IN));
		double status = data.blob().getStatus();
		while (status > Status.COMPLETED) {
			Thread.sleep(50);
			status = data.blob().getStatus();
		}

		data.blob().writeTo(new File(BLOB_FILE_OUT));
		status = data.blob().getStatus();
		while (status > Status.COMPLETED) {
			Thread.sleep(50);
			status = data.blob().getStatus();
		}
		File resultingFile = new File(BLOB_FILE_OUT);
		Assert.isTrue(resultingFile.exists());

		FileReader fr = new FileReader(resultingFile);
		char[] chin = new char[chout.length];
		fr.read(chin);
		fr.close();
		ArrayAssert.areEqual(chout, chin);
	}

	private void deleteFiles() throws IOException {
		IOUtil.deleteDir(BLOB_PATH);
	}
}
