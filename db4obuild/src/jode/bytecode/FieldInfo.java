/* FieldInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class FieldInfo extends BinaryInfo
{
    ClassInfo clazzInfo;
    int modifier;
    String name;
    String typeSig;
    Object constant;
    boolean syntheticFlag;
    boolean deprecatedFlag;
    
    public FieldInfo(ClassInfo classinfo) {
	clazzInfo = classinfo;
    }
    
    public FieldInfo(ClassInfo classinfo, String string, String string_0_,
		     int i) {
	clazzInfo = classinfo;
	name = string;
	typeSig = string_0_;
	modifier = i;
    }
    
    protected void readAttribute
	(String string, int i, ConstantPool constantpool,
	 DataInputStream datainputstream, int i_1_)
	throws IOException {
	if ((i_1_ & 0x10) != 0 && string.equals("ConstantValue")) {
	    if (i != 2)
		throw new ClassFormatException
			  ("ConstantValue attribute has wrong length");
	    int i_2_ = datainputstream.readUnsignedShort();
	    constant = constantpool.getConstant(i_2_);
	} else if (string.equals("Synthetic")) {
	    syntheticFlag = true;
	    if (i != 0)
		throw new ClassFormatException
			  ("Synthetic attribute has wrong length");
	} else if (string.equals("Deprecated")) {
	    deprecatedFlag = true;
	    if (i != 0)
		throw new ClassFormatException
			  ("Deprecated attribute has wrong length");
	} else
	    super.readAttribute(string, i, constantpool, datainputstream,
				i_1_);
    }
    
    public void read
	(ConstantPool constantpool, DataInputStream datainputstream, int i)
	throws IOException {
	modifier = datainputstream.readUnsignedShort();
	name = constantpool.getUTF8(datainputstream.readUnsignedShort());
	typeSig = constantpool.getUTF8(datainputstream.readUnsignedShort());
	this.readAttributes(constantpool, datainputstream, i);
    }
    
    public void reserveSmallConstants
	(GrowableConstantPool growableconstantpool) {
	/* empty */
    }
    
    public void prepareWriting(GrowableConstantPool growableconstantpool) {
	growableconstantpool.putUTF8(name);
	growableconstantpool.putUTF8(typeSig);
	if (constant != null) {
	    growableconstantpool.putUTF8("ConstantValue");
	    if (typeSig.charAt(0) == 'J' || typeSig.charAt(0) == 'D')
		growableconstantpool.putLongConstant(constant);
	    else
		growableconstantpool.putConstant(constant);
	}
	if (syntheticFlag)
	    growableconstantpool.putUTF8("Synthetic");
	if (deprecatedFlag)
	    growableconstantpool.putUTF8("Deprecated");
	this.prepareAttributes(growableconstantpool);
    }
    
    protected int getKnownAttributeCount() {
	int i = 0;
	if (constant != null)
	    i++;
	if (syntheticFlag)
	    i++;
	if (deprecatedFlag)
	    i++;
	return i;
    }
    
    public void writeKnownAttributes
	(GrowableConstantPool growableconstantpool,
	 DataOutputStream dataoutputstream)
	throws IOException {
	if (constant != null) {
	    dataoutputstream
		.writeShort(growableconstantpool.putUTF8("ConstantValue"));
	    dataoutputstream.writeInt(2);
	    int i;
	    if (typeSig.charAt(0) == 'J' || typeSig.charAt(0) == 'D')
		i = growableconstantpool.putLongConstant(constant);
	    else
		i = growableconstantpool.putConstant(constant);
	    dataoutputstream.writeShort(i);
	}
	if (syntheticFlag) {
	    dataoutputstream
		.writeShort(growableconstantpool.putUTF8("Synthetic"));
	    dataoutputstream.writeInt(0);
	}
	if (deprecatedFlag) {
	    dataoutputstream
		.writeShort(growableconstantpool.putUTF8("Deprecated"));
	    dataoutputstream.writeInt(0);
	}
    }
    
    public void write(GrowableConstantPool growableconstantpool,
		      DataOutputStream dataoutputstream) throws IOException {
	dataoutputstream.writeShort(modifier);
	dataoutputstream.writeShort(growableconstantpool.putUTF8(name));
	dataoutputstream.writeShort(growableconstantpool.putUTF8(typeSig));
	this.writeAttributes(growableconstantpool, dataoutputstream);
    }
    
    public void dropInfo(int i) {
	if ((i & 0x10) != 0)
	    constant = null;
	super.dropInfo(i);
    }
    
    public String getName() {
	return name;
    }
    
    public String getType() {
	return typeSig;
    }
    
    public int getModifiers() {
	return modifier;
    }
    
    public boolean isSynthetic() {
	return syntheticFlag;
    }
    
    public boolean isDeprecated() {
	return deprecatedFlag;
    }
    
    public Object getConstant() {
	clazzInfo.loadInfo(16);
	return constant;
    }
    
    public void setName(String string) {
	name = string;
    }
    
    public void setType(String string) {
	typeSig = string;
    }
    
    public void setModifiers(int i) {
	modifier = i;
    }
    
    public void setSynthetic(boolean bool) {
	syntheticFlag = bool;
    }
    
    public void setDeprecated(boolean bool) {
	deprecatedFlag = bool;
    }
    
    public void setConstant(Object object) {
	constant = object;
    }
    
    public String toString() {
	return ("Field " + Modifier.toString(modifier) + " " + typeSig + " "
		+ name);
    }
}
