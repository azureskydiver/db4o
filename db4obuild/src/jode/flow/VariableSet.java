/* VariableSet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

import jode.AssertError;
import jode.decompiler.LocalInfo;

public final class VariableSet extends AbstractSet implements Cloneable
{
    LocalInfo[] locals;
    int count;
    
    public VariableSet() {
	locals = null;
	count = 0;
    }
    
    public VariableSet(LocalInfo[] localinfos) {
	count = localinfos.length;
	locals = localinfos;
    }
    
    public final void grow(int i) {
	if (locals != null) {
	    i += count;
	    if (i > locals.length) {
		int i_0_ = locals.length * 2;
		LocalInfo[] localinfos = new LocalInfo[i_0_ > i ? i_0_ : i];
		System.arraycopy(locals, 0, localinfos, 0, count);
		locals = localinfos;
	    }
	} else if (i > 0)
	    locals = new LocalInfo[i];
    }
    
    public boolean add(Object object) {
	if (contains(object))
	    return false;
	grow(1);
	locals[count++] = (LocalInfo) object;
	return true;
    }
    
    public boolean contains(Object object) {
	LocalInfo localinfo = ((LocalInfo) object).getLocalInfo();
	for (int i = 0; i < count; i++) {
	    if (locals[i].getLocalInfo() == localinfo)
		return true;
	}
	return false;
    }
    
    public final boolean containsSlot(int i) {
	return findSlot(i) != null;
    }
    
    public LocalInfo findLocal(String string) {
	for (int i = 0; i < count; i++) {
	    if (locals[i].getName().equals(string))
		return locals[i];
	}
	return null;
    }
    
    public LocalInfo findSlot(int i) {
	for (int i_1_ = 0; i_1_ < count; i_1_++) {
	    if (locals[i_1_].getSlot() == i)
		return locals[i_1_];
	}
	return null;
    }
    
    public boolean remove(Object object) {
	LocalInfo localinfo = ((LocalInfo) object).getLocalInfo();
	for (int i = 0; i < count; i++) {
	    if (locals[i].getLocalInfo() == localinfo) {
		locals[i] = locals[--count];
		locals[count] = null;
		return true;
	    }
	}
	return false;
    }
    
    public int size() {
	return count;
    }
    
    public Iterator iterator() {
	return new Iterator() {
	    int pos = 0;
	    
	    public boolean hasNext() {
		return pos < count;
	    }
	    
	    public Object next() {
		return locals[pos++];
	    }
	    
	    public void remove() {
		if (pos < count)
		    System.arraycopy(locals, pos, locals, pos - 1,
				     count - pos);
		count--;
		pos--;
		locals[count] = null;
	    }
	};
    }
    
    public void clear() {
	locals = null;
	count = 0;
    }
    
    public Object clone() {
	try {
	    VariableSet variableset_3_ = (VariableSet) super.clone();
	    if (count > 0) {
		variableset_3_.locals = new LocalInfo[count];
		System.arraycopy(locals, 0, variableset_3_.locals, 0, count);
	    }
	    return variableset_3_;
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new AssertError("Clone?");
	}
    }
    
    public VariableSet intersect(VariableSet variableset_4_) {
	VariableSet variableset_5_ = new VariableSet();
	variableset_5_.grow(Math.min(count, variableset_4_.count));
	for (int i = 0; i < count; i++) {
	    LocalInfo localinfo = locals[i];
	    int i_6_ = localinfo.getSlot();
	    if (variableset_4_.containsSlot(i_6_)
		&& !variableset_5_.containsSlot(i_6_))
		variableset_5_.locals[variableset_5_.count++]
		    = localinfo.getLocalInfo();
	}
	return variableset_5_;
    }
    
    public void mergeGenKill(Collection collection, SlotSet slotset) {
	grow(collection.size());
	Iterator iterator = collection.iterator();
	while (iterator.hasNext()) {
	    LocalInfo localinfo = (LocalInfo) iterator.next();
	    if (!slotset.containsSlot(localinfo.getSlot()))
		add(localinfo.getLocalInfo());
	}
    }
}
