/* Value - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.jvm;

class Value
{
    Object value;
    NewObject newObj;
    
    public Value() {
	/* empty */
    }
    
    public void setObject(Object object) {
	newObj = null;
	this.value = object;
    }
    
    public Object objectValue() {
	if (newObj != null)
	    return newObj.objectValue();
	return this.value;
    }
    
    public void setInt(int i) {
	newObj = null;
	this.value = new Integer(i);
    }
    
    public int intValue() {
	return ((Integer) this.value).intValue();
    }
    
    public void setLong(long l) {
	newObj = null;
	this.value = new Long(l);
    }
    
    public long longValue() {
	return ((Long) this.value).longValue();
    }
    
    public void setFloat(float f) {
	newObj = null;
	this.value = new Float(f);
    }
    
    public float floatValue() {
	return ((Float) this.value).floatValue();
    }
    
    public void setDouble(double d) {
	newObj = null;
	this.value = new Double(d);
    }
    
    public double doubleValue() {
	return ((Double) this.value).doubleValue();
    }
    
    public void setNewObject(NewObject newobject) {
	newObj = newobject;
    }
    
    public NewObject getNewObject() {
	return newObj;
    }
    
    public void setValue(Value value_0_) {
	this.value = value_0_.value;
	newObj = value_0_.newObj;
    }
    
    public String toString() {
	return newObj != null ? newObj.toString() : "" + this.value;
    }
}
