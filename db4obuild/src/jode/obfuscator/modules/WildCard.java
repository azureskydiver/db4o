/* WildCard - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;

import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.OptionHandler;

public class WildCard implements IdentifierMatcher, OptionHandler
{
    String wildcard;
    int firstStar;
    
    public WildCard() {
	/* empty */
    }
    
    public WildCard(String string) {
	this.wildcard = string;
	firstStar = this.wildcard.indexOf('*');
    }
    
    public void setOption(String string, Collection collection) {
	if (string.equals("value")) {
	    if (collection.size() != 1)
		throw new IllegalArgumentException
			  ("Wildcard supports only one value.");
	    this.wildcard = (String) collection.iterator().next();
	    firstStar = this.wildcard.indexOf('*');
	} else
	    throw new IllegalArgumentException("Invalid option `" + string
					       + "'.");
    }
    
    public String getNextComponent(Identifier identifier) {
	String string = identifier.getFullName();
	if (string.length() > 0)
	    string += ".";
	int i = string.length();
	if (!this.wildcard.startsWith(string))
	    return null;
	int i_0_ = this.wildcard.indexOf('.', i);
	if (i_0_ > 0 && (i_0_ <= firstStar || firstStar == -1))
	    return this.wildcard.substring(i, i_0_);
	if (firstStar == -1)
	    return this.wildcard.substring(i);
	return null;
    }
    
    public boolean matchesSub(Identifier identifier, String string) {
	String string_1_ = identifier.getFullName();
	if (string_1_.length() > 0)
	    string_1_ += ".";
	if (string != null)
	    string_1_ += (String) string;
	if (firstStar == -1 || firstStar >= string_1_.length())
	    return this.wildcard.startsWith(string_1_);
	return string_1_.startsWith(this.wildcard.substring(0, firstStar));
    }
    
    public boolean matches(Identifier identifier) {
	String string = identifier.getFullName();
	if (firstStar == -1) {
	    if (this.wildcard.equals(string))
		return true;
	    return false;
	}
	if (!string.startsWith(this.wildcard.substring(0, firstStar)))
	    return false;
	string = string.substring(firstStar);
	int i;
	int i_2_;
	for (i_2_ = firstStar;
	     (i = this.wildcard.indexOf('*', i_2_ + 1)) != -1; i_2_ = i) {
	    for (String string_3_ = this.wildcard.substring(i_2_ + 1, i);
		 !string.startsWith(string_3_); string = string.substring(1)) {
		if (string.length() == 0)
		    return false;
	    }
	    string = string.substring(i - i_2_ - 1);
	}
	return string.endsWith(this.wildcard.substring(i_2_ + 1));
    }
    
    public String toString() {
	return "Wildcard " + this.wildcard;
    }
}
