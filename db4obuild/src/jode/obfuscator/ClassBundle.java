/* ClassBundle - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.zip.ZipOutputStream;

import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.Reference;
import jode.obfuscator.modules.MultiIdentifierMatcher;
import jode.obfuscator.modules.SimpleAnalyzer;
import jode.obfuscator.modules.WildCard;

public class ClassBundle implements OptionHandler
{
    PackageIdentifier basePackage;
    Set toAnalyze = new HashSet();
    String classPath
	= System.getProperty("java.class.path").replace(File.pathSeparatorChar,
							',');
    String destDir = ".";
    String inTableFile;
    String outTableFile;
    String outRevTableFile;
    IdentifierMatcher loading;
    IdentifierMatcher preserving;
    IdentifierMatcher reaching;
    CodeTransformer[] preTrafos;
    CodeAnalyzer analyzer;
    CodeTransformer[] postTrafos;
    Renamer renamer;
    private static final Map aliasesHash = new WeakHashMap();
    private static final Map clazzCache = new HashMap();
    private static final Map referenceCache = new HashMap();
    
    public ClassBundle() {
	basePackage = new PackageIdentifier(this, null, "", "");
	basePackage.setReachable();
	basePackage.setPreserved();
    }
    
    public static void setStripOptions(Collection collection) {
	/* empty */
    }
    
    public void setOption(String string, Collection collection) {
	if (string.equals("classpath")) {
	    Iterator iterator = collection.iterator();
	    StringBuffer stringbuffer
		= new StringBuffer((String) iterator.next());
	    while (iterator.hasNext())
		stringbuffer.append(',').append((String) iterator.next());
	    ClassInfo.setClassPath(stringbuffer.toString());
	} else if (string.equals("dest")) {
	    if (collection.size() != 1)
		throw new IllegalArgumentException
			  ("Only one destination path allowed");
	    destDir = (String) collection.iterator().next();
	} else if (string.equals("verbose")) {
	    if (collection.size() != 1)
		throw new IllegalArgumentException
			  ("Verbose takes one int parameter");
	    GlobalOptions.verboseLevel
		= ((Integer) collection.iterator().next()).intValue();
	} else if (string.equals("intable") || string.equals("table")) {
	    if (collection.size() != 1)
		throw new IllegalArgumentException
			  ("Only one destination path allowed");
	    inTableFile = (String) collection.iterator().next();
	} else if (string.equals("outtable")) {
	    if (collection.size() != 1)
		throw new IllegalArgumentException
			  ("Only one destination path allowed");
	    outTableFile = (String) collection.iterator().next();
	} else if (string.equals("outrevtable") || string.equals("revtable")) {
	    if (collection.size() != 1)
		throw new IllegalArgumentException
			  ("Only one destination path allowed");
	    outRevTableFile = (String) collection.iterator().next();
	} else if (string.equals("strip")) {
	    Iterator iterator = collection.iterator();
	while_34_:
	    while (iterator.hasNext()) {
		String string_0_ = (String) iterator.next();
		for (int i = 0; i < Main.stripNames.length; i++) {
		    if (string_0_.equals(Main.stripNames[i])) {
			Main.stripping |= 1 << i;
			continue while_34_;
		    }
		}
		throw new IllegalArgumentException("Unknown strip option: `"
						   + string_0_ + "'");
	    }
	} else if (string.equals("load")) {
	    if (collection.size() == 1) {
		Object object = collection.iterator().next();
		if (object instanceof String)
		    loading = new WildCard((String) object);
		else
		    loading = (IdentifierMatcher) object;
	    } else {
		IdentifierMatcher[] identifiermatchers
		    = new IdentifierMatcher[collection.size()];
		int i = 0;
		Iterator iterator = collection.iterator();
		while (iterator.hasNext()) {
		    Object object = iterator.next();
		    identifiermatchers[i++]
			= (object instanceof String
			   ? (IdentifierMatcher) new WildCard((String) object)
			   : (IdentifierMatcher) object);
		}
		loading = new MultiIdentifierMatcher(MultiIdentifierMatcher.OR,
						     identifiermatchers);
	    }
	} else if (string.equals("preserve")) {
	    if (collection.size() == 1) {
		Object object = collection.iterator().next();
		if (object instanceof String)
		    preserving = new WildCard((String) object);
		else
		    preserving = (IdentifierMatcher) object;
	    } else {
		IdentifierMatcher[] identifiermatchers
		    = new IdentifierMatcher[collection.size()];
		int i = 0;
		Iterator iterator = collection.iterator();
		while (iterator.hasNext()) {
		    Object object = iterator.next();
		    identifiermatchers[i++]
			= (object instanceof String
			   ? (IdentifierMatcher) new WildCard((String) object)
			   : (IdentifierMatcher) object);
		}
		preserving
		    = new MultiIdentifierMatcher(MultiIdentifierMatcher.OR,
						 identifiermatchers);
	    }
	} else {
	    if (string.equals("reach")) {
		if (collection.size() == 1) {
		    Object object = collection.iterator().next();
		    if (object instanceof String)
			reaching = new WildCard((String) object);
		    else
			reaching = (IdentifierMatcher) object;
		} else {
		    IdentifierMatcher[] identifiermatchers
			= new IdentifierMatcher[collection.size()];
		    int i = 0;
		    Iterator iterator = collection.iterator();
		    while (iterator.hasNext()) {
			Object object = iterator.next();
			identifiermatchers[i++]
			    = (object instanceof String
			       ? (IdentifierMatcher) new WildCard((String)
								  object)
			       : (IdentifierMatcher) object);
		    }
		    reaching
			= new MultiIdentifierMatcher(MultiIdentifierMatcher.OR,
						     identifiermatchers);
		}
	    }
	    if (string.equals("pre"))
		preTrafos = ((CodeTransformer[])
			     collection.toArray(new CodeTransformer
						[collection.size()]));
	    else if (string.equals("analyzer")) {
		if (collection.size() != 1)
		    throw new IllegalArgumentException
			      ("Only one analyzer is allowed");
		analyzer = (CodeAnalyzer) collection.iterator().next();
	    } else if (string.equals("post"))
		postTrafos = ((CodeTransformer[])
			      collection.toArray(new CodeTransformer
						 [collection.size()]));
	    else if (string.equals("renamer")) {
		if (collection.size() != 1)
		    throw new IllegalArgumentException
			      ("Only one renamer allowed");
		renamer = (Renamer) collection.iterator().next();
	    } else
		throw new IllegalArgumentException("Invalid option `" + string
						   + "'.");
	}
    }
    
    public Reference getReferenceAlias(Reference reference) {
	Reference reference_1_ = (Reference) aliasesHash.get(reference);
	if (reference_1_ == null) {
	    Identifier identifier = getIdentifier(reference);
	    String string = getTypeAlias(reference.getType());
	    if (identifier == null)
		reference_1_
		    = Reference.getReference(reference.getClazz(),
					     reference.getName(), string);
	    else
		reference_1_
		    = Reference.getReference(("L"
					      + identifier.getParent()
						    .getFullAlias
						    ().replace('.', '/')
					      + ';'),
					     identifier.getAlias(), string);
	    aliasesHash.put(reference, reference_1_);
	}
	return reference_1_;
    }
    
    public String getClassAlias(String string) {
	ClassIdentifier classidentifier = getClassIdentifier(string);
	if (classidentifier == null)
	    return string;
	return classidentifier.getFullAlias();
    }
    
    public String getTypeAlias(String string) {
	String string_2_ = (String) aliasesHash.get(string);
	if (string_2_ == null) {
	    StringBuffer stringbuffer = new StringBuffer();
	    int i = 0;
	    int i_3_;
	    while ((i_3_ = string.indexOf('L', i)) != -1) {
		stringbuffer.append(string.substring(i, i_3_ + 1));
		i = string.indexOf(';', i_3_);
		String string_4_ = getClassAlias(string.substring
						     (i_3_ + 1, i)
						     .replace('/', '.'));
		stringbuffer.append(string_4_.replace('.', '/'));
	    }
	    string_2_
		= stringbuffer.append(string.substring(i)).toString().intern();
	    aliasesHash.put(string, string_2_);
	}
	return string_2_;
    }
    
    public void addClassIdentifier(Identifier identifier) {
	/* empty */
    }
    
    public ClassIdentifier getClassIdentifier(String string) {
	if (clazzCache.containsKey(string))
	    return (ClassIdentifier) clazzCache.get(string);
	ClassIdentifier classidentifier
	    = (ClassIdentifier) basePackage.getIdentifier(string);
	clazzCache.put(string, classidentifier);
	return classidentifier;
    }
    
    public Identifier getIdentifier(Reference reference) {
	if (referenceCache.containsKey(reference))
	    return (Identifier) referenceCache.get(reference);
	String string = reference.getClazz();
	if (string.charAt(0) == '[')
	    return null;
	ClassIdentifier classidentifier
	    = getClassIdentifier(string.substring(1, string.length() - 1)
				     .replace('/', '.'));
	Identifier identifier
	    = (classidentifier == null ? null
	       : classidentifier.getIdentifier(reference.getName(),
					       reference.getType()));
	referenceCache.put(reference, identifier);
	return identifier;
    }
    
    public void reachableClass(String string) {
	ClassIdentifier classidentifier = getClassIdentifier(string);
	if (classidentifier != null)
	    classidentifier.setReachable();
    }
    
    public void reachableReference(Reference reference, boolean bool) {
	String string = reference.getClazz();
	if (string.charAt(0) != '[') {
	    ClassIdentifier classidentifier
		= getClassIdentifier(string.substring
					 (1, string.length() - 1)
					 .replace('/', '.'));
	    if (classidentifier != null)
		classidentifier.reachableReference(reference, bool);
	}
    }
    
    public void analyzeIdentifier(Identifier identifier) {
	if (identifier == null)
	    throw new NullPointerException();
	toAnalyze.add(identifier);
    }
    
    public void analyze() {
	while (!toAnalyze.isEmpty()) {
	    Identifier identifier = (Identifier) toAnalyze.iterator().next();
	    toAnalyze.remove(identifier);
	    identifier.analyze();
	}
    }
    
    public IdentifierMatcher getPreserveRule() {
	return preserving;
    }
    
    public CodeAnalyzer getCodeAnalyzer() {
	return analyzer;
    }
    
    public CodeTransformer[] getPreTransformers() {
	return preTrafos;
    }
    
    public CodeTransformer[] getPostTransformers() {
	return postTrafos;
    }
    
    public void buildTable(Renamer renamer) {
	basePackage.buildTable(renamer);
    }
    
    public void readTable() {
	try {
	    TranslationTable translationtable = new TranslationTable();
	    FileInputStream fileinputstream = new FileInputStream(inTableFile);
	    translationtable.load(fileinputstream);
	    fileinputstream.close();
	    basePackage.readTable(translationtable);
	} catch (IOException ioexception) {
	    GlobalOptions.err
		.println("Can't read rename table " + inTableFile);
	    ioexception.printStackTrace(GlobalOptions.err);
	}
    }
    
    public void writeTable() {
	TranslationTable translationtable = new TranslationTable();
	basePackage.writeTable(translationtable, false);
	try {
	    FileOutputStream fileoutputstream
		= new FileOutputStream(outTableFile);
	    translationtable.store(fileoutputstream);
	    fileoutputstream.close();
	} catch (IOException ioexception) {
	    GlobalOptions.err
		.println("Can't write rename table " + outTableFile);
	    ioexception.printStackTrace(GlobalOptions.err);
	}
    }
    
    public void writeRevTable() {
	TranslationTable translationtable = new TranslationTable();
	basePackage.writeTable(translationtable, true);
	try {
	    FileOutputStream fileoutputstream
		= new FileOutputStream(outRevTableFile);
	    translationtable.store(fileoutputstream);
	    fileoutputstream.close();
	} catch (IOException ioexception) {
	    GlobalOptions.err
		.println("Can't write rename table " + outRevTableFile);
	    ioexception.printStackTrace(GlobalOptions.err);
	}
    }
    
    public void doTransformations() {
	basePackage.doTransformations();
    }
    
    public void storeClasses() {
	if (destDir.endsWith(".jar") || destDir.endsWith(".zip")) {
	    try {
		ZipOutputStream zipoutputstream
		    = new ZipOutputStream(new FileOutputStream(destDir));
		basePackage.storeClasses(zipoutputstream);
		zipoutputstream.close();
	    } catch (IOException ioexception) {
		GlobalOptions.err.println("Can't write zip file: " + destDir);
		ioexception.printStackTrace(GlobalOptions.err);
	    }
	} else {
	    File file = new File(destDir);
	    if (!file.exists())
		GlobalOptions.err.println("Destination directory "
					  + file.getPath()
					  + " doesn't exists.");
	    else
		basePackage.storeClasses(new File(destDir));
	}
    }
    
    public void run() {
	if (analyzer == null)
	    analyzer = new SimpleAnalyzer();
	if (preTrafos == null)
	    preTrafos = new CodeTransformer[0];
	if (postTrafos == null)
	    postTrafos = new CodeTransformer[0];
	if (renamer == null)
	    renamer = new Renamer() {
		public Iterator generateNames(Identifier identifier) {
		    final String base = identifier.getName();
		    return new Iterator() {
			int last = 0;
			
			public boolean hasNext() {
			    return true;
			}
			
			public Object next() {
			    return last++ == 0 ? base : base + last;
			}
			
			public void remove() {
			    throw new UnsupportedOperationException();
			}
		    };
		}
	    };
	Runtime runtime = Runtime.getRuntime();
	long l = runtime.freeMemory();
	long l_7_;
	do {
	    l_7_ = l;
	    runtime.gc();
	    runtime.runFinalization();
	    l = runtime.freeMemory();
	} while (l < l_7_);
	System.err.println("used before: " + (runtime.totalMemory() - l));
	GlobalOptions.err.println("Loading and preserving classes");
	long l_8_ = System.currentTimeMillis();
	basePackage.loadMatchingClasses(loading);
	basePackage.applyPreserveRule(preserving);
	System.err.println("Time used: " + (System.currentTimeMillis()
					    - l_8_));
	GlobalOptions.err.println("Computing reachability");
	l_8_ = System.currentTimeMillis();
	analyze();
	System.err.println("Time used: " + (System.currentTimeMillis()
					    - l_8_));
	l = runtime.freeMemory();
	do {
	    l_7_ = l;
	    runtime.gc();
	    runtime.runFinalization();
	    l = runtime.freeMemory();
	} while (l < l_7_);
	System.err.println("used after analyze: " + (runtime.totalMemory()
						     - l));
	GlobalOptions.err.println("Renaming methods");
	l_8_ = System.currentTimeMillis();
	if (inTableFile != null)
	    readTable();
	buildTable(renamer);
	if (outTableFile != null)
	    writeTable();
	if (outRevTableFile != null)
	    writeRevTable();
	System.err.println("Time used: " + (System.currentTimeMillis()
					    - l_8_));
	GlobalOptions.err.println("Transforming the classes");
	l_8_ = System.currentTimeMillis();
	doTransformations();
	System.err.println("Time used: " + (System.currentTimeMillis()
					    - l_8_));
	l = runtime.freeMemory();
	do {
	    l_7_ = l;
	    runtime.gc();
	    runtime.runFinalization();
	    l = runtime.freeMemory();
	} while (l < l_7_);
	System.err.println("used after transform: " + (runtime.totalMemory()
						       - l));
	GlobalOptions.err.println("Writing new classes");
	l_8_ = System.currentTimeMillis();
	storeClasses();
	System.err.println("Time used: " + (System.currentTimeMillis()
					    - l_8_));
    }
}
