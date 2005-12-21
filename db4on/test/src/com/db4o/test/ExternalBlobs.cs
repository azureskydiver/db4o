/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.ext;
using com.db4o.types;
using j4o.io;
using j4o.lang;

namespace com.db4o.test
{
	public class ExternalBlobs
	{
		public ExternalBlobs() : base()
		{
		}

		internal static String BLOB_FILE_IN = "regressionBlobIn.txt";
		internal static String BLOB_FILE_OUT = "regressionBlobOut.txt";
		internal Blob blob;

		internal void configure()
		{
			try
			{
				Db4o.configure().setBlobPath(AllTestsConfSingle.BLOB_PATH);
			}
			catch (Exception e)
			{
				JavaSystem.printStackTrace(e);
			}
		}

		internal void storeOne()
		{
		}

		public void testOne()
		{
			if (new File(AllTestsConfSingle.BLOB_PATH).exists())
			{
				try
				{
					{
						byte[] chout1 = new byte[]
							{
								72,
								105,
								32,
								102,
								111,
								108,
								107,
								115
							};
						new File(BLOB_FILE_IN).delete();
						new File(BLOB_FILE_OUT).delete();

						RandomAccessFile fw1 = new RandomAccessFile(BLOB_FILE_IN, "rw");
						fw1.write(chout1);
						fw1.close();
						blob.readFrom(new File(BLOB_FILE_IN));
						double status1 = blob.getStatus();
						while (status1 > Status.COMPLETED)
						{
							Thread.sleep(50);
							status1 = blob.getStatus();
						}
						blob.writeTo(new File(BLOB_FILE_OUT));
						status1 = blob.getStatus();
						while (status1 > Status.COMPLETED)
						{
							Thread.sleep(50);
							status1 = blob.getStatus();
						}
						File resultingFile1 = new File(BLOB_FILE_OUT);
						Tester.ensure(resultingFile1.exists());
						if (resultingFile1.exists())
						{
							RandomAccessFile fr1 = new RandomAccessFile(BLOB_FILE_OUT, "rw");
							byte[] chin1 = new byte[chout1.Length];
							fr1.read(chin1);
							for (int i1 = 0; i1 < chin1.Length; i1++)
							{
								Tester.ensure(chout1[i1] == chin1[i1]);
							}
							fr1.close();
						}
					}
				}
				catch (Exception e)
				{
					{
						Tester.ensure(false);
						JavaSystem.printStackTrace(e);
					}
				}
			}
		}
	}
}