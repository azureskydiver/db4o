/* SlotSet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.flow;
import java.util.AbstractSet;
import java.util.Iterator;

import jode.AssertError;
import jode.decompiler.LocalInfo;

public final class SlotSet extends AbstractSet implements Cloneable
{
    LocalInfo[] locals;
    int count;
    
    public SlotSet() {
	locals = null;
	count = 0;
    }
    
    public SlotSet(LocalInfo[] localinfos) {
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
	LocalInfo localinfo = (LocalInfo) object;
	LocalInfo localinfo_1_ = findSlot(localinfo.getSlot());
	if (localinfo_1_ != null) {
	    localinfo.combineWith(localinfo_1_);
	    return false;
	}
	grow(1);
	locals[count++] = localinfo;
	return true;
    }
    
    public final boolean contains(Object object) {
	return containsSlot(((LocalInfo) object).getSlot());
    }
    
    public final boolean containsSlot(int i) {
	return findSlot(i) != null;
    }
    
    public LocalInfo findSlot(int i) {
	for (int i_2_ = 0; i_2_ < count; i_2_++) {
	    if (locals[i_2_].getSlot() == i)
		return locals[i_2_];
	}
	return null;
    }
    
    public boolean remove(Object object) {
	int i = ((LocalInfo) object).getSlot();
	for (int i_3_ = 0; i_3_ < count; i_3_++) {
	    if (locals[i_3_].getSlot() == i) {
		locals[i_3_] = locals[--count];
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
	    }
	};
    }
    
    public void clear() {
	locals = null;
	count = 0;
    }
    
    public Object clone() {
	try {
	    SlotSet slotset_5_ = (SlotSet) super.clone();
	    if (count > 0) {
		slotset_5_.locals = new LocalInfo[count];
		System.arraycopy(locals, 0, slotset_5_.locals, 0, count);
	    }
	    return slotset_5_;
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new AssertError("Clone?");
	}
    }
    
    public void merge(VariableSet variableset) {
	for (int i = 0; i < count; i++) {
	    LocalInfo localinfo = locals[i];
	    int i_6_ = localinfo.getSlot();
	    for (int i_7_ = 0; i_7_ < variableset.count; i_7_++) {
		if (localinfo.getSlot() == variableset.locals[i_7_].getSlot())
		    localinfo.combineWith(variableset.locals[i_7_]);
	    }
	}
    }
    
    public void mergeKill(SlotSet slotset_8_) {
	grow(slotset_8_.size());
	Iterator iterator = slotset_8_.iterator();
	while (iterator.hasNext()) {
	    LocalInfo localinfo = (LocalInfo) iterator.next();
	    if (!containsSlot(localinfo.getSlot()))
		add(localinfo.getLocalInfo());
	}
    }
}
