/* ModifierMatcher - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator.modules;
import java.util.Collection;
import java.util.Iterator;

import jode.obfuscator.ClassIdentifier;
import jode.obfuscator.FieldIdentifier;
import jode.obfuscator.Identifier;
import jode.obfuscator.IdentifierMatcher;
import jode.obfuscator.MethodIdentifier;
import jode.obfuscator.OptionHandler;

public class ModifierMatcher
    implements IdentifierMatcher, OptionHandler, Cloneable
{
    static final int PUBLIC = 1;
    static final int PROTECTED = 4;
    static final int PRIVATE = 2;
    int[] andMasks;
    int[] xorMasks;
    public static ModifierMatcher denyAll
	= new ModifierMatcher(new int[0], new int[0]);
    public static ModifierMatcher allowAll = new ModifierMatcher(0, 0);
    
    public ModifierMatcher() {
	this(0, 0);
    }
    
    private ModifierMatcher(int[] is, int[] is_0_) {
	andMasks = is;
	xorMasks = is_0_;
    }
    
    public ModifierMatcher(int i, int i_1_) {
	andMasks = new int[] { i };
	xorMasks = new int[] { i_1_ };
    }
    
    public void setOption(String string, Collection collection) {
	ModifierMatcher modifiermatcher_2_ = this;
	if (string.equals("access")) {
	    Iterator iterator = collection.iterator();
	    while (iterator.hasNext()) {
		String string_3_ = (String) iterator.next();
		boolean bool = string_3_.charAt(0) == '<';
		boolean bool_4_ = string_3_.charAt(0) == '>';
		if (bool || bool_4_)
		    string_3_ = string_3_.substring(1);
		string_3_ = string_3_.toUpperCase();
		if (bool) {
		    int i = (string_3_.equals("PROTECTED") ? 1
			     : string_3_.equals("PACKAGE") ? 4
			     : string_3_.equals("PRIVATE") ? 0 : -1);
		    if (i == -1)
			throw new IllegalArgumentException
				  ("Unknown access modifier " + string_3_);
		    modifiermatcher_2_
			= modifiermatcher_2_.forbidAccess(i, true);
		} else {
		    int i = (string_3_.equals("PUBLIC") ? 1
			     : string_3_.equals("PROTECTED") ? 4
			     : string_3_.equals("PACKAGE") ? 0
			     : string_3_.equals("PRIVATE") ? 2 : -1);
		    if (i == -1)
			throw new IllegalArgumentException("Unknown access "
							   + string_3_);
		    modifiermatcher_2_
			= modifiermatcher_2_.forceAccess(i, bool_4_);
		}
	    }
	} else if (string.equals("modifier")) {
	    Iterator iterator = collection.iterator();
	    while (iterator.hasNext()) {
		String string_5_ = (String) iterator.next();
		boolean bool = string_5_.charAt(0) == '!';
		if (bool)
		    string_5_ = string_5_.substring(1);
		string_5_ = string_5_.toUpperCase();
		int i = (string_5_.equals("ABSTRACT") ? 1024
			 : string_5_.equals("FINAL") ? 16
			 : string_5_.equals("INTERFACE") ? 512
			 : string_5_.equals("NATIVE") ? 256
			 : string_5_.equals("STATIC") ? 8
			 : string_5_.equals("STRICT") ? 2048
			 : string_5_.equals("SYNCHRONIZED") ? 32
			 : string_5_.equals("TRANSIENT") ? 128
			 : string_5_.equals("VOLATILE") ? 64 : -1);
		if (i == -1)
		    throw new IllegalArgumentException("Unknown modifier "
						       + string_5_);
		if (bool)
		    modifiermatcher_2_ = modifiermatcher_2_.forbidModifier(i);
		else
		    modifiermatcher_2_ = modifiermatcher_2_.forceModifier(i);
	    }
	} else
	    throw new IllegalArgumentException("Invalid option `" + string
					       + "'.");
	andMasks = modifiermatcher_2_.andMasks;
	xorMasks = modifiermatcher_2_.xorMasks;
    }
    
    private static boolean implies(int i, int i_6_, int i_7_, int i_8_) {
	return (i & i_7_) == i_7_ && (i_6_ & i_7_) == i_8_;
    }
    
    private boolean implies(int i, int i_9_) {
	for (int i_10_ = 0; i_10_ < andMasks.length; i_10_++) {
	    if (!implies(andMasks[i_10_], xorMasks[i_10_], i, i_9_))
		return false;
	}
	return true;
    }
    
    private boolean impliedBy(int i, int i_11_) {
	for (int i_12_ = 0; i_12_ < andMasks.length; i_12_++) {
	    if (implies(i, i_11_, andMasks[i_12_], xorMasks[i_12_]))
		return true;
	}
	return false;
    }
    
    private boolean implies(ModifierMatcher modifiermatcher_13_) {
	for (int i = 0; i < andMasks.length; i++) {
	    if (!modifiermatcher_13_.impliedBy(andMasks[i], xorMasks[i]))
		return false;
	}
	return true;
    }
    
    public ModifierMatcher and(ModifierMatcher modifiermatcher_14_) {
	if (implies(modifiermatcher_14_))
	    return this;
	if (modifiermatcher_14_.implies(this))
	    return modifiermatcher_14_;
	ModifierMatcher modifiermatcher_15_ = denyAll;
	for (int i = 0; i < andMasks.length; i++)
	    modifiermatcher_15_
		= modifiermatcher_15_.or(modifiermatcher_14_.and(andMasks[i],
								 xorMasks[i]));
	return modifiermatcher_15_;
    }
    
    public ModifierMatcher or(ModifierMatcher modifiermatcher_16_) {
	if (implies(modifiermatcher_16_))
	    return modifiermatcher_16_;
	if (modifiermatcher_16_.implies(this))
	    return this;
	ModifierMatcher modifiermatcher_17_ = this;
	for (int i = 0; i < modifiermatcher_16_.andMasks.length; i++)
	    modifiermatcher_17_
		= modifiermatcher_17_.or(modifiermatcher_16_.andMasks[i],
					 modifiermatcher_16_.xorMasks[i]);
	return modifiermatcher_17_;
    }
    
    private ModifierMatcher and(int i, int i_18_) {
	if (implies(i, i_18_))
	    return this;
	int i_19_ = 0;
    while_28_:
	for (int i_20_ = 0; i_20_ < andMasks.length; i_20_++) {
	    if (!implies(i, i_18_, andMasks[i_20_], xorMasks[i_20_])) {
		for (int i_21_ = 0; i_21_ < andMasks.length; i_21_++) {
		    if (i_21_ != i_20_
			&& implies(i | andMasks[i_21_],
				   i_18_ | xorMasks[i_21_], andMasks[i_20_],
				   xorMasks[i_20_]))
			continue while_28_;
		}
		i_19_++;
	    }
	}
	if (i_19_ == 0)
	    return new ModifierMatcher(i, i_18_);
	int[] is = new int[i_19_];
	int[] is_22_ = new int[i_19_];
	int i_23_ = 0;
    while_30_:
	for (int i_24_ = 0; i_24_ < i_19_; i_24_++) {
	    if (!implies(i, i_18_, andMasks[i_24_], xorMasks[i_24_])) {
		for (int i_25_ = 0; i_25_ < andMasks.length; i_25_++) {
		    if (i_25_ != i_24_
			&& implies(i | andMasks[i_25_],
				   i_18_ | xorMasks[i_25_], andMasks[i_24_],
				   xorMasks[i_24_]))
			continue while_30_;
		}
		is[i_23_] = andMasks[i_24_] | i;
		is_22_[i_23_] = xorMasks[i_24_] | i_18_;
		i_23_++;
	    }
	}
	return new ModifierMatcher(is, is_22_);
    }
    
    private ModifierMatcher or(int i, int i_26_) {
	int i_27_ = -1;
	if (this == denyAll)
	    return new ModifierMatcher(i, i_26_);
	for (int i_28_ = 0; i_28_ < andMasks.length; i_28_++) {
	    if (implies(i, i_26_, andMasks[i_28_], xorMasks[i_28_]))
		return this;
	    if (implies(andMasks[i_28_], xorMasks[i_28_], i, i_26_)) {
		i_27_ = i_28_;
		break;
	    }
	}
	int[] is;
	int[] is_29_;
	if (i_27_ == -1) {
	    i_27_ = andMasks.length;
	    is = new int[i_27_ + 1];
	    is_29_ = new int[i_27_ + 1];
	    System.arraycopy(andMasks, 0, is, 0, i_27_);
	    System.arraycopy(xorMasks, 0, is_29_, 0, i_27_);
	} else {
	    is = (int[]) andMasks.clone();
	    is_29_ = (int[]) xorMasks.clone();
	}
	is[i_27_] = i;
	is_29_[i_27_] = i_26_;
	return new ModifierMatcher(is, is_29_);
    }
    
    public ModifierMatcher forceAccess(int i, boolean bool) {
	if (bool) {
	    if (i == 2)
		return this;
	    if (i == 0)
		return and(2, 0);
	    ModifierMatcher modifiermatcher_30_ = and(1, 1);
	    if (i == 4)
		return modifiermatcher_30_.or(and(4, 4));
	    if (i == 1)
		return modifiermatcher_30_;
	    throw new IllegalArgumentException("" + i);
	}
	if (i == 0)
	    return and(7, 0);
	return and(i, i);
    }
    
    public ModifierMatcher forbidAccess(int i, boolean bool) {
	if (bool) {
	    if (i == 2)
		return denyAll;
	    if (i == 0)
		return and(2, 2);
	    if (i == 4)
		return and(5, 0);
	    if (i == 1)
		return and(1, 0);
	    throw new IllegalArgumentException("" + i);
	}
	if (i == 0)
	    return and(2, 2).or(and(4, 4)).or(and(1, 1));
	return and(i, 0);
    }
    
    public final ModifierMatcher forceModifier(int i) {
	return and(i, i);
    }
    
    public final ModifierMatcher forbidModifier(int i) {
	return and(i, 0);
    }
    
    public final boolean matches(int i) {
	for (int i_31_ = 0; i_31_ < andMasks.length; i_31_++) {
	    if ((i & andMasks[i_31_]) == xorMasks[i_31_])
		return true;
	}
	return false;
    }
    
    public final boolean matches(Identifier identifier) {
	int i;
	if (identifier instanceof ClassIdentifier)
	    i = ((ClassIdentifier) identifier).getModifiers();
	else if (identifier instanceof MethodIdentifier)
	    i = ((MethodIdentifier) identifier).getModifiers();
	else if (identifier instanceof FieldIdentifier)
	    i = ((FieldIdentifier) identifier).getModifiers();
	else
	    return false;
	return matches(i);
    }
    
    public final boolean matchesSub(Identifier identifier, String string) {
	return true;
    }
    
    public final String getNextComponent(Identifier identifier) {
	return null;
    }
    
    public Object clone() {
	try {
	    return super.clone();
	} catch (CloneNotSupportedException clonenotsupportedexception) {
	    throw new IncompatibleClassChangeError(this.getClass().getName());
	}
    }
}
