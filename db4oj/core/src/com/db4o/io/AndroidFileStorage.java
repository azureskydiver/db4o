/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.io;

import java.io.*;

import com.db4o.io.FileStorage.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore
public class AndroidFileStorage extends StorageDecorator {

	public AndroidFileStorage(FileStorage storage) {
		super(storage);
	}

	@Override
	protected Bin decorate(Bin bin) {
		return new AndroidBin((FileBin) bin);
	}
	
	private static class AndroidBin extends BinDecorator {

		public AndroidBin(FileBin bin) {
			super(bin);
		}

		@Override
		public void close() {
			// FIXME: This is a temporary quickfix for a bug in Android.
			//        Remove after Android has been fixed.
			try {
				FileBin fileBin = (FileBin)_bin;
				if (!fileBin.isClosed()) {
					fileBin.seek(0);
				}
			} catch (IOException e) {
				// ignore
			}
			super.close();
		}
	}
}
