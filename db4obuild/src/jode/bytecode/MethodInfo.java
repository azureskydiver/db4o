/* MethodInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;

public class MethodInfo extends BinaryInfo
{
    ClassInfo clazzInfo;
    int modifier;
    String name;
    String typeSig;
    BytecodeInfo bytecode;
    String[] exceptions;
    boolean syntheticFlag;
    boolean deprecatedFlag;
    
    public MethodInfo(ClassInfo classinfo) {
	clazzInfo = classinfo;
    }
    
    public MethodInfo(ClassInfo classinfo, String string, String string_0_,
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
	if ((i_1_ & 0x10) != 0 && string.equals("Code")) {
	    bytecode = new BytecodeInfo(this);
	    bytecode.read(constantpool, datainputstream);
	} else if (string.equals("Exceptions")) {
	    int i_2_ = datainputstream.readUnsignedShort();
	    exceptions = new String[i_2_];
	    for (int i_3_ = 0; i_3_ < i_2_; i_3_++)
		exceptions[i_3_]
		    = constantpool
			  .getClassName(datainputstream.readUnsignedShort());
	    if (i != 2 * (i_2_ + 1))
		throw new ClassFormatException
			  ("Exceptions attribute has wrong length");
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
	if (bytecode != null)
	    bytecode.reserveSmallConstants(growableconstantpool);
    }
    
    public void prepareWriting(GrowableConstantPool growableconstantpool) {
	growableconstantpool.putUTF8(name);
	growableconstantpool.putUTF8(typeSig);
	if (bytecode != null) {
	    growableconstantpool.putUTF8("Code");
	    bytecode.prepareWriting(growableconstantpool);
	}
	if (exceptions != null) {
	    growableconstantpool.putUTF8("Exceptions");
	    for (int i = 0; i < exceptions.length; i++)
		growableconstantpool.putClassName(exceptions[i]);
	}
	if (syntheticFlag)
	    growableconstantpool.putUTF8("Synthetic");
	if (deprecatedFlag)
	    growableconstantpool.putUTF8("Deprecated");
	this.prepareAttributes(growableconstantpool);
    }
    
    protected int getKnownAttributeCount() {
	int i = 0;
	if (bytecode != null)
	    i++;
	if (exceptions != null)
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
	if (bytecode != null) {
	    dataoutputstream.writeShort(growableconstantpool.putUTF8("Code"));
	    dataoutputstream.writeInt(bytecode.getSize());
	    bytecode.write(growableconstantpool, dataoutputstream);
	}
	if (exceptions != null) {
	    int i = exceptions.length;
	    dataoutputstream
		.writeShort(growableconstantpool.putUTF8("Exceptions"));
	    dataoutputstream.writeInt(2 + i * 2);
	    dataoutputstream.writeShort(i);
	    for (int i_4_ = 0; i_4_ < i; i_4_++)
		dataoutputstream.writeShort
		    (growableconstantpool.putClassName(exceptions[i_4_]));
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
	if ((i & 0x10) != 0) {
	    bytecode = null;
	    exceptions = null;
	}
	if (bytecode != null)
	    bytecode.dropInfo(i);
	super.dropInfo(i);
    }
    
    public ClassInfo getClazzInfo() {
	return clazzInfo;
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
    
    public boolean isStatic() {
	return Modifier.isStatic(modifier);
    }
    
    public boolean isSynthetic() {
	return syntheticFlag;
    }
    
    public boolean isDeprecated() {
	return deprecatedFlag;
    }
    
    public BytecodeInfo getBytecode() {
	return bytecode;
    }
    
    public String[] getExceptions() {
	return exceptions;
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
    
    public void setBytecode(BytecodeInfo bytecodeinfo) {
	clazzInfo.loadInfo(16);
	bytecode = bytecodeinfo;
    }
    
    public void setExceptions(String[] strings) {
	clazzInfo.loadInfo(16);
	exceptions = strings;
    }
    
    public String toString() {
	return ("Method " + Modifier.toString(modifier) + " " + typeSig + " "
		+ clazzInfo.getName() + "." + name);
    }
}
