/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

/**
 * public for .NET conversion reasons.
 */
public class YapReader {
	
	// for coding convenience, we allow objects to grab into the buffer
	byte[] i_bytes;
	int i_offset;

	
	YapReader(){
	}
	
	YapReader(int a_length){
		i_bytes = new byte[a_length];
	}
	
    void append(byte a_byte) {
        i_bytes[i_offset++] = a_byte;
    }

    void append(byte[] a_bytes) {
        System.arraycopy(a_bytes, 0, i_bytes, i_offset, a_bytes.length);
        i_offset += a_bytes.length;
    }
    
	final boolean containsTheSame(YapReader other) {
	    if(other != null) {
	        byte[] otherBytes = other.i_bytes;
	        if(i_bytes == null) {
	            return otherBytes == null;
	        }
	        if(otherBytes != null && i_bytes.length == otherBytes.length) {
	            int len = i_bytes.length;
	            for (int i = 0; i < len; i++) {
	                if(i_bytes[i] != otherBytes[i]) {
	                    return false;
	                }
	            }
	            return true;
	        }
	    }
	    return false;
	}
	
	int getLength() {
		return i_bytes.length;
	}
	
    void incrementOffset(int a_by) {
        i_offset += a_by;
    }
    
    /**
     * non-encrypted read, used for indexes
     * @param a_stream
     * @param a_address
     */
    void read(YapStream a_stream, int a_address){
        a_stream.readBytes(i_bytes, a_address, getLength());
    }
	
	void readBegin(byte a_identifier) {
		if (Deploy.debug) {
			if (Deploy.brackets) {
				if (readByte() != YapConst.YAPBEGIN) {
					throw new RuntimeException("YapBytes.readBegin() YAPBEGIN expected.");
				}
			}
			if (Deploy.identifiers) {
				byte readB = readByte();
				if (readB != a_identifier) {
					throw new RuntimeException("YapBytes.readBegin() wrong identifier");
				}
			}
		}
	}

    void readBegin(int a_id, byte a_identifier) {
        if (Deploy.debug) {
            readBegin(a_identifier);
        }
    }
	
	byte readByte() {
		return i_bytes[i_offset++];
	}
	
	byte[] readBytes(int a_length){
	    byte[] bytes = new byte[a_length];
	    System.arraycopy(i_bytes, i_offset, bytes, 0, a_length);
	    i_offset += a_length;
	    return bytes;
	}
    
	final YapReader readEmbeddedObject(Transaction a_trans) {
		return a_trans.i_stream.readObjectReaderByAddress(readInt(), readInt());
	}
	
	void readEncrypt(YapStream a_stream, int a_address) {
		a_stream.readBytes(i_bytes, a_address, getLength());
		a_stream.i_handlers.decrypt(this);
	}

    void readEnd() {
        if (Deploy.debug && Deploy.brackets) {
            if (readByte() != YapConst.YAPEND) {
                throw new RuntimeException("YapBytes.readEnd() YAPEND expected");
            }
        }
    }

    final int readInt() {
        if (Deploy.debug) {
            return YInt.readInt(this);
        } else {
            if (YapConst.INTEGER_BYTES == 4) {
                int o = (i_offset += 4) - 1;
                return (i_bytes[o] & 255) | (i_bytes[--o] & 255)
                    << 8 | (i_bytes[--o] & 255)
                    << 16 | i_bytes[--o]
                    << 24;
            } else {
            	int ret = 0;
                int ii = i_offset + YapConst.INTEGER_BYTES;
                while (i_offset < ii) {
                    ret = (ret << 8) + (i_bytes[i_offset++] & 0xff);
                }
				return ret;
            }
        }
		
    }

    void replaceWith(byte[] a_bytes) {
        System.arraycopy(a_bytes, 0, i_bytes, 0, getLength());
    }
    
    String toString(Transaction a_trans) {
        try {
            return (String)a_trans.i_stream.i_handlers.i_stringHandler.read1(this);
        } catch (Exception e) {
            if(Deploy.debug || Debug.atHome) {
                e.printStackTrace();
            }
        }
        return "";
    }
    
    void writeBegin(byte a_identifier) {
        if (Deploy.debug) {
            if (Deploy.brackets) {
                append(YapConst.YAPBEGIN);
            }
            if (Deploy.identifiers) {
                append(a_identifier);
            }
        }
    }
    
    void writeBegin(byte a_identifier, int a_length) {
        if (Deploy.debug) {
            writeBegin(a_identifier);
        }
    }
    
    void writeEnd() {
        if (Deploy.debug && Deploy.brackets) {
            append(YapConst.YAPEND);
        }
    }
    
    final void writeInt(int a_int) {
        if (Deploy.debug) {
            YInt.writeInt(a_int, this);
        } else {
            if (YapConst.INTEGER_BYTES == 4) {
                int o = i_offset + 4;
                i_offset = o;
                byte[] b = i_bytes;
                b[--o] = (byte)a_int;
                b[--o] = (byte) (a_int >>= 8);
                b[--o] = (byte) (a_int >>= 8);
                b[--o] = (byte) (a_int >>= 8);
            } else {
                for (int ii = YapConst.WRITE_LOOP; ii >= 0; ii -= 8) {
                    i_bytes[i_offset++] = (byte) (a_int >> ii);
                }
            }
        }
    }
    
}
