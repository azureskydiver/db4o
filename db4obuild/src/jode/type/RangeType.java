/* RangeType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.type;
import jode.AssertError;
import jode.GlobalOptions;

public class RangeType extends Type
{
    final ReferenceType bottomType;
    final ReferenceType topType;
    
    public RangeType(ReferenceType referencetype,
		     ReferenceType referencetype_0_) {
	super(103);
	if (referencetype == Type.tNull)
	    throw new AssertError("bottom is NULL");
	bottomType = referencetype;
	topType = referencetype_0_;
    }
    
    public ReferenceType getBottom() {
	return bottomType;
    }
    
    public ReferenceType getTop() {
	return topType;
    }
    
    public Type getHint() {
	Type type = bottomType.getHint();
	Type type_1_ = topType.getHint();
	if (topType == Type.tNull && bottomType.equals(type))
	    return type;
	return type_1_;
    }
    
    public Type getCanonic() {
	return topType.getCanonic();
    }
    
    public Type getSuperType() {
	return topType.getSuperType();
    }
    
    public Type getSubType() {
	return Type.tRange(bottomType, Type.tNull);
    }
    
    public Type getCastHelper(Type type) {
	return topType.getCastHelper(type);
    }
    
    public String getTypeSignature() {
	if (topType.isClassType() || !bottomType.isValidType())
	    return topType.getTypeSignature();
	return bottomType.getTypeSignature();
    }
    
    public Class getTypeClass() throws ClassNotFoundException {
	if (topType.isClassType() || !bottomType.isValidType())
	    return topType.getTypeClass();
	return bottomType.getTypeClass();
    }
    
    public String toString() {
	return "<" + bottomType + "-" + topType + ">";
    }
    
    public String getDefaultName() {
	throw new AssertError("getDefaultName() called on range");
    }
    
    public int hashCode() {
	int i = topType.hashCode();
	return (i << 16 | i >>> 16) ^ bottomType.hashCode();
    }
    
    public boolean equals(Object object) {
	if (object instanceof RangeType) {
	    RangeType rangetype_2_ = (RangeType) object;
	    return (topType.equals(rangetype_2_.topType)
		    && bottomType.equals(rangetype_2_.bottomType));
	}
	return false;
    }
    
    public Type intersection(Type type) {
	if (type == Type.tError)
	    return type;
	if (type == Type.tUnknown)
	    return this;
	Type type_3_ = bottomType.getSpecializedType(type);
	Type type_4_ = topType.getGeneralizedType(type);
	Type type_5_;
	if (type_4_.equals(type_3_))
	    type_5_ = type_4_;
	else if (type_4_ instanceof ReferenceType
		 && type_3_ instanceof ReferenceType)
	    type_5_ = ((ReferenceType) type_4_)
			  .createRangeType((ReferenceType) type_3_);
	else
	    type_5_ = Type.tError;
	if ((GlobalOptions.debuggingFlags & 0x4) != 0)
	    GlobalOptions.err.println("intersecting " + this + " and " + type
				      + " to " + type_5_);
	return type_5_;
    }
}
