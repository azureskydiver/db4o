package com.db4o.io;

import java.io.IOException;

public abstract class IoAdapter {
    
	private int _blockSize;
    
    protected final long regularAddress(int blockAddress, int blockAddressOffset){
        return (long)blockAddress * _blockSize + blockAddressOffset;
    }
    
    public void blockCopy(int oldAddress, int oldAddressOffset, int newAddress, int newAddressOffset, int length) throws IOException{
        copy(regularAddress(oldAddress, oldAddressOffset), regularAddress(newAddress, newAddressOffset), length);
    }
    
	public void blockSeek(int address) throws IOException {
		blockSeek(address,0);
	}

	public void blockSeek(int address, int offset)
			throws IOException {		
		seek(regularAddress(address,offset));
	}

	public void blockSize(int blockSize) {
		_blockSize=blockSize;
	}

	public abstract void close() throws IOException;

    public void copy(long oldAddress, long newAddress, int length) throws IOException{
        byte[] copyBytes = new byte[length];
        seek(oldAddress);
        read(copyBytes);
        seek(newAddress);
        write(copyBytes);
    }
    
	public abstract long getLength() throws IOException;

    public abstract IoAdapter open(String path, boolean lockFile, long initialLength) throws IOException;

	public int read(byte[] buffer) throws IOException {
		return read(buffer,buffer.length);		
	}

	public abstract int read(byte[] bytes, int length) throws IOException;

	public abstract void seek(long pos) throws IOException;

	public abstract void sync() throws IOException;

	public void write(byte[] bytes) throws IOException {
		write(bytes,bytes.length);
	}

	public abstract void write(byte[] buffer, int length) throws IOException;
	
	public int blockSize() {
		return _blockSize;
	}


}