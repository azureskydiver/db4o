/* Reference - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.bytecode;
import java.util.Iterator;

import jode.util.UnifyHash;

public class Reference
{
    private final String clazz;
    private final String name;
    private final String type;
    private static final UnifyHash unifier = new UnifyHash();
    
    public static Reference getReference(String string, String string_0_,
					 String string_1_) {
	int i
	    = string.hashCode() ^ string_0_.hashCode() ^ string_1_.hashCode();
	Iterator iterator = unifier.iterateHashCode(i);
	while (iterator.hasNext()) {
	    Reference reference = (Reference) iterator.next();
	    if (reference.clazz.equals(string)
		&& reference.name.equals(string_0_)
		&& reference.type.equals(string_1_))
		return reference;
	}
	Reference reference = new Reference(string, string_0_, string_1_);
	unifier.put(i, reference);
	return reference;
    }
    
    private Reference(String string, String string_2_, String string_3_) {
	clazz = string;
	name = string_2_;
	if(name.equals("this$0")){
		int xxx = 1;
	}
	type = string_3_;
    }
    
    public String getClazz() {
	return clazz;
    }
    
    public String getName() {
	return name;
    }
    
    public String getType() {
	return type;
    }
    
    public String toString() {
	return clazz + " " + name + " " + type;
    }
}
