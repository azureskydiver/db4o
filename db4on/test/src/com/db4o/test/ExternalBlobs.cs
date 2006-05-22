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

		internal void Configure()
		{
			try
			{
				Db4o.Configure().SetBlobPath(AllTestsConfSingle.BLOB_PATH);
			}
			catch (Exception e)
			{
				JavaSystem.PrintStackTrace(e);
			}
		}

		internal void StoreOne()
		{
		}

		public void TestOne()
		{
			if (new File(AllTestsConfSingle.BLOB_PATH).Exists())
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
						new File(BLOB_FILE_IN).Delete();
						new File(BLOB_FILE_OUT).Delete();

						RandomAccessFile fw1 = new RandomAccessFile(BLOB_FILE_IN, "rw");
						fw1.Write(chout1);
						fw1.Close();
						blob.ReadFrom(new File(BLOB_FILE_IN));
						double status1 = blob.GetStatus();
						while (status1 > Status.COMPLETED)
						{
							Thread.Sleep(50);
							status1 = blob.GetStatus();
						}
						blob.WriteTo(new File(BLOB_FILE_OUT));
						status1 = blob.GetStatus();
						while (status1 > Status.COMPLETED)
						{
							Thread.Sleep(50);
							status1 = blob.GetStatus();
						}
						File resultingFile1 = new File(BLOB_FILE_OUT);
						Tester.Ensure(resultingFile1.Exists());
						if (resultingFile1.Exists())
						{
							RandomAccessFile fr1 = new RandomAccessFile(BLOB_FILE_OUT, "rw");
							byte[] chin1 = new byte[chout1.Length];
							fr1.Read(chin1);
							for (int i1 = 0; i1 < chin1.Length; i1++)
							{
								Tester.Ensure(chout1[i1] == chin1[i1]);
							}
							fr1.Close();
						}
					}
				}
				catch (Exception e)
				{
					{
						Tester.Ensure(false);
						JavaSystem.PrintStackTrace(e);
					}
				}
			}
		}
	}
}