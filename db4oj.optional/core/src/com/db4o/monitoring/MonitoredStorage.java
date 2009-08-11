package com.db4o.monitoring;

import com.db4o.io.*;

/**
 * Publishes JMX statistics through IOStatsMBean.
 */
@decaf.Ignore
public class MonitoredStorage extends StorageDecorator {

	public MonitoredStorage(Storage storage) {
		super(storage);
	}
	
	@Override
	protected Bin decorate(BinConfiguration config, Bin bin) {
		return new MonitoredBin(config.uri(), bin);
	}
	
	private static class MonitoredBin extends BinDecorator {

		private IOStats _ioMBean;

		public MonitoredBin(String uri, Bin bin) {
			super(bin);
			_ioMBean = Db4oMBeans.newIOStatsMBean(uri);
		}
		
		@Override
		public void close() {
			try {
				super.close();
			} finally {
				_ioMBean.unregister();
			}
		}
		
		@Override
		public void sync() {
			super.sync();
			_ioMBean.notifyFlush();
		}
		
		@Override
		public int read(long position, byte[] bytes, int bytesToRead) {
			int bytesRead = super.read(position, bytes, bytesToRead);
			_ioMBean.notifyBytesRead(bytesRead);
			return bytesRead;
		}
		
		@Override
		public int syncRead(long position, byte[] bytes, int bytesToRead) {
			int bytesRead = super.syncRead(position, bytes, bytesToRead);
			_ioMBean.notifyBytesRead(bytesRead);
			return bytesRead;
		}
		
		@Override
		public void write(long position, byte[] bytes, int bytesToWrite) {
			super.write(position, bytes, bytesToWrite);
			_ioMBean.notifyBytesWritten(bytesToWrite);
		}
	}
}
