/* SerializePreserver - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;

import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.MethodIdentifier;
import jode.obfuscator.PackageIdentifier;

public class SerializePreserver implements IdentifierMatcher
{
    boolean onlySUID = true;
    
    public void setOption(String string, Collection collection) {
	if (string.equals("all"))
	    onlySUID = false;
	else
	    throw new IllegalArgumentException("Invalid option `" + string
					       + "'.");
    }
    
    public final boolean matchesSub(Identifier identifier, String string) {
	if (identifier instanceof PackageIdentifier)
	    return true;
	if (identifier instanceof ClassIdentifier) {
	    ClassIdentifier classidentifier = (ClassIdentifier) identifier;
	    return (classidentifier.isSerializable()
		    && (!onlySUID || classidentifier.hasSUID()));
	}
	return false;
    }
    
    public final boolean matches(Identifier identifier) {
	ClassIdentifier classidentifier;
	if (identifier instanceof ClassIdentifier)
	    classidentifier = (ClassIdentifier) identifier;
	else if (identifier instanceof FieldIdentifier)
	    classidentifier = (ClassIdentifier) identifier.getParent();
	else
	    return false;
	if (!classidentifier.isSerializable()
	    || onlySUID && !classidentifier.hasSUID())
	    return false;
	if (identifier instanceof FieldIdentifier) {
	    FieldIdentifier fieldidentifier = (FieldIdentifier) identifier;
	    if ((fieldidentifier.getModifiers() & 0x88) == 0)
		return true;
	    if (identifier.getName().equals("serialPersistentFields")
		|| identifier.getName().equals("serialVersionUID"))
		return true;
	} else if (identifier instanceof MethodIdentifier) {
	    if (identifier.getName().equals("writeObject")
		&& identifier.getType()
		       .equals("(Ljava.io.ObjectOutputStream)V"))
		return true;
	    if (identifier.getName().equals("readObject")
		&& identifier.getType()
		       .equals("(Ljava.io.ObjectInputStream)V"))
		return true;
	} else if (identifier instanceof ClassIdentifier) {
	    if (!classidentifier.hasSUID())
		classidentifier.addSUID();
	    return true;
	}
	return false;
    }
    
    public final String getNextComponent(Identifier identifier) {
	return null;
    }
}
