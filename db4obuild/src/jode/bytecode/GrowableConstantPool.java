/* GrowableConstantPool - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class GrowableConstantPool extends ConstantPool
{
    Hashtable entryToIndex = new Hashtable();
    boolean written;
    
    private class Key
    {
	int tag;
	Object objData;
	int intData;
	
	public Key(int i, Object object, int i_0_) {
	    tag = i;
	    objData = object;
	    intData = i_0_;
	}
	
	public int hashCode() {
	    return tag ^ objData.hashCode() ^ intData;
	}
	
	public boolean equals(Object object) {
	    if (object instanceof Key) {
		Key key_1_ = (Key) object;
		return (tag == key_1_.tag && intData == key_1_.intData
			&& objData.equals(key_1_.objData));
	    }
	    return false;
	}
    }
    
    public GrowableConstantPool() {
	count = 1;
	tags = new int[128];
	indices1 = new int[128];
	indices2 = new int[128];
	constants = new Object[128];
	written = false;
    }
    
    public final void grow(int i) {
	if (written)
	    throw new IllegalStateException("adding to written ConstantPool");
	if (tags.length < i) {
	    int i_2_ = Math.max(tags.length * 2, i);
	    int[] is = new int[i_2_];
	    System.arraycopy(tags, 0, is, 0, count);
	    tags = is;
	    is = new int[i_2_];
	    System.arraycopy(indices1, 0, is, 0, count);
	    indices1 = is;
	    is = new int[i_2_];
	    System.arraycopy(indices2, 0, is, 0, count);
	    indices2 = is;
	    Object[] objects = new Object[i_2_];
	    System.arraycopy(constants, 0, objects, 0, count);
	    constants = objects;
	}
    }
    
    private int putConstant(int i, Object object) {
	Key key = new Key(i, object, 0);
	Integer integer = (Integer) entryToIndex.get(key);
	if (integer != null)
	    return integer.intValue();
	int i_3_ = count;
	grow(count + 1);
	tags[i_3_] = i;
	constants[i_3_] = object;
	entryToIndex.put(key, new Integer(i_3_));
	count++;
	return i_3_;
    }
    
    private int putLongConstant(int i, Object object) {
	Key key = new Key(i, object, 0);
	Integer integer = (Integer) entryToIndex.get(key);
	if (integer != null)
	    return integer.intValue();
	int i_4_ = count;
	grow(count + 2);
	tags[i_4_] = i;
	tags[i_4_ + 1] = -i;
	constants[i_4_] = object;
	entryToIndex.put(key, new Integer(i_4_));
	count += 2;
	return i_4_;
    }
    
    int putIndexed(int i, Object object, int i_5_, int i_6_) {
	Key key = new Key(i, object, i_6_);
	Integer integer = (Integer) entryToIndex.get(key);
	if (integer != null) {
	    int i_7_ = integer.intValue();
	    indices1[i_7_] = i_5_;
	    indices2[i_7_] = i_6_;
	    return i_7_;
	}
	grow(count + 1);
	tags[count] = i;
	indices1[count] = i_5_;
	indices2[count] = i_6_;
	entryToIndex.put(key, new Integer(count));
	return count++;
    }
    
    public final int putUTF8(String string) {
	return putConstant(1, string);
    }
    
    public int putClassName(String string) {
	string = string.replace('.', '/');
	TypeSignature.checkTypeSig("L" + string + ";");
	return putIndexed(7, string, putUTF8(string), 0);
    }
    
    public int putClassType(String string) {
	TypeSignature.checkTypeSig(string);
	if (string.charAt(0) == 'L')
	    string = string.substring(1, string.length() - 1);
	else if (string.charAt(0) != '[')
	    throw new IllegalArgumentException("wrong class type: " + string);
	return putIndexed(7, string, putUTF8(string), 0);
    }
    
    public int putRef(int i, Reference reference) {
	String string = reference.getClazz();
	String string_8_ = reference.getType();
	if (i == 9)
	    TypeSignature.checkTypeSig(string_8_);
	else
	    TypeSignature.checkMethodTypeSig(string_8_);
	int i_9_ = putClassType(string);
	int i_10_ = putUTF8(reference.getName());
	int i_11_ = putUTF8(string_8_);
	int i_12_ = putIndexed(12, reference.getName(), i_10_, i_11_);
	return putIndexed(i, string, i_9_, i_12_);
    }
    
    public int putConstant(Object object) {
	if (object instanceof String)
	    return putIndexed(8, object, putUTF8((String) object), 0);
	int i;
	if (object instanceof Integer)
	    i = 3;
	else if (object instanceof Float)
	    i = 4;
	else
	    throw new IllegalArgumentException("illegal constant " + object
					       + " of type: "
					       + object.getClass());
	return putConstant(i, object);
    }
    
    public int putLongConstant(Object object) {
	int i;
	if (object instanceof Long)
	    i = 5;
	else if (object instanceof Double)
	    i = 6;
	else
	    throw new IllegalArgumentException("illegal long constant "
					       + object + " of type: "
					       + object.getClass());
	return putLongConstant(i, object);
    }
    
    public int reserveConstant(Object object) {
	if (object instanceof String)
	    return putIndexed(8, object, -1, 0);
	return putConstant(object);
    }
    
    public int reserveLongConstant(Object object) {
	return putLongConstant(object);
    }
    
    public int copyConstant(ConstantPool constantpool, int i)
	throws ClassFormatException {
	return putConstant(constantpool.getConstant(i));
    }
    
    public void write(DataOutputStream dataoutputstream) throws IOException {
	written = true;
	dataoutputstream.writeShort(count);
	for (int i = 1; i < count; i++) {
	    int i_13_ = tags[i];
	    dataoutputstream.writeByte(i_13_);
	    switch (i_13_) {
	    case 7:
		dataoutputstream.writeShort(indices1[i]);
		break;
	    case 9:
	    case 10:
	    case 11:
		dataoutputstream.writeShort(indices1[i]);
		dataoutputstream.writeShort(indices2[i]);
		break;
	    case 8:
		dataoutputstream.writeShort(indices1[i]);
		break;
	    case 3:
		dataoutputstream.writeInt(((Integer) constants[i]).intValue());
		break;
	    case 4:
		dataoutputstream
		    .writeFloat(((Float) constants[i]).floatValue());
		break;
	    case 5:
		dataoutputstream.writeLong(((Long) constants[i]).longValue());
		i++;
		break;
	    case 6:
		dataoutputstream
		    .writeDouble(((Double) constants[i]).doubleValue());
		i++;
		break;
	    case 12:
		dataoutputstream.writeShort(indices1[i]);
		dataoutputstream.writeShort(indices2[i]);
		break;
	    case 1:
		dataoutputstream.writeUTF((String) constants[i]);
		break;
	    default:
		throw new ClassFormatException("unknown constant tag");
	    }
	}
    }
}
