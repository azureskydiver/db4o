/* ClassIdentifier - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.FieldInfo;
import jode.bytecode.InnerClassInfo;
import jode.bytecode.MethodInfo;
import jode.bytecode.Reference;
import jode.obfuscator.modules.ModifierMatcher;

public class ClassIdentifier extends Identifier
{
    PackageIdentifier pack;
    String name;
    String fullName;
    ClassInfo info;
    String superName;
    String[] ifaceNames;
    List fieldIdents;
    List methodIdents;
    List knownSubClasses = new LinkedList();
    List virtualReachables = new LinkedList();
    
    public ClassIdentifier(PackageIdentifier packageidentifier, String string,
			   String string_0_, ClassInfo classinfo) {
	super(string_0_);
	pack = packageidentifier;
	fullName = string;
	name = string_0_;
	info = classinfo;
    }
    
    public void addSubClass(ClassIdentifier classidentifier_1_) {
	knownSubClasses.add(classidentifier_1_);
	Iterator iterator = virtualReachables.iterator();
	while (iterator.hasNext())
	    classidentifier_1_.reachableReference((Reference) iterator.next(),
						  true);
    }
    
    private FieldIdentifier findField(String string, String string_2_) {
	Iterator iterator = fieldIdents.iterator();
	while (iterator.hasNext()) {
	    FieldIdentifier fieldidentifier
		= (FieldIdentifier) iterator.next();
	    if (fieldidentifier.getName().equals(string)
		&& fieldidentifier.getType().equals(string_2_))
		return fieldidentifier;
	}
	return null;
    }
    
    private MethodIdentifier findMethod(String string, String string_3_) {
	Iterator iterator = methodIdents.iterator();
	while (iterator.hasNext()) {
	    MethodIdentifier methodidentifier
		= (MethodIdentifier) iterator.next();
	    if (methodidentifier.getName().equals(string)
		&& methodidentifier.getType().equals(string_3_))
		return methodidentifier;
	}
	return null;
    }
    
    public void reachableReference(Reference reference, boolean bool) {
	boolean bool_4_ = false;
	Iterator iterator = getChilds();
	while (iterator.hasNext()) {
	    Identifier identifier = (Identifier) iterator.next();
	    if (reference.getName().equals(identifier.getName())
		&& reference.getType().equals(identifier.getType())) {
		identifier.setReachable();
		bool_4_ = true;
	    }
	}
	if (!bool_4_) {
	    ClassIdentifier classidentifier_5_
		= Main.getClassBundle()
		      .getClassIdentifier(info.getSuperclass().getName());
	    if (classidentifier_5_ != null)
		classidentifier_5_.reachableReference(reference, false);
	}
	if (bool) {
	    Iterator iterator_6_ = virtualReachables.iterator();
	    while (iterator_6_.hasNext()) {
		Reference reference_7_ = (Reference) iterator_6_.next();
		if (reference_7_.getName().equals(reference.getName())
		    && reference_7_.getType().equals(reference.getType()))
		    return;
	    }
	    Iterator iterator_8_ = knownSubClasses.iterator();
	    while (iterator_8_.hasNext())
		((ClassIdentifier) iterator_8_.next())
		    .reachableReference(reference, false);
	    virtualReachables.add(reference);
	}
    }
    
    public void chainMethodIdentifier(Identifier identifier) {
	String string = identifier.getName();
	String string_9_ = identifier.getType();
	Iterator iterator = methodIdents.iterator();
	while (iterator.hasNext()) {
	    Identifier identifier_10_ = (Identifier) iterator.next();
	    if (identifier_10_.getName().equals(string)
		&& identifier_10_.getType().equals(string_9_))
		identifier.addShadow(identifier_10_);
	}
    }
    
    public long calcSerialVersionUID() {
	final MessageDigest md;
	try {
	    md = MessageDigest.getInstance("SHA");
	} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
	    nosuchalgorithmexception.printStackTrace();
	    GlobalOptions.err.println("Can't calculate serialVersionUID");
	    return 0L;
	}
	OutputStream outputstream = new OutputStream() {
	    public void write(int i) {
		md.update((byte) i);
	    }
	    
	    public void write(byte[] is, int i, int i_12_) {
		md.update(is, i, i_12_);
	    }
	};
	DataOutputStream dataoutputstream = new DataOutputStream(outputstream);
	try {
	    dataoutputstream.writeUTF(info.getName());
	    int i = info.getModifiers();
	    i &= 0x611;
	    dataoutputstream.writeInt(i);
	    ClassInfo[] classinfos
		= (ClassInfo[]) info.getInterfaces().clone();
	    Arrays.sort(classinfos, new Comparator() {
		public int compare(Object object, Object object_14_) {
		    return ((ClassInfo) object).getName()
			       .compareTo(((ClassInfo) object_14_).getName());
		}
	    });
	    for (int i_15_ = 0; i_15_ < classinfos.length; i_15_++)
		dataoutputstream.writeUTF(classinfos[i_15_].getName());
	    Comparator comparator = new Comparator() {
		public int compare(Object object, Object object_17_) {
		    Identifier identifier = (Identifier) object;
		    Identifier identifier_18_ = (Identifier) object_17_;
		    String string = identifier.getName();
		    String string_19_ = identifier_18_.getName();
		    boolean bool
			= string.equals("<init>") || string.equals("<clinit>");
		    boolean bool_20_ = (string_19_.equals("<init>")
					|| string_19_.equals("<clinit>"));
		    if (bool != bool_20_)
			return bool ? -1 : 1;
		    int i_21_ = identifier.getName()
				    .compareTo(identifier_18_.getName());
		    if (i_21_ != 0)
			return i_21_;
		    return identifier.getType()
			       .compareTo(identifier_18_.getType());
		}
	    };
	    List list = Arrays.asList(fieldIdents.toArray());
	    List list_22_ = Arrays.asList(methodIdents.toArray());
	    Collections.sort(list, comparator);
	    Collections.sort(list_22_, comparator);
	    Iterator iterator = list.iterator();
	    while (iterator.hasNext()) {
		FieldIdentifier fieldidentifier
		    = (FieldIdentifier) iterator.next();
		i = fieldidentifier.info.getModifiers();
		if ((i & 0x2) == 0 || (i & 0x88) == 0) {
		    dataoutputstream.writeUTF(fieldidentifier.getName());
		    dataoutputstream.writeInt(i);
		    dataoutputstream.writeUTF(fieldidentifier.getType());
		}
	    }
	    Iterator iterator_23_ = list_22_.iterator();
	    while (iterator_23_.hasNext()) {
		MethodIdentifier methodidentifier
		    = (MethodIdentifier) iterator_23_.next();
		i = methodidentifier.info.getModifiers();
		if (!Modifier.isPrivate(i)) {
		    dataoutputstream.writeUTF(methodidentifier.getName());
		    dataoutputstream.writeInt(i);
		    dataoutputstream.writeUTF(methodidentifier.getType()
						  .replace('/', '.'));
		}
	    }
	    dataoutputstream.close();
	    byte[] is = md.digest();
	    long l = 0L;
	    for (int i_24_ = 0; i_24_ < 8; i_24_++)
		l += (long) (is[i_24_] & 0xff) << 8 * i_24_;
	    return l;
	} catch (IOException ioexception) {
	    ioexception.printStackTrace();
	    GlobalOptions.err.println("Can't calculate serialVersionUID");
	    return 0L;
	}
    }
    
    public void addSUID() {
	long l = calcSerialVersionUID();
	FieldInfo fieldinfo = new FieldInfo(info, "serialVersionUID", "J", 25);
	fieldinfo.setConstant(new Long(l));
	FieldIdentifier fieldidentifier = new FieldIdentifier(this, fieldinfo);
	fieldIdents.add(fieldidentifier);
	fieldidentifier.setPreserved();
    }
    
    public boolean isSerializable() {
	return ClassInfo.forName("java.lang.Serializable").implementedBy(info);
    }
    
    public boolean hasSUID() {
	return findField("serialVersionUID", "J") != null;
    }
    
    protected void setSinglePreserved() {
	pack.setPreserved();
    }
    
    public void setSingleReachable() {
	super.setSingleReachable();
	Main.getClassBundle().analyzeIdentifier(this);
    }
    
    public void analyzeSuperClasses(ClassInfo classinfo) {
	for (/**/; classinfo != null; classinfo = classinfo.getSuperclass()) {
	    ClassIdentifier classidentifier_25_
		= Main.getClassBundle()
		      .getClassIdentifier(classinfo.getName());
	    if (classidentifier_25_ != null)
		classidentifier_25_.addSubClass(this);
	    else {
		String string
		    = ("L" + classinfo.getName().replace('.', '/') + ";")
			  .intern();
		MethodInfo[] methodinfos = classinfo.getMethods();
		for (int i = 0; i < methodinfos.length; i++) {
		    int i_26_ = methodinfos[i].getModifiers();
		    if ((0x1a & i_26_) == 0
			&& !methodinfos[i].getName().equals("<init>"))
			reachableReference
			    (Reference.getReference(string,
						    methodinfos[i].getName(),
						    methodinfos[i].getType()),
			     true);
		}
	    }
	    ClassInfo[] classinfos = classinfo.getInterfaces();
	    for (int i = 0; i < classinfos.length; i++)
		analyzeSuperClasses(classinfos[i]);
	}
    }
    
    public void analyze() {
	if (GlobalOptions.verboseLevel > 0)
	    GlobalOptions.err.println("Reachable: " + this);
	ClassInfo[] classinfos = info.getInterfaces();
	for (int i = 0; i < classinfos.length; i++)
	    analyzeSuperClasses(classinfos[i]);
	analyzeSuperClasses(info.getSuperclass());
    }
    
    public void initSuperClasses(ClassInfo classinfo) {
	for (/**/; classinfo != null; classinfo = classinfo.getSuperclass()) {
	    ClassIdentifier classidentifier_27_
		= Main.getClassBundle()
		      .getClassIdentifier(classinfo.getName());
	    if (classidentifier_27_ != null) {
		Iterator iterator
		    = classidentifier_27_.getMethodIdents().iterator();
		while (iterator.hasNext()) {
		    MethodIdentifier methodidentifier
			= (MethodIdentifier) iterator.next();
		    int i = methodidentifier.info.getModifiers();
		    if ((0x1a & i) == 0
			&& !methodidentifier.getName().equals("<init>"))
			chainMethodIdentifier(methodidentifier);
		}
	    } else {
		MethodInfo[] methodinfos = classinfo.getMethods();
		for (int i = 0; i < methodinfos.length; i++) {
		    int i_28_ = methodinfos[i].getModifiers();
		    if ((0x1a & i_28_) == 0
			&& !methodinfos[i].getName().equals("<init>")) {
			MethodIdentifier methodidentifier
			    = findMethod(methodinfos[i].getName(),
					 methodinfos[i].getType());
			if (methodidentifier != null)
			    methodidentifier.setPreserved();
		    }
		}
	    }
	    ClassInfo[] classinfos = classinfo.getInterfaces();
	    for (int i = 0; i < classinfos.length; i++)
		initSuperClasses(classinfos[i]);
	}
    }
    
    public void initClass() {
	ClassInfo classinfo = info;
	if (this != null) {
	    /* empty */
	}
	classinfo.loadInfo(255);
	FieldInfo[] fieldinfos = info.getFields();
	MethodInfo[] methodinfos = info.getMethods();
	if (Main.swapOrder) {
	    Collections.shuffle(Arrays.asList(fieldinfos), Main.rand);
	    Collections.shuffle(Arrays.asList(methodinfos), Main.rand);
	}
	fieldIdents = new ArrayList(fieldinfos.length);
	methodIdents = new ArrayList(methodinfos.length);
	for (int i = 0; i < fieldinfos.length; i++)
	    fieldIdents.add(new FieldIdentifier(this, fieldinfos[i]));
	for (int i = 0; i < methodinfos.length; i++) {
	    MethodIdentifier methodidentifier
		= new MethodIdentifier(this, methodinfos[i]);
	    methodIdents.add(methodidentifier);
	    if (methodidentifier.getName().equals("<clinit>")) {
		methodidentifier.setPreserved();
		methodidentifier.setReachable();
	    } else if (methodidentifier.getName().equals("<init>"))
		methodidentifier.setPreserved();
	}
	ClassInfo[] classinfos = info.getInterfaces();
	ifaceNames = new String[classinfos.length];
	for (int i = 0; i < classinfos.length; i++) {
	    ifaceNames[i] = classinfos[i].getName();
	    initSuperClasses(classinfos[i]);
	}
	if (info.getSuperclass() != null) {
	    superName = info.getSuperclass().getName();
	    initSuperClasses(info.getSuperclass());
	}
	if ((Main.stripping & 0x10) != 0)
	    info.setSourceFile(null);
	if ((Main.stripping & 0x2) != 0) {
	    info.setInnerClasses(new InnerClassInfo[0]);
	    info.setOuterClasses(new InnerClassInfo[0]);
	    info.setExtraClasses(new InnerClassInfo[0]);
	}
	InnerClassInfo[] innerclassinfos = info.getInnerClasses();
	InnerClassInfo[] innerclassinfos_29_ = info.getOuterClasses();
	InnerClassInfo[] innerclassinfos_30_ = info.getExtraClasses();
	if (innerclassinfos_29_ != null) {
	    for (int i = 0; i < innerclassinfos_29_.length; i++) {
		if (innerclassinfos_29_[i].outer != null)
		    Main.getClassBundle()
			.getClassIdentifier(innerclassinfos_29_[i].outer);
	    }
	}
	if (innerclassinfos != null) {
	    for (int i = 0; i < innerclassinfos.length; i++)
		Main.getClassBundle()
		    .getClassIdentifier(innerclassinfos[i].inner);
	}
	if (innerclassinfos_30_ != null) {
	    for (int i = 0; i < innerclassinfos_30_.length; i++) {
		Main.getClassBundle()
		    .getClassIdentifier(innerclassinfos_30_[i].inner);
		if (innerclassinfos_30_[i].outer != null)
		    Main.getClassBundle()
			.getClassIdentifier(innerclassinfos_30_[i].outer);
	    }
	}
    }
    
    public void addIfaces(Collection collection,
			  ClassIdentifier classidentifier_31_) {
	ClassInfo[] classinfos = classidentifier_31_.info.getInterfaces();
	for (int i = 0; i < classinfos.length; i++) {
	    ClassIdentifier classidentifier_32_
		= Main.getClassBundle()
		      .getClassIdentifier(classinfos[i].getName());
	    if (classidentifier_32_ != null
		&& !classidentifier_32_.isReachable())
		addIfaces(collection, classidentifier_32_);
	    else
		collection.add(classinfos[i]);
	}
    }
    
    public void transformSuperIfaces() {
	if ((Main.stripping & 0x1) != 0) {
	    LinkedList linkedlist = new LinkedList();
	    ClassIdentifier classidentifier_33_ = this;
	    for (;;) {
		addIfaces(linkedlist, classidentifier_33_);
		ClassIdentifier classidentifier_34_
		    = Main.getClassBundle()
			  .getClassIdentifier(classidentifier_33_.superName);
		if (classidentifier_34_ == null
		    || classidentifier_34_.isReachable())
		    break;
		classidentifier_33_ = classidentifier_34_;
	    }
	    ClassInfo classinfo = classidentifier_33_.info.getSuperclass();
	    ClassInfo[] classinfos
		= ((ClassInfo[])
		   linkedlist.toArray(new ClassInfo[linkedlist.size()]));
	    info.setSuperclass(classinfo);
	    info.setInterfaces(classinfos);
	}
    }
    
    public void transformInnerClasses() {
	InnerClassInfo[] innerclassinfos = info.getOuterClasses();
	if (innerclassinfos != null) {
	    int i = innerclassinfos.length;
	    if ((Main.stripping & 0x1) != 0) {
		for (int i_35_ = 0; i_35_ < innerclassinfos.length; i_35_++) {
		    if (innerclassinfos[i_35_].outer != null) {
			ClassIdentifier classidentifier_36_
			    = (Main.getClassBundle().getClassIdentifier
			       (innerclassinfos[i_35_].outer));
			if (classidentifier_36_ != null
			    && !classidentifier_36_.isReachable())
			    i--;
		    }
		}
	    }
	    if (i == 0)
		info.setOuterClasses(null);
	    else {
		InnerClassInfo[] innerclassinfos_37_ = new InnerClassInfo[i];
		int i_38_ = 0;
		String string = getFullAlias();
		for (int i_39_ = 0; i_39_ < innerclassinfos.length; i_39_++) {
		    ClassIdentifier classidentifier_40_
			= (innerclassinfos[i_39_].outer != null
			   ? (Main.getClassBundle().getClassIdentifier
			      (innerclassinfos[i_39_].outer))
			   : null);
		    if (classidentifier_40_ == null
			|| classidentifier_40_.isReachable()) {
			String string_41_ = string;
			String string_42_
			    = (classidentifier_40_ == null
			       ? innerclassinfos[i_39_].outer
			       : classidentifier_40_.getFullAlias());
			String string_43_
			    = (innerclassinfos[i_39_].name == null ? null
			       : (string_42_ != null
				  && string_41_.startsWith(string_42_ + "$"))
			       ? string_41_.substring(string_42_.length() + 1)
			       : string_41_.substring(string_41_
							  .lastIndexOf('.')
						      + 1));
			innerclassinfos_37_[i_38_++]
			    = new InnerClassInfo(string_41_, string_42_,
						 string_43_,
						 (innerclassinfos[i_39_]
						  .modifiers));
			string = string_42_;
		    }
		}
		info.setOuterClasses(innerclassinfos_37_);
	    }
	}
	InnerClassInfo[] innerclassinfos_44_ = info.getInnerClasses();
	if (innerclassinfos_44_ != null) {
	    int i = innerclassinfos_44_.length;
	    if ((Main.stripping & 0x1) != 0) {
		for (int i_45_ = 0; i_45_ < innerclassinfos_44_.length;
		     i_45_++) {
		    ClassIdentifier classidentifier_46_
			= (Main.getClassBundle().getClassIdentifier
			   (innerclassinfos_44_[i_45_].inner));
		    if (classidentifier_46_ != null
			&& !classidentifier_46_.isReachable())
			i--;
		}
	    }
	    if (i == 0)
		info.setInnerClasses(null);
	    else {
		InnerClassInfo[] innerclassinfos_47_ = new InnerClassInfo[i];
		int i_48_ = 0;
		for (int i_49_ = 0; i_49_ < innerclassinfos_44_.length;
		     i_49_++) {
		    ClassIdentifier classidentifier_50_
			= (Main.getClassBundle().getClassIdentifier
			   (innerclassinfos_44_[i_49_].inner));
		    if (classidentifier_50_ == null
			|| (Main.stripping & 0x1) == 0
			|| classidentifier_50_.isReachable()) {
			String string = (classidentifier_50_ == null
					 ? innerclassinfos_44_[i_49_].inner
					 : classidentifier_50_.getFullAlias());
			String string_51_ = getFullAlias();
			String string_52_
			    = (innerclassinfos_44_[i_49_].name == null ? null
			       : (string_51_ != null
				  && string.startsWith(string_51_ + "$"))
			       ? string.substring(string_51_.length() + 1)
			       : string
				     .substring(string.lastIndexOf('.') + 1));
			innerclassinfos_47_[i_48_++]
			    = new InnerClassInfo(string, string_51_,
						 string_52_,
						 (innerclassinfos_44_[i_49_]
						  .modifiers));
		    }
		}
		info.setInnerClasses(innerclassinfos_47_);
	    }
	}
	InnerClassInfo[] innerclassinfos_53_ = info.getExtraClasses();
	if (innerclassinfos_53_ != null) {
	    int i = innerclassinfos_53_.length;
	    if ((Main.stripping & 0x1) != 0) {
		for (int i_54_ = 0; i_54_ < innerclassinfos_53_.length;
		     i_54_++) {
		    ClassIdentifier classidentifier_55_
			= (innerclassinfos_53_[i_54_].outer != null
			   ? (Main.getClassBundle().getClassIdentifier
			      (innerclassinfos_53_[i_54_].outer))
			   : null);
		    ClassIdentifier classidentifier_56_
			= (Main.getClassBundle().getClassIdentifier
			   (innerclassinfos_53_[i_54_].inner));
		    if ((classidentifier_55_ != null
			 && !classidentifier_55_.isReachable())
			|| (classidentifier_56_ != null
			    && !classidentifier_56_.isReachable()))
			i--;
		}
	    }
	    if (i == 0)
		info.setExtraClasses(null);
	    else {
		InnerClassInfo[] innerclassinfos_57_
		    = i > 0 ? new InnerClassInfo[i] : null;
		int i_58_ = 0;
		for (int i_59_ = 0; i_59_ < innerclassinfos_53_.length;
		     i_59_++) {
		    ClassIdentifier classidentifier_60_
			= (innerclassinfos_53_[i_59_].outer != null
			   ? (Main.getClassBundle().getClassIdentifier
			      (innerclassinfos_53_[i_59_].outer))
			   : null);
		    ClassIdentifier classidentifier_61_
			= (Main.getClassBundle().getClassIdentifier
			   (innerclassinfos_53_[i_59_].inner));
		    if ((classidentifier_61_ == null
			 || classidentifier_61_.isReachable())
			&& (classidentifier_60_ == null
			    || classidentifier_60_.isReachable())) {
			String string = (classidentifier_61_ == null
					 ? innerclassinfos_53_[i_59_].inner
					 : classidentifier_61_.getFullAlias());
			String string_62_
			    = (classidentifier_60_ == null
			       ? innerclassinfos_53_[i_59_].outer
			       : classidentifier_60_.getFullAlias());
			String string_63_
			    = (innerclassinfos_53_[i_59_].name == null ? null
			       : (string_62_ != null
				  && string.startsWith(string_62_ + "$"))
			       ? string.substring(string_62_.length() + 1)
			       : string
				     .substring(string.lastIndexOf('.') + 1));
			innerclassinfos_57_[i_58_++]
			    = new InnerClassInfo(string, string_62_,
						 string_63_,
						 (innerclassinfos_53_[i_59_]
						  .modifiers));
		    }
		}
		info.setExtraClasses(innerclassinfos_57_);
	    }
	}
    }
    
    public void doTransformations() {
	if (GlobalOptions.verboseLevel > 0)
	    GlobalOptions.err.println("Transforming " + this);
	info.setName(getFullAlias());
	transformSuperIfaces();
	transformInnerClasses();
	ArrayList arraylist = new ArrayList(fieldIdents.size());
	ArrayList arraylist_64_ = new ArrayList(methodIdents.size());
	Iterator iterator = fieldIdents.iterator();
	while (iterator.hasNext()) {
	    FieldIdentifier fieldidentifier
		= (FieldIdentifier) iterator.next();
	    if ((Main.stripping & 0x1) == 0 || fieldidentifier.isReachable()) {
		fieldidentifier.doTransformations();
		arraylist.add(fieldidentifier.info);
	    }
	}
	Iterator iterator_65_ = methodIdents.iterator();
	while (iterator_65_.hasNext()) {
	    MethodIdentifier methodidentifier
		= (MethodIdentifier) iterator_65_.next();
	    if ((Main.stripping & 0x1) == 0
		|| methodidentifier.isReachable()) {
		methodidentifier.doTransformations();
		arraylist_64_.add(methodidentifier.info);
	    }
	}
	info.setFields((FieldInfo[])
		       arraylist.toArray(new FieldInfo[arraylist.size()]));
	info.setMethods((MethodInfo[])
			arraylist_64_
			    .toArray(new MethodInfo[arraylist_64_.size()]));
    }
    
    public void storeClass(DataOutputStream dataoutputstream)
	throws IOException {
	if (GlobalOptions.verboseLevel > 0)
	    GlobalOptions.err.println("Writing " + this);
	info.write(dataoutputstream);
	info = null;
	fieldIdents = methodIdents = null;
    }
    
    public Identifier getParent() {
	return pack;
    }
    
    public String getFullName() {
	return fullName;
    }
    
    public String getFullAlias() {
	if (pack.parent == null)
	    return this.getAlias();
	return pack.getFullAlias() + "." + this.getAlias();
    }
    
    public String getName() {
	return name;
    }
    
    public String getType() {
	return "Ljava/lang/Class;";
    }
    
    public int getModifiers() {
	return info.getModifiers();
    }
    
    public List getFieldIdents() {
	return fieldIdents;
    }
    
    public List getMethodIdents() {
	return methodIdents;
    }
    
    public Iterator getChilds() {
	final Iterator fieldIter = fieldIdents.iterator();
	final Iterator methodIter = methodIdents.iterator();
	return new Iterator() {
	    boolean fieldsNext = fieldIter.hasNext();
	    
	    public boolean hasNext() {
		return fieldsNext ? true : methodIter.hasNext();
	    }
	    
	    public Object next() {
		if (fieldsNext) {
		    Object object = fieldIter.next();
		    fieldsNext = fieldIter.hasNext();
		    return object;
		}
		return methodIter.next();
	    }
	    
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }
    
    public String toString() {
	return "ClassIdentifier " + getFullName();
    }
    
    public Identifier getIdentifier(String string, String string_68_) {
	Iterator iterator = getChilds();
	while (iterator.hasNext()) {
	    Identifier identifier = (Identifier) iterator.next();
	    if (identifier.getName().equals(string)
		&& identifier.getType().startsWith(string_68_))
		return identifier;
	}
	if (superName != null) {
	    ClassIdentifier classidentifier_69_
		= Main.getClassBundle().getClassIdentifier(superName);
	    if (classidentifier_69_ != null) {
		Identifier identifier
		    = classidentifier_69_.getIdentifier(string, string_68_);
		if (identifier != null)
		    return identifier;
	    }
	}
	return null;
    }
    
    public boolean containsFieldAliasDirectly
	(String string, String string_70_,
	 IdentifierMatcher identifiermatcher) {
	Iterator iterator = fieldIdents.iterator();
	while (iterator.hasNext()) {
	    Identifier identifier = (Identifier) iterator.next();
	    if (((Main.stripping & 0x1) == 0 || identifier.isReachable())
		&& identifier.wasAliased()
		&& identifier.getAlias().equals(string)
		&& identifier.getType().startsWith(string_70_)
		&& identifiermatcher.matches(identifier))
		return true;
	}
	return false;
    }
    
    public boolean containsMethodAliasDirectly
	(String string, String string_71_,
	 IdentifierMatcher identifiermatcher) {
	Iterator iterator = methodIdents.iterator();
	while (iterator.hasNext()) {
	    Identifier identifier = (Identifier) iterator.next();
	    if (((Main.stripping & 0x1) == 0 || identifier.isReachable())
		&& identifier.wasAliased()
		&& identifier.getAlias().equals(string)
		&& identifier.getType().startsWith(string_71_)
		&& identifiermatcher.matches(identifier))
		return true;
	}
	return false;
    }
    
    public boolean fieldConflicts(FieldIdentifier fieldidentifier,
				  String string) {
	String string_72_
	    = (Main.options & 0x1) != 0 ? fieldidentifier.getType() : "";
	ModifierMatcher modifiermatcher = ModifierMatcher.allowAll;
	if (containsFieldAliasDirectly(string, string_72_, modifiermatcher))
	    return true;
	return false;
    }
    
    public boolean methodConflicts(MethodIdentifier methodidentifier,
				   String string) {
	String string_73_ = methodidentifier.getType();
	if ((Main.options & 0x1) == 0)
	    string_73_ = string_73_.substring(0, string_73_.indexOf(')') + 1);
	ModifierMatcher modifiermatcher = ModifierMatcher.allowAll;
	if (containsMethodAliasDirectly(string, string_73_, modifiermatcher))
	    return true;
	ModifierMatcher modifiermatcher_74_
	    = modifiermatcher.forceAccess(0, true);
	if (methodidentifier.info.isStatic())
	    modifiermatcher_74_.forbidModifier(8);
	ClassInfo classinfo = info.getSuperclass();
	ClassIdentifier classidentifier_75_ = this;
	for (/**/; classinfo != null; classinfo = classinfo.getSuperclass()) {
	    ClassIdentifier classidentifier_76_
		= Main.getClassBundle()
		      .getClassIdentifier(classinfo.getName());
	    if (classidentifier_76_ != null) {
		if (classidentifier_76_.containsMethodAliasDirectly
		    (string, string_73_, modifiermatcher_74_))
		    return true;
	    } else {
		MethodInfo[] methodinfos = classinfo.getMethods();
		for (int i = 0; i < methodinfos.length; i++) {
		    if (methodinfos[i].getName().equals(string)
			&& methodinfos[i].getType().startsWith(string_73_)
			&& modifiermatcher_74_
			       .matches(methodinfos[i].getModifiers()))
			return true;
		}
	    }
	}
	if (modifiermatcher_74_.matches(methodidentifier)) {
	    Iterator iterator = knownSubClasses.iterator();
	    while (iterator.hasNext()) {
		ClassIdentifier classidentifier_77_
		    = (ClassIdentifier) iterator.next();
		if (classidentifier_77_.containsMethodAliasDirectly
		    (string, string_73_, modifiermatcher_74_))
		    return true;
	    }
	}
	return false;
    }
    
    public boolean conflicting(String string) {
	return pack.contains(string, this);
    }
}
