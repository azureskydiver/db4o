/* StrongRenamer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;
import java.util.Iterator;

import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.LocalIdentifier;
import jode.obfuscator.MethodIdentifier;
import jode.obfuscator.OptionHandler;
import jode.obfuscator.PackageIdentifier;
import jode.obfuscator.Renamer;

public class StrongRenamer implements Renamer, OptionHandler
{
    static String[] idents
	= { "Package", "Class", "Field", "Method", "Local" };
    static String[] parts = { "Start", "Part" };
    String[][] charsets;
    String[] javaKeywords
	= { "abstract", "default", "if", "private", "throw", "boolean", "do",
	    "implements", "protected", "throws", "break", "double", "import",
	    "public", "transient", "byte", "else", "instanceof", "return",
	    "try", "case", "extends", "int", "short", "void", "catch", "final",
	    "interface", "static", "volatile", "char", "finally", "long",
	    "super", "while", "class", "float", "native", "switch", "const",
	    "for", "new", "synchronized", "continue", "goto", "package",
	    "this", "strictfp", "null", "true", "false" };
    
    public StrongRenamer() {
	charsets = new String[idents.length][parts.length];
	for (int i = 0; i < idents.length; i++) {
	    for (int i_0_ = 0; i_0_ < parts.length; i_0_++)
		charsets[i][i_0_] = "abcdefghijklmnopqrstuvwxyz";
	}
    }
    
    public void setOption(String string, Collection collection) {
	if (string.startsWith("charset")) {
	    Object object = collection.iterator().next();
	    if (collection.size() != 1 || !(object instanceof String))
		throw new IllegalArgumentException
			  ("Only string parameter are supported.");
	    String string_1_ = (String) object;
	    String string_2_ = string.substring("charset".length());
	    int i = -1;
	    int i_3_ = -1;
	    if (string_2_.length() > 0) {
		for (int i_4_ = 0; i_4_ < idents.length; i_4_++) {
		    if (string_2_.startsWith(idents[i_4_])) {
			string_2_ = string_2_.substring(idents[i_4_].length());
			i_3_ = i_4_;
			break;
		    }
		}
	    }
	    if (string_2_.length() > 0) {
		for (int i_5_ = 0; i_5_ < parts.length; i_5_++) {
		    if (string_2_.startsWith(parts[i_5_])) {
			string_2_ = string_2_.substring(parts[i_5_].length());
			i = i_5_;
			break;
		    }
		}
	    }
	    if (string_2_.length() > 0)
		throw new IllegalArgumentException("Invalid charset `" + string
						   + "'");
	    for (int i_6_ = 0; i_6_ < idents.length; i_6_++) {
		if (i_3_ < 0 || i_3_ == i_6_) {
		    for (int i_7_ = 0; i_7_ < parts.length; i_7_++) {
			if (i < 0 || i == i_7_)
			    charsets[i_6_][i_7_] = string_1_;
		    }
		}
	    }
	} else
	    throw new IllegalArgumentException("Invalid option `" + string
					       + "'");
    }
    
    public Iterator generateNames(Identifier identifier) {
	int i;
	if (identifier instanceof PackageIdentifier)
	    i = 0;
	else if (identifier instanceof ClassIdentifier)
	    i = 1;
	else if (identifier instanceof FieldIdentifier)
	    i = 2;
	else if (identifier instanceof MethodIdentifier)
	    i = 3;
	else if (identifier instanceof LocalIdentifier)
	    i = 4;
	else
	    throw new IllegalArgumentException(identifier.getClass()
						   .getName());
	final String[] theCharset = charsets[i];
	return new Iterator() {
	    char[] name = null;
	    int headIndex;
	    
	    public boolean hasNext() {
		return true;
	    }
	    
	    public Object next() {
		if (name == null) {
		    name = new char[] { theCharset[0].charAt(0) };
		    headIndex = 0;
		    return new String(name);
		}
		String string;
	    while_31_:
		for (;;) {
		    if (++headIndex < theCharset[0].length()) {
			name[0] = theCharset[0].charAt(headIndex);
			return new String(name);
		    }
		    headIndex = 0;
		    name[0] = theCharset[0].charAt(0);
		    String string_9_ = theCharset[1];
		    for (int i_10_ = 1; i_10_ < name.length; i_10_++) {
			int i_11_ = string_9_.indexOf(name[i_10_]) + 1;
			if (i_11_ < string_9_.length()) {
			    name[i_10_] = string_9_.charAt(i_11_);
			    return new String(name);
			}
			name[i_10_] = string_9_.charAt(0);
		    }
		    name = new char[name.length + 1];
		    name[0] = theCharset[0].charAt(0);
		    char c = theCharset[1].charAt(0);
		    for (int i_12_ = 1; i_12_ < name.length; i_12_++)
			name[i_12_] = c;
		    string = new String(name);
		    int i_13_ = 0;
		    for (;;) {
			if (i_13_ >= javaKeywords.length)
			    break while_31_;
			if (string.equals(javaKeywords[i_13_]))
			    break;
			i_13_++;
		    }
		}
		return string;
	    }
	    
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }
}
