/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.foundation.network;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * Transport buffer for C/S mode to simulate a
 * socket connection in memory.
 */
class BlockingByteChannel {

    private final static int DISCARD_BUFFER_SIZE = 500;
    private byte[] i_cache;
    private boolean i_closed = false;
    private int i_readOffset;
    private int i_timeout;
    private int i_writeOffset;
    private final Lock4 i_lock = new Lock4();

    public BlockingByteChannel(int timeout) {
        i_timeout = timeout;
    }

    protected int available() {
        return i_writeOffset - i_readOffset;
    }

    private void checkDiscardCache() {
        if (i_readOffset == i_writeOffset && i_cache.length > DISCARD_BUFFER_SIZE) {
            i_cache = null;
            i_readOffset = 0;
            i_writeOffset = 0;
        }
    }

    void close() {
        i_closed = true;
    }

    protected void makefit(int length) {
        if (i_cache == null) {
            i_cache = new byte[length];
        } else {
            // doesn't fit
            if (i_writeOffset + length > i_cache.length) {
                // move, if possible
                if (i_writeOffset + length - i_readOffset <= i_cache.length) {
                    byte[] temp = new byte[i_cache.length];
                    System.arraycopy(i_cache, i_readOffset, temp, 0, i_cache.length - i_readOffset);
                    i_cache = temp;
                    i_writeOffset -= i_readOffset;
                    i_readOffset = 0;

                    // else append
                } else {
                    byte[] temp = new byte[i_writeOffset + length];
                    System.arraycopy(i_cache, 0, temp, 0, i_cache.length);
                    i_cache = temp;
                }
            }
        }
    }

    public int read() throws IOException {
        try{
            Integer ret = (Integer)i_lock.run(new Closure4() {
                public Object run() throws Exception {
                    waitForAvailable();
                    int retVal = i_cache[i_readOffset++];
                    checkDiscardCache();
                    return new Integer(retVal);
                }
            
            });
            return ret.intValue();
        }catch(IOException iex){
            throw iex;
        }catch(Exception e){
            throw new Db4oUnexpectedException(e);
        }
    }

    public int read(final  byte[] a_bytes, final int a_offset, final int a_length) throws IOException {
        try{
            Integer ret = (Integer)i_lock.run(new Closure4() {
                public Object run() throws Exception {
                    waitForAvailable();
                    int avail = available();
                    int length = a_length;
                    if (avail < a_length) {
                        length = avail;
                    }
                    System.arraycopy(i_cache, i_readOffset, a_bytes, a_offset, length);
                    i_readOffset += length;
                    checkDiscardCache();
                    return new Integer(avail);
                }
            });
            return ret.intValue();
        }catch(IOException iex){
            throw iex;
        }catch(Exception e){
            throw new Db4oUnexpectedException(e);
        }
    }

    public void setTimeout(int timeout) {
        i_timeout = timeout;
    }

    private void waitForAvailable() throws IOException {
        while (available() == 0) {
        	if (i_closed) {
                throw new IOException(Messages.get(35));
            }
            try {
                i_lock.snooze(i_timeout);
            } catch (Exception e) {
                throw new IOException(Messages.get(55));
            }
        }
    }

    public void write(byte[] bytes) throws IOException {
    	write(bytes, 0, bytes.length);
    }
    
	public void write(final byte[] bytes, final int off, final int len) throws IOException {
		checkClosed();
		i_lock.run(new SafeClosure4() {
			public Object run() {
				makefit(len);
				System.arraycopy(bytes, off, i_cache, i_writeOffset, len);
				i_writeOffset += len;
				i_lock.awake();
				return null;
			}
		});
	}

    public void write(final int i) throws IOException {
    	checkClosed();
		i_lock.run(new SafeClosure4() {
			public Object run() {
				makefit(1);
				i_cache[i_writeOffset++] = (byte) i;
				i_lock.awake();
				return null;
			}
		});
	}

	private void checkClosed() throws IOException {
		if(i_closed) {
			throw new IOException();
		}		
	}
}
