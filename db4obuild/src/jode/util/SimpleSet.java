/* SimpleSet - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.util;
import java.util.AbstractSet;
import java.util.Iterator;

import jode.AssertError;

public class SimpleSet extends AbstractSet implements Cloneable
{
    Object[] elementObjects;
    int count = 0;
    
    public SimpleSet() {
	this(2);
    }
    
    public SimpleSet(int i) {
	elementObjects = new Object[i];
    }
    
    public int size() {
	return count;
    }
    
    public boolean add(Object object) {
	if (object == null)
	    throw new NullPointerException();
	for (int i = 0; i < count; i++) {
	    if (object.equals(elementObjects[i]))
		return false;
	}
	if (count == elementObjects.length) {
	    Object[] objects = new Object[(count + 1) * 3 / 2];
	    System.arraycopy(elementObjects, 0, objects, 0, count);
	    elementObjects = objects;
	}
	elementObjects[count++] = object;
	return true;
    }
    
    public Object clone() {
	try {
	    SimpleSet simpleset_0_ = (SimpleSet) super.clone();
	    simpleset_0_.elementObjects = (Object[]) elementObjects.clone();
	    return simpleset_0_;
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new AssertError("Clone?");
	}
    }
    
    public Iterator iterator() {
	return new Iterator() {
	    int pos = 0;
	    
	    public boolean hasNext() {
		return pos < count;
	    }
	    
	    public Object next() {
		return elementObjects[pos++];
	    }
	    
	    public void remove() {
		if (pos < count)
		    System.arraycopy(elementObjects, pos, elementObjects,
				     pos - 1, count - pos);
		count--;
		pos--;
	    }
	};
    }
}
