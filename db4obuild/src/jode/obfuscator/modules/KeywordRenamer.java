/* KeywordRenamer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;
import java.util.Iterator;

import jode.obfuscator.Identifier;
import jode.obfuscator.OptionHandler;
import jode.obfuscator.Renamer;

public class KeywordRenamer implements Renamer, OptionHandler
{
    String[] keywords
	= { "if", "else", "for", "while", "throw", "return", "class",
	    "interface", "implements", "extends", "instanceof", "new", "int",
	    "boolean", "long", "float", "double", "short", "public",
	    "protected", "private", "static", "synchronized", "strict",
	    "transient", "abstract", "volatile", "final", "Object", "String",
	    "Thread", "Runnable", "StringBuffer", "Vector" };
    Renamer backup = new StrongRenamer();
    
    public void setOption(String string, Collection collection) {
	if (string.startsWith("keywords"))
	    keywords
		= (String[]) collection.toArray(new String[collection.size()]);
	else if (string.startsWith("backup")) {
	    if (collection.size() != 1)
		throw new IllegalArgumentException
			  ("Only one backup is allowed");
	    backup = (Renamer) collection.iterator().next();
	} else
	    throw new IllegalArgumentException("Invalid option `" + string
					       + "'");
    }
    
    public Iterator generateNames(final Identifier ident) {
	return new Iterator() {
	    int pos = 0;
	    Iterator backing = null;
	    
	    public boolean hasNext() {
		return true;
	    }
	    
	    public Object next() {
		if (pos < keywords.length)
		    return keywords[pos++];
		if (backing == null)
		    backing = backup.generateNames(ident);
		return backing.next();
	    }
	    
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }
}
