/* NullType - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.type;

public class NullType extends ReferenceType
{
    public NullType() {
	super(8);
    }
    
    public Type getSubType() {
	return this;
    }
    
    public Type createRangeType(ReferenceType referencetype) {
	return Type.tRange(referencetype, this);
    }
    
    public Type getGeneralizedType(Type type) {
	if (type.typecode == 103)
	    type = ((RangeType) type).getTop();
	return type;
    }
    
    public Type getSpecializedType(Type type) {
	if (type.typecode == 103)
	    type = ((RangeType) type).getBottom();
	return type;
    }
    
    public String toString() {
	return "tNull";
    }
}
