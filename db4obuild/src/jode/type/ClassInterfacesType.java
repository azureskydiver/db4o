/* ClassInterfacesType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.type;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import jode.AssertError;
import jode.bytecode.ClassInfo;

public class ClassInterfacesType extends ReferenceType
{
    ClassInfo clazz;
    ClassInfo[] ifaces;
    private static final Hashtable keywords = new Hashtable();
    
    public ClassInfo getClazz() {
	return clazz != null ? clazz : ClassInfo.javaLangObject;
    }
    
    public ClassInterfacesType(String string) {
	super(10);
	ClassInfo classinfo = ClassInfo.forName(string);
	if (classinfo.isInterface()) {
	    clazz = null;
	    ifaces = new ClassInfo[] { classinfo };
	} else {
	    clazz = classinfo == ClassInfo.javaLangObject ? null : classinfo;
	    ifaces = new ClassInfo[0];
	}
    }
    
    public ClassInterfacesType(ClassInfo classinfo) {
	super(10);
	if (classinfo.isInterface()) {
	    clazz = null;
	    ifaces = new ClassInfo[] { classinfo };
	} else {
	    clazz = classinfo == ClassInfo.javaLangObject ? null : classinfo;
	    ifaces = new ClassInfo[0];
	}
    }
    
    public ClassInterfacesType(ClassInfo classinfo, ClassInfo[] classinfos) {
	super(10);
	clazz = classinfo;
	ifaces = classinfos;
    }
    
    static ClassInterfacesType create(ClassInfo classinfo,
				      ClassInfo[] classinfos) {
	if (classinfos.length == 0 && classinfo == null)
	    return Type.tObject;
	if (classinfos.length == 0)
	    return Type.tClass(classinfo);
	if (classinfos.length == 1 && classinfo == null)
	    return Type.tClass(classinfos[0]);
	return new ClassInterfacesType(classinfo, classinfos);
    }
    
    public Type getSubType() {
	if (clazz == null && ifaces.length == 1 || ifaces.length == 0)
	    return Type.tRange(this, Type.tNull);
	throw new AssertError
		  ("getSubType called on set of classes and interfaces!");
    }
    
    public Type getHint() {
	if (ifaces.length == 0 || clazz == null && ifaces.length == 1)
	    return this;
	if (clazz != null)
	    return Type.tClass(clazz.getName());
	return Type.tClass(ifaces[0].getName());
    }
    
    public Type getCanonic() {
	if (ifaces.length == 0 || clazz == null && ifaces.length == 1)
	    return this;
	if (clazz != null)
	    return Type.tClass(clazz.getName());
	return Type.tClass(ifaces[0].getName());
    }
    
    public Type createRangeType(ReferenceType referencetype) {
	if (referencetype.typecode != 10)
	    return Type.tError;
	ClassInterfacesType classinterfacestype_0_
	    = (ClassInterfacesType) referencetype;
	if (referencetype == Type.tObject)
	    return (this == Type.tObject ? (Type) Type.tObject
		    : Type.tRange(Type.tObject, this));
	if (classinterfacestype_0_.clazz != null) {
	    if (!classinterfacestype_0_.clazz.superClassOf(clazz))
		return Type.tError;
	    for (int i = 0; i < classinterfacestype_0_.ifaces.length; i++) {
		if (!classinterfacestype_0_.ifaces[i].implementedBy(clazz))
		    return Type.tError;
	    }
	    if (classinterfacestype_0_.clazz == clazz
		&& classinterfacestype_0_.ifaces.length == 0)
		return classinterfacestype_0_;
	    if (ifaces.length != 0)
		return Type.tRange(classinterfacestype_0_,
				   create(clazz, new ClassInfo[0]));
	    return Type.tRange(classinterfacestype_0_, this);
	}
	ClassInfo classinfo = clazz;
	if (classinfo != null) {
	    for (int i = 0; i < classinterfacestype_0_.ifaces.length; i++) {
		if (!classinterfacestype_0_.ifaces[i]
			 .implementedBy(classinfo)) {
		    classinfo = null;
		    break;
		}
	    }
	}
	if (classinfo == null && classinterfacestype_0_.ifaces.length == 1) {
	    for (int i = 0; i < ifaces.length; i++) {
		if (ifaces[i] == classinterfacestype_0_.ifaces[0])
		    return classinterfacestype_0_;
	    }
	}
	ClassInfo[] classinfos = new ClassInfo[ifaces.length];
	int i = 0;
    while_5_:
	for (int i_1_ = 0; i_1_ < ifaces.length; i_1_++) {
	    for (int i_2_ = 0; i_2_ < classinterfacestype_0_.ifaces.length;
		 i_2_++) {
		if (!classinterfacestype_0_.ifaces[i_2_]
			 .implementedBy(ifaces[i_1_]))
		    continue while_5_;
	    }
	    classinfos[i++] = ifaces[i_1_];
	}
	if (classinfo == null && i == 0)
	    return Type.tError;
	if (i < classinfos.length) {
	    ClassInfo[] classinfos_3_ = new ClassInfo[i];
	    System.arraycopy(classinfos, 0, classinfos_3_, 0, i);
	    classinfos = classinfos_3_;
	} else if (classinfo == clazz)
	    return Type.tRange(classinterfacestype_0_, this);
	return Type.tRange(classinterfacestype_0_,
			   create(classinfo, classinfos));
    }
    
    public Type getSpecializedType(Type type) {
	int i = type.typecode;
	if (i == 103) {
	    type = ((RangeType) type).getBottom();
	    i = type.typecode;
	}
	if (i == 8)
	    return this;
	if (i == 9)
	    return ((ArrayType) type).getSpecializedType(this);
	if (i != 10)
	    return Type.tError;
	ClassInterfacesType classinterfacestype_4_
	    = (ClassInterfacesType) type;
	ClassInfo classinfo;
	if (clazz == null)
	    classinfo = classinterfacestype_4_.clazz;
	else if (classinterfacestype_4_.clazz == null)
	    classinfo = clazz;
	else if (clazz.superClassOf(classinterfacestype_4_.clazz))
	    classinfo = classinterfacestype_4_.clazz;
	else if (classinterfacestype_4_.clazz.superClassOf(clazz))
	    classinfo = clazz;
	else
	    return Type.tError;
	if (classinfo == clazz
	    && ReferenceType.implementsAllIfaces(clazz, ifaces,
						 (classinterfacestype_4_
						  .ifaces)))
	    return this;
	if (classinfo == classinterfacestype_4_.clazz
	    && ReferenceType.implementsAllIfaces(classinterfacestype_4_.clazz,
						 classinterfacestype_4_.ifaces,
						 ifaces))
	    return classinterfacestype_4_;
	Vector vector = new Vector();
    while_7_:
	for (int i_5_ = 0; i_5_ < ifaces.length; i_5_++) {
	    ClassInfo classinfo_6_ = ifaces[i_5_];
	    if (classinfo == null || !classinfo_6_.implementedBy(classinfo)) {
		for (int i_7_ = 0; i_7_ < classinterfacestype_4_.ifaces.length;
		     i_7_++) {
		    if (classinfo_6_.implementedBy(classinterfacestype_4_
						   .ifaces[i_7_]))
			continue while_7_;
		}
		vector.addElement(classinfo_6_);
	    }
	}
    while_9_:
	for (int i_8_ = 0; i_8_ < classinterfacestype_4_.ifaces.length;
	     i_8_++) {
	    ClassInfo classinfo_9_ = classinterfacestype_4_.ifaces[i_8_];
	    if (classinfo == null || !classinfo_9_.implementedBy(classinfo)) {
		for (int i_10_ = 0; i_10_ < vector.size(); i_10_++) {
		    if (classinfo_9_.implementedBy((ClassInfo)
						   vector.elementAt(i_10_)))
			continue while_9_;
		}
		vector.addElement(classinfo_9_);
	    }
	}
	ClassInfo[] classinfos = new ClassInfo[vector.size()];
	vector.copyInto(classinfos);
	return create(classinfo, classinfos);
    }
    
    public Type getGeneralizedType(Type type) {
	int i = type.typecode;
	if (i == 103) {
	    type = ((RangeType) type).getTop();
	    i = type.typecode;
	}
	if (i == 8)
	    return this;
	if (i == 9)
	    return ((ArrayType) type).getGeneralizedType(this);
	if (i != 10)
	    return Type.tError;
	ClassInterfacesType classinterfacestype_11_
	    = (ClassInterfacesType) type;
	ClassInfo classinfo;
	if (clazz == null || classinterfacestype_11_.clazz == null)
	    classinfo = null;
	else {
	    for (classinfo = clazz; classinfo != null;
		 classinfo = classinfo.getSuperclass()) {
		if (classinfo.superClassOf(classinterfacestype_11_.clazz))
		    break;
	    }
	    if (classinfo == ClassInfo.javaLangObject)
		classinfo = null;
	}
	if (classinfo == clazz
	    && ReferenceType.implementsAllIfaces(classinterfacestype_11_.clazz,
						 (classinterfacestype_11_
						  .ifaces),
						 ifaces))
	    return this;
	if (classinfo == classinterfacestype_11_.clazz
	    && ReferenceType.implementsAllIfaces(clazz, ifaces,
						 (classinterfacestype_11_
						  .ifaces)))
	    return classinterfacestype_11_;
	Stack stack = new Stack();
	if (clazz != null) {
	    for (ClassInfo classinfo_12_ = clazz; classinfo != classinfo_12_;
		 classinfo_12_ = classinfo_12_.getSuperclass()) {
		ClassInfo[] classinfos = classinfo_12_.getInterfaces();
		for (int i_13_ = 0; i_13_ < classinfos.length; i_13_++)
		    stack.push(classinfos[i_13_]);
	    }
	}
	Vector vector = new Vector();
	for (int i_14_ = 0; i_14_ < ifaces.length; i_14_++)
	    stack.push(ifaces[i_14_]);
    while_10_:
	while (!stack.isEmpty()) {
	    ClassInfo classinfo_15_ = (ClassInfo) stack.pop();
	    if ((classinfo == null || !classinfo_15_.implementedBy(classinfo))
		&& !vector.contains(classinfo_15_)) {
		if (classinterfacestype_11_.clazz != null
		    && classinfo_15_
			   .implementedBy(classinterfacestype_11_.clazz))
		    vector.addElement(classinfo_15_);
		else {
		    for (int i_16_ = 0;
			 i_16_ < classinterfacestype_11_.ifaces.length;
			 i_16_++) {
			if (classinfo_15_.implementedBy(classinterfacestype_11_
							.ifaces[i_16_])) {
			    vector.addElement(classinfo_15_);
			    continue while_10_;
			}
		    }
		    ClassInfo[] classinfos = classinfo_15_.getInterfaces();
		    for (int i_17_ = 0; i_17_ < classinfos.length; i_17_++)
			stack.push(classinfos[i_17_]);
		}
	    }
	}
	ClassInfo[] classinfos = new ClassInfo[vector.size()];
	vector.copyInto(classinfos);
	return create(classinfo, classinfos);
    }
    
    public String getTypeSignature() {
	if (clazz != null)
	    return "L" + clazz.getName().replace('.', '/') + ";";
	if (ifaces.length > 0)
	    return "L" + ifaces[0].getName().replace('.', '/') + ";";
	return "Ljava/lang/Object;";
    }
    
    public Class getTypeClass() throws ClassNotFoundException {
	if (clazz != null)
	    return Class.forName(clazz.getName());
	if (ifaces.length > 0)
	    return Class.forName(ifaces[0].getName());
	return Class.forName("java.lang.Object");
    }
    
    public ClassInfo getClassInfo() {
	if (clazz != null)
	    return clazz;
	if (ifaces.length > 0)
	    return ifaces[0];
	return ClassInfo.javaLangObject;
    }
    
    public String toString() {
	if (this == Type.tObject)
	    return "java.lang.Object";
	if (ifaces.length == 0)
	    return clazz.getName();
	if (clazz == null && ifaces.length == 1)
	    return ifaces[0].getName();
	StringBuffer stringbuffer = new StringBuffer("{");
	String string = "";
	if (clazz != null) {
	    stringbuffer = stringbuffer.append(clazz.getName());
	    string = ", ";
	}
	for (int i = 0; i < ifaces.length; i++) {
	    stringbuffer.append(string).append(ifaces[i].getName());
	    string = ", ";
	}
	return stringbuffer.append("}").toString();
    }
    
    public Type getCastHelper(Type type) {
	Type type_18_ = type.getHint();
	switch (type_18_.getTypeCode()) {
	case 9:
	    if (clazz == null
		&& ReferenceType.implementsAllIfaces(null,
						     ArrayType.arrayIfaces,
						     ifaces))
		return null;
	    return Type.tObject;
	case 10: {
	    ClassInterfacesType classinterfacestype_19_
		= (ClassInterfacesType) type_18_;
	    if (classinterfacestype_19_.clazz == null || clazz == null
		|| clazz.superClassOf(classinterfacestype_19_.clazz)
		|| classinterfacestype_19_.clazz.superClassOf(clazz))
		return null;
	    ClassInfo classinfo;
	    for (classinfo = clazz.getSuperclass();
		 (classinfo != null
		  && !classinfo.superClassOf(classinterfacestype_19_.clazz));
		 classinfo = classinfo.getSuperclass()) {
		/* empty */
	    }
	    return Type.tClass(classinfo.getName());
	}
	case 101:
	    return null;
	default:
	    return Type.tObject;
	}
    }
    
    public boolean isValidType() {
	return ifaces.length == 0 || clazz == null && ifaces.length == 1;
    }
    
    public boolean isClassType() {
	return true;
    }
    
    public String getDefaultName() {
	ClassInfo classinfo;
	if (clazz != null)
	    classinfo = clazz;
	else if (ifaces.length > 0)
	    classinfo = ifaces[0];
	else
	    classinfo = ClassInfo.javaLangObject;
	String string = classinfo.getName();
	int i = Math.max(string.lastIndexOf('.'), string.lastIndexOf('$'));
	if (i >= 0)
	    string = string.substring(i + 1);
	if (Character.isUpperCase(string.charAt(0))) {
	    string = string.toLowerCase();
	    if (keywords.get(string) != null){
			return "var_" + string;
	    }
		if(string.equals("object")){
			return "obj";
		}
	    return string;
	}
	return "var_" + string;
    }
    
    public int hashCode() {
	int i = clazz == null ? 0 : clazz.hashCode();
	for (int i_20_ = 0; i_20_ < ifaces.length; i_20_++)
	    i ^= ifaces[i_20_].hashCode();
	return i;
    }
    
    public boolean equals(Object object) {
	if (object == this)
	    return true;
	if (object instanceof Type && ((Type) object).typecode == 10) {
	    ClassInterfacesType classinterfacestype_21_
		= (ClassInterfacesType) object;
	    if (classinterfacestype_21_.clazz == clazz
		&& classinterfacestype_21_.ifaces.length == ifaces.length) {
	    while_12_:
		for (int i = 0; i < classinterfacestype_21_.ifaces.length;
		     i++) {
		    for (int i_22_ = 0; i_22_ < ifaces.length; i_22_++) {
			if (classinterfacestype_21_.ifaces[i] == ifaces[i_22_])
			    continue while_12_;
		    }
		    return false;
		}
		return true;
	    }
	}
	return false;
    }
    
    static {
	keywords.put("abstract", Boolean.TRUE);
	keywords.put("default", Boolean.TRUE);
	keywords.put("if", Boolean.TRUE);
	keywords.put("private", Boolean.TRUE);
	keywords.put("throw", Boolean.TRUE);
	keywords.put("boolean", Boolean.TRUE);
	keywords.put("do", Boolean.TRUE);
	keywords.put("implements", Boolean.TRUE);
	keywords.put("protected", Boolean.TRUE);
	keywords.put("throws", Boolean.TRUE);
	keywords.put("break", Boolean.TRUE);
	keywords.put("double", Boolean.TRUE);
	keywords.put("import", Boolean.TRUE);
	keywords.put("public", Boolean.TRUE);
	keywords.put("transient", Boolean.TRUE);
	keywords.put("byte", Boolean.TRUE);
	keywords.put("else", Boolean.TRUE);
	keywords.put("instanceof", Boolean.TRUE);
	keywords.put("return", Boolean.TRUE);
	keywords.put("try", Boolean.TRUE);
	keywords.put("case", Boolean.TRUE);
	keywords.put("extends", Boolean.TRUE);
	keywords.put("int", Boolean.TRUE);
	keywords.put("short", Boolean.TRUE);
	keywords.put("void", Boolean.TRUE);
	keywords.put("catch", Boolean.TRUE);
	keywords.put("final", Boolean.TRUE);
	keywords.put("interface", Boolean.TRUE);
	keywords.put("static", Boolean.TRUE);
	keywords.put("volatile", Boolean.TRUE);
	keywords.put("char", Boolean.TRUE);
	keywords.put("finally", Boolean.TRUE);
	keywords.put("long", Boolean.TRUE);
	keywords.put("super", Boolean.TRUE);
	keywords.put("while", Boolean.TRUE);
	keywords.put("class", Boolean.TRUE);
	keywords.put("float", Boolean.TRUE);
	keywords.put("native", Boolean.TRUE);
	keywords.put("switch", Boolean.TRUE);
	keywords.put("const", Boolean.TRUE);
	keywords.put("for", Boolean.TRUE);
	keywords.put("new", Boolean.TRUE);
	keywords.put("synchronized", Boolean.TRUE);
	keywords.put("continue", Boolean.TRUE);
	keywords.put("goto", Boolean.TRUE);
	keywords.put("package", Boolean.TRUE);
	keywords.put("this", Boolean.TRUE);
	keywords.put("strictfp", Boolean.TRUE);
	keywords.put("null", Boolean.TRUE);
	keywords.put("true", Boolean.TRUE);
	keywords.put("false", Boolean.TRUE);
    }
}
