/* FieldIdentifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import jode.bytecode.FieldInfo;

public class FieldIdentifier extends Identifier
{
    FieldInfo info;
    ClassIdentifier clazz;
    String name;
    String type;
    private boolean notConstant;
    private Object constant;
    private Collection fieldListeners;
    
    public FieldIdentifier(ClassIdentifier classidentifier,
			   FieldInfo fieldinfo) {
	super(fieldinfo.getName());
	name = fieldinfo.getName();
	type = fieldinfo.getType();
	info = fieldinfo;
	clazz = classidentifier;
	constant = fieldinfo.getConstant();
    }
    
    public void setSingleReachable() {
	super.setSingleReachable();
	Main.getClassBundle().analyzeIdentifier(this);
    }
    
    public void setSinglePreserved() {
	super.setSinglePreserved();
	setNotConstant();
    }
    
    public void analyze() {
	String string = getType();
	int i = string.indexOf('L');
	if (i != -1) {
	    int i_0_ = string.indexOf(';', i);
	    Main.getClassBundle().reachableClass(string.substring
						     (i + 1, i_0_)
						     .replace('/', '.'));
	}
    }
    
    public Identifier getParent() {
	return clazz;
    }
    
    public String getFullName() {
	return clazz.getFullName() + "." + getName() + "." + getType();
    }
    
    public String getFullAlias() {
	return (clazz.getFullAlias() + "." + this.getAlias() + "."
		+ Main.getClassBundle().getTypeAlias(getType()));
    }
    
    public String getName() {
	return name;
    }
    
    public String getType() {
	return type;
    }
    
    public int getModifiers() {
	return info.getModifiers();
    }
    
    public Iterator getChilds() {
	return Collections.EMPTY_LIST.iterator();
    }
    
    public boolean isNotConstant() {
	return notConstant;
    }
    
    public Object getConstant() {
	return constant;
    }
    
    public void addFieldListener(Identifier identifier) {
	if (identifier == null)
	    throw new NullPointerException();
	if (fieldListeners == null)
	    fieldListeners = new HashSet();
	if (!fieldListeners.contains(identifier))
	    fieldListeners.add(identifier);
    }
    
    public void setNotConstant() {
	if (!notConstant) {
	    notConstant = true;
	    if (fieldListeners != null) {
		Iterator iterator = fieldListeners.iterator();
		while (iterator.hasNext())
		    Main.getClassBundle()
			.analyzeIdentifier((Identifier) iterator.next());
		fieldListeners = null;
	    }
	}
    }
    
    public String toString() {
	return "FieldIdentifier " + getFullName();
    }
    
    public boolean conflicting(String string) {
	return clazz.fieldConflicts(this, string);
    }
    
    public void doTransformations() {
	info.setName(this.getAlias());
	info.setType(Main.getClassBundle().getTypeAlias(type));
    }
}
