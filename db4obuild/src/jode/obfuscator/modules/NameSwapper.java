/* NameSwapper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.LocalIdentifier;
import jode.obfuscator.MethodIdentifier;
import jode.obfuscator.PackageIdentifier;
import jode.obfuscator.Renamer;

public class NameSwapper implements Renamer
{
    private Random rand;
    private Set packs;
    private Set clazzes;
    private Set methods;
    private Set fields;
    private Set locals;
    
    private class NameGenerator implements Iterator
    {
	Collection pool;
	
	NameGenerator(Collection collection) {
	    pool = collection;
	}
	
	public boolean hasNext() {
	    return true;
	}
	
	public Object next() {
	    int i = rand.nextInt(pool.size());
	    Iterator iterator = pool.iterator();
	    while (i > 0)
		iterator.next();
	    return (String) iterator.next();
	}
	
	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }
    
    public NameSwapper(boolean bool, long l) {
	if (bool)
	    packs = clazzes = methods = fields = locals = new HashSet();
	else {
	    packs = new HashSet();
	    clazzes = new HashSet();
	    methods = new HashSet();
	    fields = new HashSet();
	    locals = new HashSet();
	}
    }
    
    public NameSwapper(boolean bool) {
	this(bool, System.currentTimeMillis());
    }
    
    public final Collection getCollection(Identifier identifier) {
	if (identifier instanceof PackageIdentifier)
	    return packs;
	if (identifier instanceof ClassIdentifier)
	    return clazzes;
	if (identifier instanceof MethodIdentifier)
	    return methods;
	if (identifier instanceof FieldIdentifier)
	    return fields;
	if (identifier instanceof LocalIdentifier)
	    return locals;
	throw new IllegalArgumentException(identifier.getClass().getName());
    }
    
    public final void addIdentifierName(Identifier identifier) {
	getCollection(identifier).add(identifier.getName());
    }
    
    public Iterator generateNames(Identifier identifier) {
	return new NameGenerator(getCollection(identifier));
    }
}
