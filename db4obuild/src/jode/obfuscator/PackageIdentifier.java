/* PackageIdentifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import jode.GlobalOptions;
import jode.bytecode.ClassInfo;

public class PackageIdentifier extends Identifier
{
    ClassBundle bundle;
    PackageIdentifier parent;
    String name;
    String fullName;
    boolean loadOnDemand;
    Map loadedClasses;
    List swappedClasses;
    
    public PackageIdentifier(ClassBundle classbundle,
			     PackageIdentifier packageidentifier_0_,
			     String string, String string_1_) {
	super(string_1_);
	bundle = classbundle;
	parent = packageidentifier_0_;
	fullName = string;
	name = string_1_;
	loadedClasses = new HashMap();
    }
    
    protected void setSinglePreserved() {
	if (parent != null)
	    parent.setPreserved();
    }
    
    public void setLoadOnDemand() {
	if (!loadOnDemand) {
	    loadOnDemand = true;
	    if ((Main.stripping & 0x1) == 0) {
		String string = fullName.length() > 0 ? fullName + "." : "";
		Enumeration enumeration
		    = ClassInfo.getClassesAndPackages(getFullName());
		while (enumeration.hasMoreElements()) {
		    String string_2_
			= ((String) enumeration.nextElement()).intern();
		    if (!loadedClasses.containsKey(string_2_)) {
			String string_3_ = (string + string_2_).intern();
			if (ClassInfo.isPackage(string_3_)) {
			    PackageIdentifier packageidentifier_4_
				= new PackageIdentifier(bundle, this,
							string_3_, string_2_);
			    loadedClasses.put(string_2_, packageidentifier_4_);
			    swappedClasses = null;
			    packageidentifier_4_.setLoadOnDemand();
			} else {
			    ClassIdentifier classidentifier
				= new ClassIdentifier(this, string_3_,
						      string_2_,
						      ClassInfo
							  .forName(string_3_));
			    if (GlobalOptions.verboseLevel > 1)
				GlobalOptions.err
				    .println("preloading Class " + string_3_);
			    loadedClasses.put(string_2_, classidentifier);
			    swappedClasses = null;
			    bundle.addClassIdentifier(classidentifier);
			    classidentifier.initClass();
			}
		    }
		}
		loadOnDemand = false;
	    }
	}
    }
    
    public Identifier getIdentifier(String string) {
	if (loadOnDemand) {
	    Identifier identifier = loadClass(string);
	    return identifier;
	}
	int i = string.indexOf('.');
	if (i == -1)
	    return (Identifier) loadedClasses.get(string);
	PackageIdentifier packageidentifier_5_
	    = (PackageIdentifier) loadedClasses.get(string.substring(0, i));
	if (packageidentifier_5_ != null)
	    return packageidentifier_5_.getIdentifier(string.substring(i + 1));
	return null;
    }
    
    public Identifier loadClass(String string) {
	int i = string.indexOf('.');
	if (i == -1) {
	    Identifier identifier = (Identifier) loadedClasses.get(string);
	    if (identifier == null) {
		String string_6_
		    = fullName.length() > 0 ? fullName + "." + string : string;
		string_6_ = string_6_.intern();
		if (ClassInfo.isPackage(string_6_)) {
		    PackageIdentifier packageidentifier_7_
			= new PackageIdentifier(bundle, this, string_6_,
						string);
		    loadedClasses.put(string, packageidentifier_7_);
		    swappedClasses = null;
		    packageidentifier_7_.setLoadOnDemand();
		    identifier = packageidentifier_7_;
		} else if (!ClassInfo.exists(string_6_)) {
		    GlobalOptions.err
			.println("Warning: Can't find class " + string_6_);
		    Thread.dumpStack();
		} else {
		    identifier
			= new ClassIdentifier(this, string_6_, string,
					      ClassInfo.forName(string_6_));
		    loadedClasses.put(string, identifier);
		    swappedClasses = null;
		    bundle.addClassIdentifier(identifier);
		    ((ClassIdentifier) identifier).initClass();
		}
	    }
	    return identifier;
	}
	String string_8_ = string.substring(0, i);
	PackageIdentifier packageidentifier_9_
	    = (PackageIdentifier) loadedClasses.get(string_8_);
	if (packageidentifier_9_ == null) {
	    String string_10_ = (fullName.length() > 0
				 ? fullName + "." + string_8_ : string_8_);
	    string_10_ = string_10_.intern();
	    if (ClassInfo.isPackage(string_10_)) {
		packageidentifier_9_
		    = new PackageIdentifier(bundle, this, string_10_,
					    string_8_);
		loadedClasses.put(string_8_, packageidentifier_9_);
		swappedClasses = null;
		if (loadOnDemand)
		    packageidentifier_9_.setLoadOnDemand();
	    }
	}
	if (packageidentifier_9_ != null)
	    return packageidentifier_9_.loadClass(string.substring(i + 1));
	return null;
    }
    
    public void loadMatchingClasses(IdentifierMatcher identifiermatcher) {
	String string = identifiermatcher.getNextComponent(this);
	if (string != null) {
	    Identifier identifier = (Identifier) loadedClasses.get(string);
	    if (identifier == null) {
		string = string.intern();
		String string_11_
		    = fullName.length() > 0 ? fullName + "." + string : string;
		string_11_ = string_11_.intern();
		if (ClassInfo.isPackage(string_11_)) {
		    identifier = new PackageIdentifier(bundle, this,
						       string_11_, string);
		    loadedClasses.put(string, identifier);
		    swappedClasses = null;
		    if (loadOnDemand)
			((PackageIdentifier) identifier).setLoadOnDemand();
		} else if (ClassInfo.exists(string_11_)) {
		    if (GlobalOptions.verboseLevel > 1)
			GlobalOptions.err
			    .println("loading Class " + string_11_);
		    identifier
			= new ClassIdentifier(this, string_11_, string,
					      ClassInfo.forName(string_11_));
		    if (loadOnDemand
			|| identifiermatcher.matches(identifier)) {
			loadedClasses.put(string, identifier);
			swappedClasses = null;
			bundle.addClassIdentifier(identifier);
			((ClassIdentifier) identifier).initClass();
		    }
		} else
		    GlobalOptions.err.println
			("Warning: Can't find class/package " + string_11_);
	    }
	    if (identifier instanceof PackageIdentifier) {
		if (identifiermatcher.matches(identifier)) {
		    if (GlobalOptions.verboseLevel > 0)
			GlobalOptions.err.println("loading Package "
						  + identifier.getFullName());
		    ((PackageIdentifier) identifier).setLoadOnDemand();
		}
		if (identifiermatcher.matchesSub(identifier, null))
		    ((PackageIdentifier) identifier)
			.loadMatchingClasses(identifiermatcher);
	    }
	} else {
	    String string_12_ = fullName.length() > 0 ? fullName + "." : "";
	    Enumeration enumeration
		= ClassInfo.getClassesAndPackages(getFullName());
	    while (enumeration.hasMoreElements()) {
		String string_13_
		    = ((String) enumeration.nextElement()).intern();
		if (!loadedClasses.containsKey(string_13_)) {
		    String string_14_ = (string_12_ + string_13_).intern();
		    if (identifiermatcher.matchesSub(this, string_13_)) {
			if (ClassInfo.isPackage(string_14_)) {
			    if (GlobalOptions.verboseLevel > 0)
				GlobalOptions.err
				    .println("loading Package " + string_14_);
			    PackageIdentifier packageidentifier_15_
				= new PackageIdentifier(bundle, this,
							string_14_,
							string_13_);
			    loadedClasses.put(string_13_,
					      packageidentifier_15_);
			    swappedClasses = null;
			    if (loadOnDemand
				|| identifiermatcher
				       .matches(packageidentifier_15_))
				packageidentifier_15_.setLoadOnDemand();
			} else {
			    ClassIdentifier classidentifier
				= (new ClassIdentifier
				   (this, string_14_, string_13_,
				    ClassInfo.forName(string_14_)));
			    if (loadOnDemand
				|| identifiermatcher
				       .matches(classidentifier)) {
				if (GlobalOptions.verboseLevel > 1)
				    GlobalOptions.err.println("loading Class "
							      + string_14_);
				loadedClasses.put(string_13_, classidentifier);
				swappedClasses = null;
				bundle.addClassIdentifier(classidentifier);
				classidentifier.initClass();
			    }
			}
		    }
		}
	    }
	    ArrayList arraylist = new ArrayList();
	    arraylist.addAll(loadedClasses.values());
	    Iterator iterator = arraylist.iterator();
	    while (iterator.hasNext()) {
		Identifier identifier = (Identifier) iterator.next();
		if (identifier instanceof PackageIdentifier) {
		    if (identifiermatcher.matches(identifier))
			((PackageIdentifier) identifier).setLoadOnDemand();
		    if (identifiermatcher.matchesSub(identifier, null))
			((PackageIdentifier) identifier)
			    .loadMatchingClasses(identifiermatcher);
		}
	    }
	}
    }
    
    public void applyPreserveRule(IdentifierMatcher identifiermatcher) {
	if (loadOnDemand)
	    loadMatchingClasses(identifiermatcher);
	super.applyPreserveRule(identifiermatcher);
    }
    
    public String getFullName() {
	return fullName;
    }
    
    public String getFullAlias() {
	if (parent != null) {
	    String string = parent.getFullAlias();
	    String string_16_ = this.getAlias();
	    if (string_16_.length() == 0)
		return string;
	    if (string.length() == 0)
		return string_16_;
	    return string + "." + string_16_;
	}
	return "";
    }
    
    public void buildTable(Renamer renamer) {
	loadOnDemand = false;
	super.buildTable(renamer);
    }
    
    public void doTransformations() {
	Iterator iterator = getChilds();
	while (iterator.hasNext()) {
	    Identifier identifier = (Identifier) iterator.next();
	    if (identifier instanceof ClassIdentifier)
		((ClassIdentifier) identifier).doTransformations();
	    else
		((PackageIdentifier) identifier).doTransformations();
	}
    }
    
    public void readTable(Map map) {
	if (parent != null)
	    this.setAlias((String) map.get(getFullName()));
	Iterator iterator = loadedClasses.values().iterator();
	while (iterator.hasNext()) {
	    Identifier identifier = (Identifier) iterator.next();
	    if ((Main.stripping & 0x1) == 0 || identifier.isReachable())
		identifier.readTable(map);
	}
    }
    
    public Identifier getParent() {
	return parent;
    }
    
    public String getName() {
	return name;
    }
    
    public String getType() {
	return "package";
    }
    
    public Iterator getChilds() {
	if (swappedClasses == null) {
	    swappedClasses = Arrays.asList(loadedClasses.values().toArray());
	    Collections.shuffle(swappedClasses, Main.rand);
	}
	return swappedClasses.iterator();
    }
    
    public void storeClasses(ZipOutputStream zipoutputstream) {
	Iterator iterator = getChilds();
	while (iterator.hasNext()) {
	    Identifier identifier = (Identifier) iterator.next();
	    if ((Main.stripping & 0x1) != 0 && !identifier.isReachable()) {
		if (GlobalOptions.verboseLevel > 4)
		    GlobalOptions.err.println("Class/Package "
					      + identifier.getFullName()
					      + " is not reachable");
	    } else if (identifier instanceof PackageIdentifier)
		((PackageIdentifier) identifier).storeClasses(zipoutputstream);
	    else {
		try {
		    String string
			= (identifier.getFullAlias().replace('.', '/')
			   + ".class");
		    zipoutputstream.putNextEntry(new ZipEntry(string));
		    DataOutputStream dataoutputstream
			= (new DataOutputStream
			   (new BufferedOutputStream(zipoutputstream)));
		    ((ClassIdentifier) identifier)
			.storeClass(dataoutputstream);
		    dataoutputstream.flush();
		    zipoutputstream.closeEntry();
		} catch (IOException ioexception) {
		    GlobalOptions.err
			.println("Can't write Class " + identifier.getName());
		    ioexception.printStackTrace(GlobalOptions.err);
		}
	    }
	}
    }
    
    public void storeClasses(File file) {
	File file_17_
	    = parent == null ? file : new File(file, this.getAlias());
	if (!file_17_.exists() && !file_17_.mkdir())
	    GlobalOptions.err.println("Could not create directory "
				      + file_17_.getPath()
				      + ", check permissions.");
	Iterator iterator = getChilds();
	while (iterator.hasNext()) {
	    Identifier identifier = (Identifier) iterator.next();
	    if ((Main.stripping & 0x1) != 0 && !identifier.isReachable()) {
		if (GlobalOptions.verboseLevel > 4)
		    GlobalOptions.err.println("Class/Package "
					      + identifier.getFullName()
					      + " is not reachable");
	    } else if (identifier instanceof PackageIdentifier)
		((PackageIdentifier) identifier).storeClasses(file_17_);
	    else {
		try {
		    File file_18_
			= new File(file_17_, identifier.getAlias() + ".class");
		    DataOutputStream dataoutputstream
			= (new DataOutputStream
			   (new BufferedOutputStream
			    (new FileOutputStream(file_18_))));
		    ((ClassIdentifier) identifier)
			.storeClass(dataoutputstream);
		    dataoutputstream.close();
		} catch (IOException ioexception) {
		    GlobalOptions.err
			.println("Can't write Class " + identifier.getName());
		    ioexception.printStackTrace(GlobalOptions.err);
		}
	    }
	}
    }
    
    public String toString() {
	return parent == null ? "base package" : getFullName();
    }
    
    public boolean contains(String string, Identifier identifier) {
	Iterator iterator = loadedClasses.values().iterator();
	while (iterator.hasNext()) {
	    Identifier identifier_19_ = (Identifier) iterator.next();
	    if (identifier_19_ != identifier) {
		if (((Main.stripping & 0x1) == 0
		     || identifier_19_.isReachable())
		    && identifier_19_.getAlias().equalsIgnoreCase(string))
		    return true;
		if (identifier_19_ instanceof PackageIdentifier
		    && identifier_19_.getAlias().length() == 0
		    && ((PackageIdentifier) identifier_19_).contains(string,
								     this))
		    return true;
	    }
	}
	if (this.getAlias().length() == 0 && parent != null
	    && parent != identifier && parent.contains(string, this))
	    return true;
	return false;
    }
    
    public boolean conflicting(String string) {
	return parent.contains(string, this);
    }
}
