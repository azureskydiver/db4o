/* ReferenceType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.type;
import jode.GlobalOptions;
import jode.bytecode.ClassInfo;

public abstract class ReferenceType extends Type
{
    public ReferenceType(int i) {
	super(i);
    }
    
    public abstract Type getSpecializedType(Type type);
    
    public abstract Type getGeneralizedType(Type type);
    
    public abstract Type createRangeType(ReferenceType referencetype_0_);
    
    protected static boolean implementsAllIfaces(ClassInfo classinfo,
						 ClassInfo[] classinfos,
						 ClassInfo[] classinfos_1_) {
    while_3_:
	for (int i = 0; i < classinfos_1_.length; i++) {
	    ClassInfo classinfo_2_ = classinfos_1_[i];
	    if (classinfo == null || !classinfo_2_.implementedBy(classinfo)) {
		for (int i_3_ = 0; i_3_ < classinfos.length; i_3_++) {
		    if (classinfo_2_.implementedBy(classinfos[i_3_]))
			continue while_3_;
		}
		return false;
	    }
	}
	return true;
    }
    
    public Type getSuperType() {
	return (this == Type.tObject ? (Type) Type.tObject
		: Type.tRange(Type.tObject, this));
    }
    
    public abstract Type getSubType();
    
    public Type intersection(Type type) {
	if (type == Type.tError)
	    return type;
	if (type == Type.tUnknown)
	    return this;
	Type type_4_ = getSpecializedType(type);
	Type type_5_ = getGeneralizedType(type);
	Type type_6_;
	if (type_5_.equals(type_4_))
	    type_6_ = type_5_;
	else if (type_5_ instanceof ReferenceType
		 && type_4_ instanceof ReferenceType)
	    type_6_ = ((ReferenceType) type_5_)
			  .createRangeType((ReferenceType) type_4_);
	else
	    type_6_ = Type.tError;
	if ((GlobalOptions.debuggingFlags & 0x4) != 0)
	    GlobalOptions.err.println("intersecting " + this + " and " + type
				      + " to " + type_6_);
	return type_6_;
    }
}
