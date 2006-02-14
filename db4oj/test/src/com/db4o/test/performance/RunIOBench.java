package com.db4o.test.performance;

import java.io.*;

import com.db4o.io.*;

/**
 * @exclude
 */
public class RunIOBench {
public static void main(String[] args) throws IOException {
	DataInputStream recordedIn=new DataInputStream(new FileInputStream(args[0]));
	new File(args[1]).delete();
	IoAdapter testadapt= new RandomAccessFileAdapter().open(args[1], false, 1024);
	long bench=benchmark(recordedIn,testadapt);
	System.out.println(bench);
 }

	public static long benchmark(DataInputStream recordedIn,IoAdapter adapter) throws IOException {
		byte[] defaultData=new byte[1000];
		long start = System.currentTimeMillis();
		int runs=0;
		try {
			while(true) {
					runs++;
					char type=recordedIn.readChar();
					if(type=='q') {
						break;
					}
					if(type=='f') {
						adapter.sync();
						continue;
					}
					long pos=recordedIn.readLong();
					int length=recordedIn.readInt();
					adapter.seek(pos);
					byte[] data=(length<=defaultData.length ? defaultData : new byte[length]);
					switch(type) {
						case 'r':
						    adapter.read(data,length);
						    break;
						case 'w':
						    adapter.write(data, length);
						    break;
						default:
							throw new IllegalArgumentException("Unknown access type: "+type);
					}
			}
		} 
		finally {
			recordedIn.close();
			adapter.close();
		}
		//System.err.println(runs);
		return System.currentTimeMillis()-start;
	}
}