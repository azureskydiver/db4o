/* ArrayEnum - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.util;
import java.util.Enumeration;

public class ArrayEnum implements Enumeration
{
    int index = 0;
    int size;
    Object[] array;
    
    public ArrayEnum(int i, Object[] objects) {
	size = i;
	array = objects;
    }
    
    public boolean hasMoreElements() {
	return index < size;
    }
    
    public Object nextElement() {
	return array[index++];
    }
}
