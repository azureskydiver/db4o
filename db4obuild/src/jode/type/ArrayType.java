/* ArrayType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.type;
import java.util.Vector;

import jode.bytecode.ClassInfo;

public class ArrayType extends ReferenceType
{
    static final ClassInfo[] arrayIfaces
	= { ClassInfo.forName("java.lang.Cloneable"),
	    ClassInfo.forName("java.io.Serializable") };
    Type elementType;
    
    ArrayType(Type type) {
	super(9);
	elementType = type;
    }
    
    public Type getElementType() {
	return elementType;
    }
    
    public Type getSuperType() {
	if (elementType instanceof IntegerType)
	    return Type.tRange(Type.tObject, this);
	return Type.tRange(Type.tObject,
			   ((ReferenceType)
			    Type.tArray(elementType.getSuperType())));
    }
    
    public Type getSubType() {
	if (elementType instanceof IntegerType)
	    return this;
	return Type.tArray(elementType.getSubType());
    }
    
    public Type getHint() {
	return Type.tArray(elementType.getHint());
    }
    
    public Type getCanonic() {
	return Type.tArray(elementType.getCanonic());
    }
    
    public Type createRangeType(ReferenceType referencetype) {
	if (referencetype.getTypeCode() == 9)
	    return Type.tArray(elementType.intersection(((ArrayType)
							 referencetype)
							.elementType));
	if (referencetype.getTypeCode() == 10) {
	    ClassInterfacesType classinterfacestype
		= (ClassInterfacesType) referencetype;
	    if (classinterfacestype.clazz == null
		&& ReferenceType.implementsAllIfaces(null, arrayIfaces,
						     (classinterfacestype
						      .ifaces)))
		return Type.tRange(classinterfacestype, this);
	}
	return Type.tError;
    }
    
    public Type getSpecializedType(Type type) {
	if (type.getTypeCode() == 103)
	    type = ((RangeType) type).getBottom();
	if (type == Type.tNull)
	    return this;
	if (type.getTypeCode() == 9) {
	    Type type_0_
		= elementType.intersection(((ArrayType) type).elementType);
	    return type_0_ != Type.tError ? Type.tArray(type_0_) : Type.tError;
	}
	if (type.getTypeCode() == 10) {
	    ClassInterfacesType classinterfacestype
		= (ClassInterfacesType) type;
	    if (classinterfacestype.clazz == null
		&& ReferenceType.implementsAllIfaces(null, arrayIfaces,
						     (classinterfacestype
						      .ifaces)))
		return this;
	}
	return Type.tError;
    }
    
    public Type getGeneralizedType(Type type) {
	if (type.getTypeCode() == 103)
	    type = ((RangeType) type).getTop();
	if (type == Type.tNull)
	    return this;
	if (type.getTypeCode() == 9) {
	    Type type_1_
		= elementType.intersection(((ArrayType) type).elementType);
	    if (type_1_ != Type.tError)
		return Type.tArray(type_1_);
	    return ClassInterfacesType.create(null, arrayIfaces);
	}
	if (type.getTypeCode() == 10) {
	    ClassInterfacesType classinterfacestype
		= (ClassInterfacesType) type;
	    if (ReferenceType.implementsAllIfaces(classinterfacestype.clazz,
						  classinterfacestype.ifaces,
						  arrayIfaces))
		return ClassInterfacesType.create(null, arrayIfaces);
	    if (classinterfacestype.clazz == null
		&& ReferenceType.implementsAllIfaces(null, arrayIfaces,
						     (classinterfacestype
						      .ifaces)))
		return classinterfacestype;
	    Vector vector = new Vector();
	    for (int i = 0; i < arrayIfaces.length; i++) {
		if (classinterfacestype.clazz != null
		    && arrayIfaces[i].implementedBy(classinterfacestype.clazz))
		    vector.addElement(arrayIfaces[i]);
		else {
		    for (int i_2_ = 0;
			 i_2_ < classinterfacestype.ifaces.length; i_2_++) {
			if (arrayIfaces[i].implementedBy(classinterfacestype
							 .ifaces[i_2_])) {
			    vector.addElement(arrayIfaces[i]);
			    break;
			}
		    }
		}
	    }
	    ClassInfo[] classinfos = new ClassInfo[vector.size()];
	    vector.copyInto(classinfos);
	    return ClassInterfacesType.create(null, classinfos);
	}
	return Type.tError;
    }
    
    public Type getCastHelper(Type type) {
	Type type_3_ = type.getHint();
	switch (type_3_.getTypeCode()) {
	case 9: {
	    if (!elementType.isClassType()
		|| !((ArrayType) type_3_).elementType.isClassType())
		return Type.tObject;
	    Type type_4_
		= elementType.getCastHelper(((ArrayType) type_3_).elementType);
	    if (type_4_ != null)
		return Type.tArray(type_4_);
	    return null;
	}
	case 10: {
	    ClassInterfacesType classinterfacestype
		= (ClassInterfacesType) type_3_;
	    if (classinterfacestype.clazz == null
		&& ReferenceType.implementsAllIfaces(null, arrayIfaces,
						     (classinterfacestype
						      .ifaces)))
		return null;
	    return Type.tObject;
	}
	case 101:
	    return null;
	default:
	    return Type.tObject;
	}
    }
    
    public boolean isValidType() {
	return elementType.isValidType();
    }
    
    public boolean isClassType() {
	return true;
    }
    
    public String getTypeSignature() {
	return "[" + elementType.getTypeSignature();
    }
    
    public Class getTypeClass() throws ClassNotFoundException {
	return Class.forName("[" + elementType.getTypeSignature());
    }
    
    public String toString() {
	return elementType.toString() + "[]";
    }
    
    private static String pluralize(String string) {
	return string + ((string.endsWith("s") || string.endsWith("x")
			  || string.endsWith("sh") || string.endsWith("ch"))
			 ? "es" : "s");
    }
    
    public String getDefaultName() {
	if (elementType instanceof ArrayType)
	    return elementType.getDefaultName();
	return pluralize(elementType.getDefaultName());
    }
    
    public boolean equals(Object object) {
	if (object == this)
	    return true;
	if (object instanceof ArrayType) {
	    ArrayType arraytype_5_ = (ArrayType) object;
	    return arraytype_5_.elementType.equals(elementType);
	}
	return false;
    }
}
