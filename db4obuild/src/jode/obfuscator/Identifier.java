/* Identifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.util.Iterator;
import java.util.Map;

import jode.GlobalOptions;

public abstract class Identifier
{
    private Identifier right = null;
    private Identifier left = null;
    private boolean reachable = false;
    private boolean preserved = false;
    private String alias = null;
    private boolean wasAliased = false;
    static int serialnr = 0;
    
    public Identifier(String string) {
	alias = string;
    }
    
    public final boolean isReachable() {
	return reachable;
    }
    
    public final boolean isPreserved() {
	return preserved;
    }
    
    protected void setSinglePreserved() {
	/* empty */
    }
    
    protected void setSingleReachable() {
	if (getParent() != null)
	    getParent().setReachable();
    }
    
    public void setReachable() {
	if (!reachable) {
	    reachable = true;
	    setSingleReachable();
	}
    }
    
    public void setPreserved() {
	if (!preserved) {
	    preserved = true;
	    for (Identifier identifier_0_ = this; identifier_0_ != null;
		 identifier_0_ = identifier_0_.left)
		identifier_0_.setSinglePreserved();
	    for (Identifier identifier_1_ = right; identifier_1_ != null;
		 identifier_1_ = identifier_1_.right)
		identifier_1_.setSinglePreserved();
	}
    }
    
    public Identifier getRepresentative() {
	Identifier identifier_2_;
	for (identifier_2_ = this; identifier_2_.left != null;
	     identifier_2_ = identifier_2_.left) {
	    /* empty */
	}
	return identifier_2_;
    }
    
    public final boolean isRepresentative() {
	return left == null;
    }
    
    public final boolean wasAliased() {
	return getRepresentative().wasAliased;
    }
    
    public final void setAlias(String string) {
	if (string != null) {
	    Identifier identifier_3_ = getRepresentative();
	    identifier_3_.wasAliased = true;
	    identifier_3_.alias = string;
	}
    }
    
    public final String getAlias() {
	return getRepresentative().alias;
    }
    
    public void addShadow(Identifier identifier_4_) {
	if (isPreserved() && !identifier_4_.isPreserved())
	    identifier_4_.setPreserved();
	else if (!isPreserved() && identifier_4_.isPreserved())
	    setPreserved();
	Identifier identifier_5_;
	for (identifier_5_ = this; identifier_5_.right != null;
	     identifier_5_ = identifier_5_.right) {
	    /* empty */
	}
	Identifier identifier_6_;
	for (identifier_6_ = identifier_4_; identifier_6_.right != null;
	     identifier_6_ = identifier_6_.right) {
	    /* empty */
	}
	if (identifier_6_ != identifier_5_) {
	    for (/**/; identifier_4_.left != null;
		 identifier_4_ = identifier_4_.left) {
		/* empty */
	    }
	    identifier_5_.right = identifier_4_;
	    identifier_4_.left = identifier_5_;
	}
    }
    
    public void buildTable(Renamer renamer) {
	if (isReachable() || (Main.stripping & 0x1) == 0) {
	    if (isPreserved()) {
		if (GlobalOptions.verboseLevel > 4)
		    GlobalOptions.err
			.println(this.toString() + " is preserved");
	    } else {
		Identifier identifier_7_ = getRepresentative();
		if (!identifier_7_.wasAliased) {
		    identifier_7_.wasAliased = true;
		    identifier_7_.alias = "";
		    Iterator iterator = renamer.generateNames(this);
		    String string;
		while_35_:
		    for (;;) {
			string = (String) iterator.next();
			Identifier identifier_8_ = identifier_7_;
			for (;;) {
			    if (identifier_8_ == null)
				break while_35_;
			    if (identifier_8_.conflicting(string))
				break;
			    identifier_8_ = identifier_8_.right;
			}
		    }
		    setAlias(string.toString());
		}
	    }
	    Iterator iterator = getChilds();
	    while (iterator.hasNext())
		((Identifier) iterator.next()).buildTable(renamer);
	}
    }
    
    public void writeTable(Map map, boolean bool) {
	if (isReachable() || (Main.stripping & 0x1) == 0) {
	    if (getAlias().length() != 0) {
		String string = getName();
		for (Identifier identifier_9_ = getParent();
		     (identifier_9_ != null
		      && identifier_9_.getAlias().length() == 0);
		     identifier_9_ = identifier_9_.getParent()) {
		    if (identifier_9_.getName().length() > 0)
			string = identifier_9_.getName() + "." + string;
		}
		if (bool)
		    map.put(getFullAlias(), string);
		else
		    map.put(getFullName(), getAlias());
	    }
	    Iterator iterator = getChilds();
	    while (iterator.hasNext())
		((Identifier) iterator.next()).writeTable(map, bool);
	}
    }
    
    public void readTable(Map map) {
	Identifier identifier_10_ = getRepresentative();
	if (!identifier_10_.wasAliased) {
	    String string = (String) map.get(getFullName());
	    if (string != null) {
		identifier_10_.wasAliased = true;
		identifier_10_.setAlias(string);
	    }
	}
	Iterator iterator = getChilds();
	while (iterator.hasNext())
	    ((Identifier) iterator.next()).readTable(map);
    }
    
    public void applyPreserveRule(IdentifierMatcher identifiermatcher) {
	if (identifiermatcher.matches(this)) {
	    System.err.println("preserving: " + this);
	    setReachable();
	    for (Identifier identifier_11_ = this; identifier_11_ != null;
		 identifier_11_ = identifier_11_.getParent())
		identifier_11_.setPreserved();
	}
	Iterator iterator = getChilds();
	while (iterator.hasNext())
	    ((Identifier) iterator.next())
		.applyPreserveRule(identifiermatcher);
    }
    
    public abstract Iterator getChilds();
    
    public abstract Identifier getParent();
    
    public abstract String getName();
    
    public abstract String getType();
    
    public abstract String getFullName();
    
    public abstract String getFullAlias();
    
    public abstract boolean conflicting(String string);
    
    public void analyze() {
	/* empty */
    }
}
