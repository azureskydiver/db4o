/* ClassInfo - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.Iterator;

import jode.GlobalOptions;
import jode.util.UnifyHash;

public class ClassInfo extends BinaryInfo
{
    private static SearchPath classpath;
    private static final UnifyHash classes = new UnifyHash();
    private int status = 0;
    private boolean modified = false;
    private int modifiers = -1;
    private String name;
    private ClassInfo superclass;
    private ClassInfo[] interfaces;
    private FieldInfo[] fields;
    private MethodInfo[] methods;
    private InnerClassInfo[] outerClasses;
    private InnerClassInfo[] innerClasses;
    private InnerClassInfo[] extraClasses;
    private String sourceFile;
    public static final ClassInfo javaLangObject = forName("java.lang.Object");
    
    public static void setClassPath(String string) {
	setClassPath(new SearchPath(string));
    }
    
    public static void setClassPath(SearchPath searchpath) {
	if (classpath != searchpath) {
	    classpath = searchpath;
	    Iterator iterator = classes.iterator();
	    while (iterator.hasNext()) {
		ClassInfo classinfo = (ClassInfo) iterator.next();
		classinfo.status = 0;
		classinfo.superclass = null;
		classinfo.fields = null;
		classinfo.interfaces = null;
		classinfo.methods = null;
		classinfo.removeAllAttributes();
	    }
	}
    }
    
    public static boolean exists(String string) {
	return classpath.exists(string.replace('.', '/') + ".class");
    }
    
    public static boolean isPackage(String string) {
	return classpath.isDirectory(string.replace('.', '/'));
    }
    
    public static Enumeration getClassesAndPackages(String string) {
	final Enumeration enu = classpath.listFiles(string.replace('.', '/'));
	return new Enumeration() {
	    public boolean hasMoreElements() {
		return enu.hasMoreElements();
	    }
	    
	    public Object nextElement() {
		String string_0_ = (String) enu.nextElement();
		if (!string_0_.endsWith(".class"))
		    return string_0_;
		return string_0_.substring(0, string_0_.length() - 6);
	    }
	};
    }
    
    public static ClassInfo forName(String string) {
	if (string == null || string.indexOf(';') != -1
	    || string.indexOf('[') != -1 || string.indexOf('/') != -1)
	    throw new IllegalArgumentException("Illegal class name: "
					       + string);
	int i = string.hashCode();
	Iterator iterator = classes.iterateHashCode(i);
	while (iterator.hasNext()) {
	    ClassInfo classinfo = (ClassInfo) iterator.next();
	    if (classinfo.name.equals(string))
		return classinfo;
	}
	ClassInfo classinfo = new ClassInfo(string);
	classes.put(i, classinfo);
	return classinfo;
    }
    
    private ClassInfo(String string) {
	name = string;
    }
    
    protected void readAttribute
	(String string, int i, ConstantPool constantpool,
	 DataInputStream datainputstream, int i_1_)
	throws IOException {
	if ((i_1_ & 0x10) != 0 && string.equals("SourceFile")) {
	    if (i != 2)
		throw new ClassFormatException
			  ("SourceFile attribute has wrong length");
	    sourceFile
		= constantpool.getUTF8(datainputstream.readUnsignedShort());
	} else if ((i_1_ & 0x60) != 0 && string.equals("InnerClasses")) {
	    int i_2_ = datainputstream.readUnsignedShort();
	    if (i != 2 + 8 * i_2_)
		throw new ClassFormatException
			  ("InnerClasses attribute has wrong length");
	    int i_3_ = 0;
	    int i_4_ = 0;
	    int i_5_ = 0;
	    InnerClassInfo[] innerclassinfos = new InnerClassInfo[i_2_];
	    for (int i_6_ = 0; i_6_ < i_2_; i_6_++) {
		int i_7_ = datainputstream.readUnsignedShort();
		int i_8_ = datainputstream.readUnsignedShort();
		int i_9_ = datainputstream.readUnsignedShort();
		String string_10_ = constantpool.getClassName(i_7_);
		String string_11_
		    = i_8_ != 0 ? constantpool.getClassName(i_8_) : null;
		String string_12_
		    = i_9_ != 0 ? constantpool.getUTF8(i_9_) : null;
		int i_13_ = datainputstream.readUnsignedShort();
		if (string_12_ != null && string_12_.length() == 0)
		    string_12_ = null;
		if (string_11_ != null && string_12_ != null
		    && (string_10_.length()
			> string_11_.length() + 2 + string_12_.length())
		    && string_10_.startsWith(string_11_ + "$")
		    && string_10_.endsWith("$" + string_12_)
		    && Character.isDigit(string_10_
					     .charAt(string_11_.length() + 1)))
		    string_11_ = null;
		InnerClassInfo innerclassinfo
		    = new InnerClassInfo(string_10_, string_11_, string_12_,
					 i_13_);
		if (string_11_ != null && string_11_.equals(getName())
		    && string_12_ != null)
		    innerclassinfos[i_3_++] = innerclassinfo;
		else
		    innerclassinfos[i_2_ - ++i_5_] = innerclassinfo;
	    }
	    String string_14_ = getName();
	    for (int i_15_ = i_2_ - i_5_; i_15_ < i_2_ && string_14_ != null;
		 i_15_++) {
		InnerClassInfo innerclassinfo = innerclassinfos[i_15_];
		if (innerclassinfo.inner.equals(string_14_)) {
		    i_4_++;
		    i_5_--;
		    string_14_ = innerclassinfo.outer;
		}
	    }
	    if (i_3_ > 0) {
		innerClasses = new InnerClassInfo[i_3_];
		System.arraycopy(innerclassinfos, 0, innerClasses, 0, i_3_);
	    } else
		innerClasses = null;
	    if (i_4_ > 0)
		outerClasses = new InnerClassInfo[i_4_];
	    else
		outerClasses = null;
	    if (i_5_ > 0)
		extraClasses = new InnerClassInfo[i_5_];
	    else
		extraClasses = null;
	    int i_16_ = 0;
	    String string_17_ = getName();
	    for (int i_18_ = i_2_ - i_5_ - i_4_; i_18_ < i_2_; i_18_++) {
		InnerClassInfo innerclassinfo = innerclassinfos[i_18_];
		if (innerclassinfo.inner.equals(string_17_)) {
		    outerClasses[i_16_++] = innerclassinfo;
		    string_17_ = innerclassinfo.outer;
		} else
		    extraClasses[--i_5_] = innerclassinfo;
	    }
	} else
	    super.readAttribute(string, i, constantpool, datainputstream,
				i_1_);
    }
    
    public void read(DataInputStream datainputstream, int i)
	throws IOException {
	i |= 0x61;
	i &= status ^ 0xffffffff;
	if (datainputstream.readInt() != -889275714)
	    throw new ClassFormatException("Wrong magic");
	int i_19_ = datainputstream.readUnsignedShort();
	i_19_ |= datainputstream.readUnsignedShort() << 16;
	if (i_19_ < 2949120 || i_19_ > 3080192)
	    throw new ClassFormatException("Wrong class version");
	ConstantPool constantpool = new ConstantPool();
	constantpool.read(datainputstream);
	modifiers = datainputstream.readUnsignedShort();
	String string
	    = constantpool.getClassName(datainputstream.readUnsignedShort());
	if (!name.equals(string))
	    throw new ClassFormatException("wrong name " + string);
	String string_20_
	    = constantpool.getClassName(datainputstream.readUnsignedShort());
	superclass = string_20_ != null ? forName(string_20_) : null;
	int i_21_ = datainputstream.readUnsignedShort();
	interfaces = new ClassInfo[i_21_];
	for (int i_22_ = 0; i_22_ < i_21_; i_22_++)
	    interfaces[i_22_]
		= forName(constantpool.getClassName(datainputstream
							.readUnsignedShort()));
	status |= 0x1;
	if ((i & 0x92) != 0) {
	    int i_23_ = datainputstream.readUnsignedShort();
	    if ((status & 0x2) == 0)
		fields = new FieldInfo[i_23_];
	    for (int i_24_ = 0; i_24_ < i_23_; i_24_++) {
		if ((status & 0x2) == 0)
		    fields[i_24_] = new FieldInfo(this);
		fields[i_24_].read(constantpool, datainputstream, i);
	    }
	} else {
	    byte[] is = new byte[6];
	    int i_25_ = datainputstream.readUnsignedShort();
	    for (i_21_ = 0; i_21_ < i_25_; i_21_++) {
		datainputstream.readFully(is);
		this.skipAttributes(datainputstream);
	    }
	}
	if ((i & 0x94) != 0) {
	    int i_26_ = datainputstream.readUnsignedShort();
	    if ((status & 0x4) == 0)
		methods = new MethodInfo[i_26_];
	    for (int i_27_ = 0; i_27_ < i_26_; i_27_++) {
		if ((status & 0x4) == 0)
		    methods[i_27_] = new MethodInfo(this);
		methods[i_27_].read(constantpool, datainputstream, i);
	    }
	} else {
	    byte[] is = new byte[6];
	    int i_28_ = datainputstream.readUnsignedShort();
	    for (i_21_ = 0; i_21_ < i_28_; i_21_++) {
		datainputstream.readFully(is);
		this.skipAttributes(datainputstream);
	    }
	}
	this.readAttributes(constantpool, datainputstream, i);
	status |= i;
    }
    
    public void reserveSmallConstants
	(GrowableConstantPool growableconstantpool) {
	for (int i = 0; i < fields.length; i++)
	    fields[i].reserveSmallConstants(growableconstantpool);
	for (int i = 0; i < methods.length; i++)
	    methods[i].reserveSmallConstants(growableconstantpool);
    }
    
    public void prepareWriting(GrowableConstantPool growableconstantpool) {
	growableconstantpool.putClassName(name);
	growableconstantpool.putClassName(superclass.getName());
	for (int i = 0; i < interfaces.length; i++)
	    growableconstantpool.putClassName(interfaces[i].getName());
	for (int i = 0; i < fields.length; i++)
	    fields[i].prepareWriting(growableconstantpool);
	for (int i = 0; i < methods.length; i++)
	    methods[i].prepareWriting(growableconstantpool);
	if (sourceFile != null) {
	    growableconstantpool.putUTF8("SourceFile");
	    growableconstantpool.putUTF8(sourceFile);
	}
	if (outerClasses != null || innerClasses != null
	    || extraClasses != null) {
	    growableconstantpool.putUTF8("InnerClasses");
	    int i = outerClasses != null ? outerClasses.length : 0;
	    int i_29_ = i;
	    while (i_29_-- > 0) {
		growableconstantpool.putClassName(outerClasses[i_29_].inner);
		if (outerClasses[i_29_].outer != null)
		    growableconstantpool
			.putClassName(outerClasses[i_29_].outer);
		if (outerClasses[i_29_].name != null)
		    growableconstantpool.putUTF8(outerClasses[i_29_].name);
	    }
	    int i_30_ = innerClasses != null ? innerClasses.length : 0;
	    for (int i_31_ = 0; i_31_ < i_30_; i_31_++) {
		growableconstantpool.putClassName(innerClasses[i_31_].inner);
		if (innerClasses[i_31_].outer != null)
		    growableconstantpool
			.putClassName(innerClasses[i_31_].outer);
		if (innerClasses[i_31_].name != null)
		    growableconstantpool.putUTF8(innerClasses[i_31_].name);
	    }
	    int i_32_ = extraClasses != null ? extraClasses.length : 0;
	    for (int i_33_ = 0; i_33_ < i_32_; i_33_++) {
		growableconstantpool.putClassName(extraClasses[i_33_].inner);
		if (extraClasses[i_33_].outer != null)
		    growableconstantpool
			.putClassName(extraClasses[i_33_].outer);
		if (extraClasses[i_33_].name != null)
		    growableconstantpool.putUTF8(extraClasses[i_33_].name);
	    }
	}
	this.prepareAttributes(growableconstantpool);
    }
    
    protected int getKnownAttributeCount() {
	int i = 0;
	if (sourceFile != null)
	    i++;
	if (innerClasses != null || outerClasses != null
	    || extraClasses != null)
	    i++;
	return i;
    }
    
    public void writeKnownAttributes
	(GrowableConstantPool growableconstantpool,
	 DataOutputStream dataoutputstream)
	throws IOException {
	if (sourceFile != null) {
	    dataoutputstream
		.writeShort(growableconstantpool.putUTF8("SourceFile"));
	    dataoutputstream.writeInt(2);
	    dataoutputstream
		.writeShort(growableconstantpool.putUTF8(sourceFile));
	}
	if (outerClasses != null || innerClasses != null
	    || extraClasses != null) {
	    dataoutputstream
		.writeShort(growableconstantpool.putUTF8("InnerClasses"));
	    int i = outerClasses != null ? outerClasses.length : 0;
	    int i_34_ = innerClasses != null ? innerClasses.length : 0;
	    int i_35_ = extraClasses != null ? extraClasses.length : 0;
	    int i_36_ = i + i_34_ + i_35_;
	    dataoutputstream.writeInt(2 + i_36_ * 8);
	    dataoutputstream.writeShort(i_36_);
	    int i_37_ = i;
	    while (i_37_-- > 0) {
		dataoutputstream.writeShort(growableconstantpool.putClassName
					    (outerClasses[i_37_].inner));
		dataoutputstream.writeShort
		    (outerClasses[i_37_].outer != null
		     ? growableconstantpool
			   .putClassName(outerClasses[i_37_].outer)
		     : 0);
		dataoutputstream.writeShort
		    (outerClasses[i_37_].name != null
		     ? growableconstantpool.putUTF8(outerClasses[i_37_].name)
		     : 0);
		dataoutputstream.writeShort(outerClasses[i_37_].modifiers);
	    }
	    for (int i_38_ = 0; i_38_ < i_34_; i_38_++) {
		dataoutputstream.writeShort(growableconstantpool.putClassName
					    (innerClasses[i_38_].inner));
		dataoutputstream.writeShort
		    (innerClasses[i_38_].outer != null
		     ? growableconstantpool
			   .putClassName(innerClasses[i_38_].outer)
		     : 0);
		dataoutputstream.writeShort
		    (innerClasses[i_38_].name != null
		     ? growableconstantpool.putUTF8(innerClasses[i_38_].name)
		     : 0);
		dataoutputstream.writeShort(innerClasses[i_38_].modifiers);
	    }
	    for (int i_39_ = 0; i_39_ < i_35_; i_39_++) {
		dataoutputstream.writeShort(growableconstantpool.putClassName
					    (extraClasses[i_39_].inner));
		dataoutputstream.writeShort
		    (extraClasses[i_39_].outer != null
		     ? growableconstantpool
			   .putClassName(extraClasses[i_39_].outer)
		     : 0);
		dataoutputstream.writeShort
		    (extraClasses[i_39_].name != null
		     ? growableconstantpool.putUTF8(extraClasses[i_39_].name)
		     : 0);
		dataoutputstream.writeShort(extraClasses[i_39_].modifiers);
	    }
	}
    }
    
    public void write(DataOutputStream dataoutputstream) throws IOException {
	GrowableConstantPool growableconstantpool = new GrowableConstantPool();
	reserveSmallConstants(growableconstantpool);
	prepareWriting(growableconstantpool);
	dataoutputstream.writeInt(-889275714);
	dataoutputstream.writeShort(3);
	dataoutputstream.writeShort(45);
	growableconstantpool.write(dataoutputstream);
	dataoutputstream.writeShort(modifiers);
	dataoutputstream.writeShort(growableconstantpool.putClassName(name));
	dataoutputstream.writeShort(growableconstantpool
					.putClassName(superclass.getName()));
	dataoutputstream.writeShort(interfaces.length);
	for (int i = 0; i < interfaces.length; i++)
	    dataoutputstream.writeShort
		(growableconstantpool.putClassName(interfaces[i].getName()));
	dataoutputstream.writeShort(fields.length);
	for (int i = 0; i < fields.length; i++)
	    fields[i].write(growableconstantpool, dataoutputstream);
	dataoutputstream.writeShort(methods.length);
	for (int i = 0; i < methods.length; i++)
	    methods[i].write(growableconstantpool, dataoutputstream);
	this.writeAttributes(growableconstantpool, dataoutputstream);
    }
    
    public void loadInfoReflection(Class var_class, int i)
	throws SecurityException {
	if ((i & 0x1) != 0) {
	    modifiers = var_class.getModifiers();
	    if (var_class.getSuperclass() == null)
		superclass = var_class == Object.class ? null : javaLangObject;
	    else
		superclass = forName(var_class.getSuperclass().getName());
	    Class[] var_classes = var_class.getInterfaces();
	    interfaces = new ClassInfo[var_classes.length];
	    for (int i_40_ = 0; i_40_ < var_classes.length; i_40_++)
		interfaces[i_40_] = forName(var_classes[i_40_].getName());
	    status |= 0x1;
	}
	if ((i & 0x2) != 0 && this.fields == null) {
	    Field[] fields;
	    try {
		fields = var_class.getDeclaredFields();
	    } catch (SecurityException securityexception) {
		fields = var_class.getFields();
		GlobalOptions.err.println
		    ("Could only get public fields of class " + name + ".");
	    }
	    this.fields = new FieldInfo[fields.length];
	    int i_41_ = fields.length;
	    while (--i_41_ >= 0) {
		String string
		    = TypeSignature.getSignature(fields[i_41_].getType());
		this.fields[i_41_]
		    = new FieldInfo(this, fields[i_41_].getName(), string,
				    fields[i_41_].getModifiers());
	    }
	}
	if ((i & 0x4) != 0 && this.methods == null) {
	    Constructor[] constructors;
	    Method[] methods;
	    try {
		constructors = var_class.getDeclaredConstructors();
		methods = var_class.getDeclaredMethods();
	    } catch (SecurityException securityexception) {
		constructors = var_class.getConstructors();
		methods = var_class.getMethods();
		GlobalOptions.err.println
		    ("Could only get public methods of class " + name + ".");
	    }
	    this.methods
		= new MethodInfo[constructors.length + methods.length];
	    int i_42_ = constructors.length;
	    while (--i_42_ >= 0) {
		String string
		    = TypeSignature.getSignature(constructors[i_42_]
						     .getParameterTypes(),
						 Void.TYPE);
		this.methods[i_42_]
		    = new MethodInfo(this, "<init>", string,
				     constructors[i_42_].getModifiers());
	    }
	    int i_43_ = methods.length;
	    while (--i_43_ >= 0) {
		String string
		    = TypeSignature.getSignature(methods[i_43_]
						     .getParameterTypes(),
						 methods[i_43_]
						     .getReturnType());
		this.methods[constructors.length + i_43_]
		    = new MethodInfo(this, methods[i_43_].getName(), string,
				     methods[i_43_].getModifiers());
	    }
	}
	if ((i & 0x20) != 0 && innerClasses == null) {
	    Class[] var_classes;
	    try {
		var_classes = var_class.getDeclaredClasses();
	    } catch (SecurityException securityexception) {
		var_classes = var_class.getClasses();
		GlobalOptions.err.println
		    ("Could only get public inner classes of class " + name
		     + ".");
	    }
	    if (var_classes.length > 0) {
		innerClasses = new InnerClassInfo[var_classes.length];
		int i_44_ = var_classes.length;
		while (--i_44_ >= 0) {
		    String string = var_classes[i_44_].getName();
		    int i_45_ = string.lastIndexOf('$');
		    String string_46_ = string.substring(i_45_ + 1);
		    innerClasses[i_44_]
			= new InnerClassInfo(string, getName(), string_46_,
					     var_classes[i_44_]
						 .getModifiers());
		}
	    }
	}
	if ((i & 0x40) != 0 && outerClasses == null) {
	    int i_47_ = 0;
	    for (Class var_class_48_ = var_class.getDeclaringClass();
		 var_class_48_ != null;
		 var_class_48_ = var_class_48_.getDeclaringClass())
		i_47_++;
	    if (i_47_ > 0) {
		outerClasses = new InnerClassInfo[i_47_];
		Class var_class_49_ = var_class;
		for (int i_50_ = 0; i_50_ < i_47_; i_50_++) {
		    Class var_class_51_ = var_class_49_.getDeclaringClass();
		    String string = var_class_49_.getName();
		    int i_52_ = string.lastIndexOf('$');
		    outerClasses[i_50_]
			= new InnerClassInfo(string, var_class_51_.getName(),
					     string.substring(i_52_ + 1),
					     var_class_49_.getModifiers());
		    var_class_49_ = var_class_51_;
		}
	    }
	}
	status |= i;
    }
    
    public void loadInfo(int i) {
	if ((status & i) != i) {
	    if (modified) {
		System.err.println("Allocating info 0x"
				   + Integer.toHexString(i) + " (status 0x"
				   + Integer.toHexString(status)
				   + ") in class " + this);
		Thread.dumpStack();
	    } else {
		try {
		    DataInputStream datainputstream
			= (new DataInputStream
			   (new BufferedInputStream
			    (classpath.getFile(name.replace('.', '/')
					       + ".class"))));
		    read(datainputstream, i);
		} catch (IOException ioexception) {
		    String string = ioexception.getMessage();
		    if ((i & ~0x67) != 0)
			throw new NoClassDefFoundError(name);
		    Class var_class = null;
		    try {
			var_class = Class.forName(name);
		    } catch (ClassNotFoundException classnotfoundexception) {
			/* empty */
		    } catch (NoClassDefFoundError noclassdeffounderror) {
			/* empty */
		    }
		    try {
			if (var_class != null) {
			    loadInfoReflection(var_class, i);
			    return;
			}
		    } catch (SecurityException securityexception) {
			GlobalOptions.err.println
			    (securityexception
			     + " while collecting info about class " + name
			     + ".");
		    }
		    GlobalOptions.err.println("Can't read class " + name
					      + ", types may be incorrect. ("
					      + ioexception.getClass()
						    .getName()
					      + (string != null ? ": " + string
						 : "")
					      + ")");
		    ioexception.printStackTrace(GlobalOptions.err);
		    if ((i & 0x1) != 0) {
			modifiers = 1;
			if (name.equals("java.lang.Object"))
			    superclass = null;
			else
			    superclass = javaLangObject;
			interfaces = new ClassInfo[0];
		    }
		    if ((i & 0x4) != 0)
			methods = new MethodInfo[0];
		    if ((i & 0x2) != 0)
			fields = new FieldInfo[0];
		    status |= i;
		}
	    }
	}
    }
    
    public void dropInfo(int i) {
	if ((status & i) != 0) {
	    if (modified) {
		System.err.println("Dropping info 0x" + Integer.toHexString(i)
				   + " (status 0x"
				   + Integer.toHexString(status)
				   + ") in class " + this);
		Thread.dumpStack();
	    } else {
		i &= status;
		if ((i & 0x2) != 0)
		    fields = null;
		else if ((status & 0x2) != 0 && (i & 0x90) != 0) {
		    for (int i_53_ = 0; i_53_ < fields.length; i_53_++)
			fields[i_53_].dropInfo(i);
		}
		if ((i & 0x4) != 0)
		    methods = null;
		else if ((status & 0x4) != 0 && (i & 0x90) != 0) {
		    for (int i_54_ = 0; i_54_ < methods.length; i_54_++)
			methods[i_54_].dropInfo(i);
		}
		if ((i & 0x10) != 0)
		    sourceFile = null;
		if ((i & 0x40) != 0)
		    outerClasses = null;
		if ((i & 0x20) != 0) {
		    innerClasses = null;
		    extraClasses = null;
		}
		super.dropInfo(i);
		status &= i ^ 0xffffffff;
	    }
	}
    }
    
    public String getName() {
	return name;
    }
    
    public String getJavaName() {
	if (name.indexOf('$') == -1)
	    return getName();
	if (getOuterClasses() != null) {
	    int i = outerClasses.length - 1;
	    StringBuffer stringbuffer
		= new StringBuffer(outerClasses[i].outer != null
				   ? outerClasses[i].outer : "METHOD");
	    for (int i_55_ = i; i_55_ >= 0; i_55_--)
		stringbuffer.append(".").append((outerClasses[i_55_].name
						 != null)
						? outerClasses[i_55_].name
						: "ANONYMOUS");
	    return stringbuffer.toString();
	}
	return getName();
    }
    
    public ClassInfo getSuperclass() {
	if ((status & 0x1) == 0)
	    loadInfo(1);
	return superclass;
    }
    
    public ClassInfo[] getInterfaces() {
	if ((status & 0x1) == 0)
	    loadInfo(1);
	return interfaces;
    }
    
    public int getModifiers() {
	if ((status & 0x1) == 0)
	    loadInfo(1);
	return modifiers;
    }
    
    public boolean isInterface() {
	return Modifier.isInterface(getModifiers());
    }
    
    public FieldInfo findField(String string, String string_56_) {
	if ((status & 0x2) == 0)
	    loadInfo(2);
	for (int i = 0; i < fields.length; i++) {
	    if (fields[i].getName().equals(string)
		&& fields[i].getType().equals(string_56_))
		return fields[i];
	}
	return null;
    }
    
    public MethodInfo findMethod(String string, String string_57_) {
	if ((status & 0x4) == 0)
	    loadInfo(4);
	for (int i = 0; i < methods.length; i++) {
	    if (methods[i].getName().equals(string)
		&& methods[i].getType().equals(string_57_))
		return methods[i];
	}
	return null;
    }
    
    public MethodInfo[] getMethods() {
	if ((status & 0x4) == 0)
	    loadInfo(4);
	return methods;
    }
    
    public FieldInfo[] getFields() {
	if ((status & 0x2) == 0)
	    loadInfo(2);
	return fields;
    }
    
    public InnerClassInfo[] getOuterClasses() {
	if ((status & 0x40) == 0)
	    loadInfo(64);
	return outerClasses;
    }
    
    public InnerClassInfo[] getInnerClasses() {
	if ((status & 0x20) == 0)
	    loadInfo(32);
	return innerClasses;
    }
    
    public InnerClassInfo[] getExtraClasses() {
	if ((status & 0x20) == 0)
	    loadInfo(32);
	return extraClasses;
    }
    
    public String getSourceFile() {
	return sourceFile;
    }
    
    public void setName(String string) {
	name = string;
	modified = true;
    }
    
    public void setSuperclass(ClassInfo classinfo_58_) {
	superclass = classinfo_58_;
	modified = true;
    }
    
    public void setInterfaces(ClassInfo[] classinfos) {
	interfaces = classinfos;
	modified = true;
    }
    
    public void setModifiers(int i) {
	modifiers = i;
	modified = true;
    }
    
    public void setMethods(MethodInfo[] methodinfos) {
	methods = methodinfos;
	modified = true;
    }
    
    public void setFields(FieldInfo[] fieldinfos) {
	fields = fieldinfos;
	modified = true;
    }
    
    public void setOuterClasses(InnerClassInfo[] innerclassinfos) {
	outerClasses = innerclassinfos;
	modified = true;
    }
    
    public void setInnerClasses(InnerClassInfo[] innerclassinfos) {
	innerClasses = innerclassinfos;
	modified = true;
    }
    
    public void setExtraClasses(InnerClassInfo[] innerclassinfos) {
	extraClasses = innerclassinfos;
	modified = true;
    }
    
    public void setSourceFile(String string) {
	sourceFile = string;
	modified = true;
    }
    
    public boolean superClassOf(ClassInfo classinfo_59_) {
	for (/**/; classinfo_59_ != this && classinfo_59_ != null;
	     classinfo_59_ = classinfo_59_.getSuperclass()) {
	    /* empty */
	}
	return classinfo_59_ == this;
    }
    
    public boolean implementedBy(ClassInfo classinfo_60_) {
	for (/**/; classinfo_60_ != this && classinfo_60_ != null;
	     classinfo_60_ = classinfo_60_.getSuperclass()) {
	    ClassInfo[] classinfos = classinfo_60_.getInterfaces();
	    for (int i = 0; i < classinfos.length; i++) {
		if (implementedBy(classinfos[i]))
		    return true;
	    }
	}
	return classinfo_60_ == this;
    }
    
    public String toString() {
	return name;
    }
}
