/* ImportHandler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.IOException;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedMap;
import java.util.TreeMap;

import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;
import jode.type.ArrayType;
import jode.type.ClassInterfacesType;
import jode.type.NullType;
import jode.type.Type;

public class ImportHandler
{
    public static final int DEFAULT_PACKAGE_LIMIT = 2147483647;
    public static final int DEFAULT_CLASS_LIMIT = 1;
    SortedMap imports;
    Hashtable cachedClassNames = null;
    ClassAnalyzer main;
    String className;
    String pkg;
    int importPackageLimit;
    int importClassLimit;
    static Comparator comparator = new Comparator() {
	public int compare(Object object, Object object_0_) {
	    String string = (String) object;
	    String string_1_ = (String) object_0_;
	    boolean bool = string.startsWith("java");
	    boolean bool_2_ = string_1_.startsWith("java");
	    if (bool != bool_2_)
		return bool ? -1 : 1;
	    return string.compareTo(string_1_);
	}
    };
    
    public ImportHandler() {
	this(2147483647, 1);
    }
    
    public ImportHandler(int i, int i_3_) {
	importPackageLimit = i;
	importClassLimit = i_3_;
    }
    
    private boolean conflictsImport(String string) {
	int i = string.lastIndexOf('.');
	if (i != -1) {
	    String string_4_ = string.substring(0, i);
	    if (string_4_.equals(pkg))
		return false;
	    string = string.substring(i);
	    if (pkg.length() != 0) {
		if (ClassInfo.exists(pkg + string))
		    return true;
	    } else if (ClassInfo.exists(string.substring(1)))
		return true;
	    Iterator iterator = imports.keySet().iterator();
	    while (iterator.hasNext()) {
		String string_5_ = (String) iterator.next();
		if (string_5_.endsWith(".*")) {
		    string_5_ = string_5_.substring(0, string_5_.length() - 2);
		    if (!string_5_.equals(string_4_)
			&& ClassInfo.exists(string_5_ + string))
			return true;
		} else if (string_5_.endsWith(string)
			   || string_5_.equals(string.substring(1)))
		    return true;
	    }
	}
	return false;
    }
    
    private void cleanUpImports() {
	Integer integer = new Integer(2147483647);
	TreeMap treemap = new TreeMap(comparator);
	LinkedList linkedlist = new LinkedList();
	Iterator iterator = imports.keySet().iterator();
	while (iterator.hasNext()) {
	    String string = (String) iterator.next();
	    Integer integer_6_ = (Integer) imports.get(string);
	    if (!string.endsWith(".*")) {
		if (integer_6_.intValue() >= importClassLimit) {
		    int i = string.lastIndexOf(".");
		    if (i != -1) {
			if (!treemap
				 .containsKey(string.substring(0, i) + ".*"))
			    linkedlist.add(string);
		    } else if (pkg.length() != 0)
			treemap.put(string, integer);
		}
	    } else if (integer_6_.intValue() >= importPackageLimit)
		treemap.put(string, integer);
	}
	imports = treemap;
	cachedClassNames = new Hashtable();
	iterator = linkedlist.iterator();
	while (iterator.hasNext()) {
	    String string = (String) iterator.next();
	    if (!conflictsImport(string)) {
		imports.put(string, integer);
		String string_7_
		    = string.substring(string.lastIndexOf('.') + 1);
		cachedClassNames.put(string, string_7_);
	    }
	}
    }
    
    public void dumpHeader(TabbedPrintWriter tabbedprintwriter)
	throws IOException {
	tabbedprintwriter.println("/* " + className + " - Decompiled by JODE");
	tabbedprintwriter.println(" * Visit http://jode.sourceforge.net/");
	tabbedprintwriter.println(" */");
	if (pkg.length() != 0)
	    tabbedprintwriter.println("package " + pkg + ";");
	cleanUpImports();
	Iterator iterator = imports.keySet().iterator();
	String string = null;
	while (iterator.hasNext()) {
	    String string_8_ = (String) iterator.next();
	    if (!string_8_.equals("java.lang.*")) {
		int i = string_8_.indexOf('.');
		if (i != -1) {
		    String string_9_ = string_8_.substring(0, i);
		    if (string != null && !string.equals(string_9_))
			tabbedprintwriter.println("");
		    string = string_9_;
		}
		tabbedprintwriter.println("import " + string_8_ + ";");
	    }
	}
	tabbedprintwriter.println("");
    }
    
    public void error(String string) {
	GlobalOptions.err.println(string);
    }
    
    public void init(String string) {
	imports = new TreeMap(comparator);
	imports.put("java.lang.*", new Integer(2147483647));
	int i = string.lastIndexOf('.');
	pkg = i == -1 ? "" : string.substring(0, i);
	className = i == -1 ? string : string.substring(i + 1);
    }
    
    public void useClass(ClassInfo classinfo) {
	for (;;) {
	    InnerClassInfo[] innerclassinfos = classinfo.getOuterClasses();
	    if (innerclassinfos == null)
		break;
	    if (innerclassinfos[0].name == null
		|| innerclassinfos[0].outer == null)
		return;
	    classinfo = ClassInfo.forName(innerclassinfos[0].outer);
	}
	String string = classinfo.getName();
	Integer integer = (Integer) imports.get(string);
	if (integer == null) {
	    int i = string.lastIndexOf('.');
	    if (i != -1) {
		String string_10_ = string.substring(0, i);
		if (string_10_.equals(pkg))
		    return;
		Integer integer_11_ = (Integer) imports.get(string_10_ + ".*");
		if (integer_11_ != null
		    && integer_11_.intValue() >= importPackageLimit)
		    return;
		integer_11_ = (integer_11_ == null ? new Integer(1)
			       : new Integer(integer_11_.intValue() + 1));
		imports.put(string_10_ + ".*", integer_11_);
	    }
	    integer = new Integer(1);
	} else {
	    if (integer.intValue() >= importClassLimit)
		return;
	    integer = new Integer(integer.intValue() + 1);
	}
	imports.put(string, integer);
    }
    
    public final void useType(Type type) {
	if (type instanceof ArrayType)
	    useType(((ArrayType) type).getElementType());
	else if (type instanceof ClassInterfacesType)
	    useClass(((ClassInterfacesType) type).getClassInfo());
    }
    
    public String getClassString(ClassInfo classinfo) {
	String string = classinfo.getName();
	if (cachedClassNames == null)
	    return string;
	String string_12_ = (String) cachedClassNames.get(string);
	if (string_12_ != null)
	    return string_12_;
	int i = string.lastIndexOf('.');
	if (i != -1) {
	    String string_13_ = string.substring(0, i);
	    if (string_13_.equals(pkg)
		|| (imports.get(string_13_ + ".*") != null
		    && !conflictsImport(string))) {
		String string_14_ = string.substring(i + 1);
		cachedClassNames.put(string, string_14_);
		return string_14_;
	    }
	}
	cachedClassNames.put(string, string);
	return string;
    }
    
    public String getTypeString(Type type) {
	if (type instanceof ArrayType)
	    return getTypeString(((ArrayType) type).getElementType()) + "[]";
	if (type instanceof ClassInterfacesType)
	    return getClassString(((ClassInterfacesType) type).getClassInfo());
	if (type instanceof NullType)
	    return "Object";
	return type.toString();
    }
    
    protected int loadFileFlags() {
	return 1;
    }
}
