/* MultiIdentifierMatcher - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;

import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.OptionHandler;

public class MultiIdentifierMatcher implements IdentifierMatcher, OptionHandler
{
    public static boolean OR = true;
    public static boolean AND = false;
    IdentifierMatcher[] matchers;
    boolean isOr;
    
    public MultiIdentifierMatcher() {
	matchers = new IdentifierMatcher[0];
    }
    
    public MultiIdentifierMatcher(boolean bool,
				  IdentifierMatcher[] identifiermatchers) {
	isOr = bool;
	matchers = identifiermatchers;
    }
    
    public void setOption(String string, Collection collection) {
	if (string.equals("or")) {
	    isOr = true;
	    matchers = ((IdentifierMatcher[])
			collection.toArray(new IdentifierMatcher
					   [collection.size()]));
	} else if (string.equals("and")) {
	    isOr = false;
	    matchers = ((IdentifierMatcher[])
			collection.toArray(new IdentifierMatcher
					   [collection.size()]));
	} else
	    throw new IllegalArgumentException("Invalid option `" + string
					       + "'.");
    }
    
    public boolean matches(Identifier identifier) {
	for (int i = 0; i < matchers.length; i++) {
	    if (matchers[i].matches(identifier) == isOr)
		return isOr;
	}
	return !isOr;
    }
    
    public boolean matchesSub(Identifier identifier, String string) {
	for (int i = 0; i < matchers.length; i++) {
	    if (matchers[i].matchesSub(identifier, string) == isOr)
		return isOr;
	}
	return !isOr;
    }
    
    public String getNextComponent(Identifier identifier) {
	if (isOr == AND) {
	    for (int i = 0; i < matchers.length; i++) {
		String string = matchers[i].getNextComponent(identifier);
		if (string != null && matchesSub(identifier, string))
		    return string;
	    }
	    return null;
	}
	String string = null;
	for (int i = 0; i < matchers.length; i++) {
	    if (matchesSub(identifier, null)) {
		if (string != null
		    && matchers[i].getNextComponent(identifier) != string)
		    return null;
		string = matchers[i].getNextComponent(identifier);
		if (string == null)
		    return null;
	    }
	}
	return string;
    }
}
