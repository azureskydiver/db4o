/* BinaryInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import jode.util.SimpleMap;

public class BinaryInfo
{
    public static final int HIERARCHY = 1;
    public static final int FIELDS = 2;
    public static final int METHODS = 4;
    public static final int CONSTANTS = 8;
    public static final int KNOWNATTRIBS = 16;
    public static final int INNERCLASSES = 32;
    public static final int OUTERCLASSES = 64;
    public static final int UNKNOWNATTRIBS = 128;
    public static final int FULLINFO = 255;
    public static final int MOSTINFO = 127;
    public static final int REFLECTINFO = 111;
    private Map unknownAttributes = null;
    
    static class ConstrainedInputStream extends FilterInputStream
    {
	int length;
	
	public ConstrainedInputStream(int i, InputStream inputstream) {
	    super(inputstream);
	    length = i;
	}
	
	public int read() throws IOException {
	    if (length > 0) {
		int i = super.read();
		length--;
		return i;
	    }
	    throw new EOFException();
	}
	
	public int read(byte[] is, int i, int i_0_) throws IOException {
	    if (length < i_0_)
		i_0_ = length;
	    if (i_0_ == 0)
		return -1;
	    int i_1_ = super.read(is, i, i_0_);
	    length -= i_1_;
	    return i_1_;
	}
	
	public int read(byte[] is) throws IOException {
	    return read(is, 0, is.length);
	}
	
	public long skip(long l) throws IOException {
	    if ((long) length < l)
		l = (long) length;
	    l = super.skip(l);
	    length -= (int) l;
	    return l;
	}
	
	public void skipRemaining() throws IOException {
	    int i;
	    for (/**/; length > 0; length -= i) {
		i = (int) skip((long) length);
		if (i == 0)
		    throw new EOFException();
	    }
	}
    }
    
    protected void skipAttributes(DataInputStream datainputstream)
	throws IOException {
	int i = datainputstream.readUnsignedShort();
	for (int i_2_ = 0; i_2_ < i; i_2_++) {
	    datainputstream.readUnsignedShort();
	    long l_3_;
	    for (long l = (long) datainputstream.readInt(); l > 0L;
		 l -= l_3_) {
		l_3_ = datainputstream.skip(l);
		if (l_3_ == 0L)
		    throw new EOFException("Can't skip. EOF?");
	    }
	}
    }
    
    protected int getKnownAttributeCount() {
	return 0;
    }
    
    protected void readAttribute
	(String string, int i, ConstantPool constantpool,
	 DataInputStream datainputstream, int i_4_)
	throws IOException {
	byte[] is = new byte[i];
	datainputstream.readFully(is);
	if ((i_4_ & 0x80) != 0) {
	    if (unknownAttributes == null)
		unknownAttributes = new SimpleMap();
	    unknownAttributes.put(string, is);
	}
    }
    
    protected void readAttributes
	(ConstantPool constantpool, DataInputStream datainputstream, int i)
	throws IOException {
	int i_5_ = datainputstream.readUnsignedShort();
	unknownAttributes = null;
	for (int i_6_ = 0; i_6_ < i_5_; i_6_++) {
	    String string
		= constantpool.getUTF8(datainputstream.readUnsignedShort());
	    int i_7_ = datainputstream.readInt();
	    ConstrainedInputStream constrainedinputstream
		= new ConstrainedInputStream(i_7_, datainputstream);
	    readAttribute(string, i_7_, constantpool,
			  new DataInputStream(constrainedinputstream), i);
	    constrainedinputstream.skipRemaining();
	}
    }
    
    public void dropInfo(int i) {
	if ((i & 0x80) != 0)
	    unknownAttributes = null;
    }
    
    protected void prepareAttributes
	(GrowableConstantPool growableconstantpool) {
	if (unknownAttributes != null) {
	    Iterator iterator = unknownAttributes.keySet().iterator();
	    while (iterator.hasNext())
		growableconstantpool.putUTF8((String) iterator.next());
	}
    }
    
    protected void writeKnownAttributes
	(GrowableConstantPool growableconstantpool,
	 DataOutputStream dataoutputstream)
	throws IOException {
	/* empty */
    }
    
    protected void writeAttributes
	(GrowableConstantPool growableconstantpool,
	 DataOutputStream dataoutputstream)
	throws IOException {
	int i = getKnownAttributeCount();
	if (unknownAttributes != null)
	    i += unknownAttributes.size();
	dataoutputstream.writeShort(i);
	writeKnownAttributes(growableconstantpool, dataoutputstream);
	if (unknownAttributes != null) {
	    Iterator iterator = unknownAttributes.entrySet().iterator();
	    while (iterator.hasNext()) {
		Map.Entry entry = (Map.Entry) iterator.next();
		String string = (String) entry.getKey();
		byte[] is = (byte[]) entry.getValue();
		dataoutputstream
		    .writeShort(growableconstantpool.putUTF8(string));
		dataoutputstream.writeInt(is.length);
		dataoutputstream.write(is);
	    }
	}
    }
    
    public int getAttributeSize() {
	int i = 2;
	if (unknownAttributes != null) {
	    Iterator iterator = unknownAttributes.values().iterator();
	    while (iterator.hasNext())
		i += 6 + ((byte[]) iterator.next()).length;
	}
	return i;
    }
    
    public byte[] findAttribute(String string) {
	if (unknownAttributes != null)
	    return (byte[]) unknownAttributes.get(string);
	return null;
    }
    
    public Iterator getAttributes() {
	if (unknownAttributes != null)
	    return unknownAttributes.values().iterator();
	return Collections.EMPTY_SET.iterator();
    }
    
    public void setAttribute(String string, byte[] is) {
	if (unknownAttributes == null)
	    unknownAttributes = new SimpleMap();
	unknownAttributes.put(string, is);
    }
    
    public byte[] removeAttribute(String string) {
	if (unknownAttributes != null)
	    return (byte[]) unknownAttributes.remove(string);
	return null;
    }
    
    public void removeAllAttributes() {
	unknownAttributes = null;
    }
}
