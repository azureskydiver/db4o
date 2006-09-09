/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.performance;

import java.io.*;

import com.db4o.io.*;

/**
 * 
 * @exclude
 */
public class RunIOBench {
	public static void main(String[] args) throws IOException {

		RandomAccessFile recordedIn = new RandomAccessFile(Util.BENCHFILE+".1", "rw");
		new File(Util.DBFILE).delete();
		IoAdapter testadapt = new RandomAccessFileAdapter().open(Util.DBFILE,
				false, 1024);

		// IoAdapter testadapt = new MemoryIoAdapter().open(Util.DBFILE, false,
		// 1024);
		// IoAdapter testadapt = new SymbianIoAdapter().open(Util.DBFILE,
		// false, 1024);
		long bench = benchmark(recordedIn, testadapt);
		System.out.println("tested IOAdapter: ["
				+ testadapt.getClass().getName() + "]\nspeed: " + bench);
	}

	public static long benchmark(RandomAccessFile recordedIn, IoAdapter adapter)
			throws IOException {
		byte[] defaultData = new byte[1000];
		long start = System.currentTimeMillis();
		int runs = 0;
		try {
			while (true) {
				runs++;
				char type = recordedIn.readChar();
				if (type == 'q') {
					break;
				}
				if (type == 'f') {
					adapter.sync();
					continue;
				}
				long pos = recordedIn.readLong();
				int length = recordedIn.readInt();
				adapter.seek(pos);
				byte[] data = (length <= defaultData.length ? defaultData
						: new byte[length]);
				switch (type) {
				case 'r':
					adapter.read(data, length);
					break;
				case 'w':
					adapter.write(data, length);
					break;
				default:
					throw new IllegalArgumentException("Unknown access type: "
							+ type);
				}
			}
		} finally {
			recordedIn.close();
			adapter.close();
		}
		// System.err.println(runs);
		return System.currentTimeMillis() - start;
	}
}