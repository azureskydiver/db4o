/* Main - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import jode.GlobalOptions;
import jode.bytecode.ClassInfo;

public class Main extends Options
{
    private static final int OPTION_START = 65536;
    private static final int OPTION_END = 131072;
    private static final LongOpt[] longOptions
	= { new LongOpt("cp", 1, null, 99),
	    new LongOpt("classpath", 1, null, 99),
	    new LongOpt("dest", 1, null, 100),
	    new LongOpt("help", 0, null, 104),
	    new LongOpt("version", 0, null, 86),
	    new LongOpt("verbose", 2, null, 118),
	    new LongOpt("debug", 2, null, 68),
	    new LongOpt("import", 1, null, 105),
	    new LongOpt("style", 1, null, 115),
	    new LongOpt("lvt", 2, null, 65536),
	    new LongOpt("inner", 2, null, 65537),
	    new LongOpt("anonymous", 2, null, 65538),
	    new LongOpt("push", 2, null, 65539),
	    new LongOpt("pretty", 2, null, 65540),
	    new LongOpt("decrypt", 2, null, 65541),
	    new LongOpt("onetime", 2, null, 65542),
	    new LongOpt("immediate", 2, null, 65543),
	    new LongOpt("verify", 2, null, 65544),
	    new LongOpt("contrafo", 2, null, 65545) };
    
    public static void usage() {
	PrintWriter printwriter = GlobalOptions.err;
	printwriter.println("Version: 1.1.1");
	printwriter.println
	    ("Usage: java jode.decompiler.Main [OPTION]* {CLASS|JAR}*");
	printwriter.println
	    ("Give a fully qualified CLASS name, e.g. jode.decompiler.Main, if you want to");
	printwriter.println
	    ("decompile a single class, or a JAR file containing many classes.");
	printwriter.println("OPTION is any of these:");
	printwriter.println("  -h, --help           show this information.");
	printwriter.println
	    ("  -V, --version        output version information and exit.");
	printwriter.println
	    ("  -v, --verbose        be verbose (multiple times means more verbose).");
	printwriter.println
	    ("  -c, --classpath <path> search for classes in specified classpath.");
	printwriter.println
	    ("                       The directories should be separated by ','.");
	printwriter.println
	    ("  -d, --dest <dir>     write decompiled files to disk into directory destdir.");
	printwriter
	    .println("  -s, --style {sun|gnu}  specify indentation style");
	printwriter.println("  -i, --import <pkglimit>,<clslimit>");
	printwriter.println
	    ("                       import classes used more than clslimit times");
	printwriter.println
	    ("                       and packages with more then pkglimit used classes.");
	printwriter.println
	    ("                       Limit 0 means never import. Default is 0,1.");
	printwriter.println
	    ("  -D, --debug=...      use --debug=help for more information.");
	printwriter.println
	    ("NOTE: The following options can be turned on or off with `yes' or `no'.");
	printwriter.println
	    ("The options tagged with (default) are normally on.  Omitting the yes/no");
	printwriter.println
	    ("argument will toggle the option, e.g. --verify is equivalent to --verify=no.");
	printwriter.println
	    ("      --inner          decompile inner classes (default).");
	printwriter.println
	    ("      --anonymous      decompile anonymous classes (default).");
	printwriter.println
	    ("      --contrafo       transform constructors of inner classes (default).");
	printwriter.println
	    ("      --lvt            use the local variable table (default).");
	printwriter.println
	    ("      --pretty         use `pretty' names for local variables (default).");
	printwriter.println
	    ("      --push           allow PUSH instructions in output.");
	printwriter.println
	    ("      --decrypt        decrypt encrypted strings (default).");
	printwriter.println
	    ("      --onetime        remove locals, that are used only one time.");
	printwriter.println
	    ("      --immediate      output source immediately (may produce buggy code).");
	printwriter.println
	    ("      --verify         verify code before decompiling it (default).");
    }
    
    public static boolean handleOption(int i, int i_0_, String string) {
	if (string == null)
	    Options.options ^= 1 << i;
	else if ("yes".startsWith(string) || string.equals("on"))
	    Options.options |= 1 << i;
	else if ("no".startsWith(string) || string.equals("off"))
	    Options.options &= 1 << i ^ 0xffffffff;
	else {
	    GlobalOptions.err.println
		("jode.decompiler.Main: option --"
		 + longOptions[i_0_].getName()
		 + " takes one of `yes', `no', `on', `off' as parameter");
	    return false;
	}
	return true;
    }
    
    public static void decompileClass
	(String string, ZipOutputStream zipoutputstream, String string_1_,
	 TabbedPrintWriter tabbedprintwriter, ImportHandler importhandler) {
	try {
	    ClassInfo classinfo;
	    try {
		classinfo = ClassInfo.forName(string);
	    } catch (IllegalArgumentException illegalargumentexception) {
		GlobalOptions.err
		    .println("`" + string + "' is not a class name");
		return;
	    }
	    if (!Options.skipClass(classinfo)) {
		String string_2_
		    = string.replace('.', File.separatorChar) + ".java";
		if (zipoutputstream != null) {
		    tabbedprintwriter.flush();
		    zipoutputstream.putNextEntry(new ZipEntry(string_2_));
		} else if (string_1_ != null) {
		    File file = new File(string_1_, string_2_);
		    File file_3_ = new File(file.getParent());
		    if (!file_3_.exists() && !file_3_.mkdirs())
			GlobalOptions.err.println("Could not create directory "
						  + file_3_.getPath()
						  + ", check permissions.");
		    tabbedprintwriter
			= new TabbedPrintWriter((new BufferedOutputStream
						 (new FileOutputStream(file))),
						importhandler, false);
		}
		GlobalOptions.err.println(string);
		ClassAnalyzer classanalyzer
		    = new ClassAnalyzer(classinfo, importhandler);
		classanalyzer.dumpJavaFile(tabbedprintwriter);
		if (zipoutputstream != null) {
		    tabbedprintwriter.flush();
		    zipoutputstream.closeEntry();
		} else if (string_1_ != null)
		    tabbedprintwriter.close();
		System.gc();
	    }
	} catch (IOException ioexception) {
	    GlobalOptions.err.println("Can't write source of " + string + ".");
	    GlobalOptions.err.println("Check the permissions.");
	    ioexception.printStackTrace(GlobalOptions.err);
	}
    }
    
    public static void main(String[] strings) {
	try {
	    decompile(strings);
	} catch (ExceptionInInitializerError exceptionininitializererror) {
	    exceptionininitializererror.getException().printStackTrace();
	} catch (Throwable throwable) {
	    throwable.printStackTrace();
	}
    }
    
    public static void decompile(String[] strings) {
	if (strings.length == 0)
	    usage();
	else {
	    String string = System.getProperty("java.class.path")
				.replace(File.pathSeparatorChar, ',');
	    String string_4_ = System.getProperty("sun.boot.class.path");
	    if (string_4_ != null)
		string += ',' + string_4_.replace(File.pathSeparatorChar, ',');
	    String string_5_ = null;
	    int i = 2147483647;
	    int i_6_ = 1;
	    GlobalOptions.err.println
		("Jode (c) 1998-2001 Jochen Hoenicke <jochen@gnu.org>");
	    boolean bool = false;
	    Getopt getopt = new Getopt("jode.decompiler.Main", strings,
				       "hVvc:d:D:i:s:", longOptions, true);
	    for (int i_7_ = getopt.getopt(); i_7_ != -1;
		 i_7_ = getopt.getopt()) {
		switch (i_7_) {
		case 0:
		    break;
		case 104:
		    usage();
		    bool = true;
		    break;
		case 86:
		    GlobalOptions.err.println("1.1.1");
		    break;
		case 99:
		    string = getopt.getOptarg();
		    break;
		case 100:
		    string_5_ = getopt.getOptarg();
		    break;
		case 118: {
		    String string_8_ = getopt.getOptarg();
		    if (string_8_ == null)
			GlobalOptions.verboseLevel++;
		    else {
			try {
			    GlobalOptions.verboseLevel
				= Integer.parseInt(string_8_);
			} catch (NumberFormatException numberformatexception) {
			    GlobalOptions.err.println
				("jode.decompiler.Main: Argument `" + string_8_
				 + "' to --verbose must be numeric:");
			    bool = true;
			}
		    }
		    break;
		}
		case 68: {
		    String string_9_ = getopt.getOptarg();
		    if (string_9_ == null)
			string_9_ = "help";
		    bool = bool | !GlobalOptions.setDebugging(string_9_);
		    break;
		}
		case 115: {
		    String string_10_ = getopt.getOptarg();
		    if ("sun".startsWith(string_10_))
			Options.outputStyle = 20;
		    else if ("gnu".startsWith(string_10_))
			Options.outputStyle = 66;
		    else if ("pascal".startsWith(string_10_))
			Options.outputStyle = 36;
		    else {
			GlobalOptions.err.println
			    ("jode.decompiler.Main: Unknown style `"
			     + string_10_ + "'.");
			bool = true;
		    }
		    break;
		}
		case 105: {
		    String string_11_ = getopt.getOptarg();
		    int i_12_ = string_11_.indexOf(',');
		    try {
			int i_13_
			    = Integer.parseInt(string_11_.substring(0, i_12_));
			if (i_13_ == 0)
			    i_13_ = 2147483647;
			if (i_13_ < 0)
			    throw new IllegalArgumentException();
			int i_14_
			    = Integer
				  .parseInt(string_11_.substring(i_12_ + 1));
			if (i_14_ == 0)
			    i_14_ = 2147483647;
			if (i_14_ < 0)
			    throw new IllegalArgumentException();
			i = i_13_;
			i_6_ = i_14_;
		    } catch (RuntimeException runtimeexception) {
			GlobalOptions.err.println
			    ("jode.decompiler.Main: Invalid argument for -i option.");
			bool = true;
		    }
		    break;
		}
		default:
		    if (i_7_ >= 65536 && i_7_ <= 131072)
			bool = bool | !handleOption(i_7_ - 65536,
						    getopt.getLongind(),
						    getopt.getOptarg());
		    else
			bool = true;
		}
	    }
	    if (!bool) {
		ClassInfo.setClassPath(string);
		ImportHandler importhandler = new ImportHandler(i, i_6_);
		ZipOutputStream zipoutputstream = null;
		TabbedPrintWriter tabbedprintwriter = null;
		if (string_5_ == null)
		    tabbedprintwriter
			= new TabbedPrintWriter(System.out, importhandler);
		else if (string_5_.toLowerCase().endsWith(".zip")
			 || string_5_.toLowerCase().endsWith(".jar")) {
		    try {
			zipoutputstream = (new ZipOutputStream
					   (new FileOutputStream(string_5_)));
		    } catch (IOException ioexception) {
			GlobalOptions.err
			    .println("Can't open zip file " + string_5_);
			ioexception.printStackTrace(GlobalOptions.err);
			return;
		    }
		    tabbedprintwriter
			= (new TabbedPrintWriter
			   (new BufferedOutputStream(zipoutputstream),
			    importhandler, false));
		}
		for (int i_15_ = getopt.getOptind(); i_15_ < strings.length;
		     i_15_++) {
		    try {
			if ((strings[i_15_].endsWith(".jar")
			     || strings[i_15_].endsWith(".zip"))
			    && new File(strings[i_15_]).isFile()) {
			    ClassInfo
				.setClassPath(strings[i_15_] + ',' + string);
			    Enumeration enumeration
				= new ZipFile(strings[i_15_]).entries();
			    while (enumeration.hasMoreElements()) {
				String string_16_
				    = ((ZipEntry) enumeration.nextElement())
					  .getName();
				if (string_16_.endsWith(".class")) {
				    string_16_
					= string_16_.substring
					      (0, string_16_.length() - 6)
					      .replace('/', '.');
				    decompileClass(string_16_, zipoutputstream,
						   string_5_,
						   tabbedprintwriter,
						   importhandler);
				}
			    }
			    ClassInfo.setClassPath(string);
			} else
			    decompileClass(strings[i_15_], zipoutputstream,
					   string_5_, tabbedprintwriter,
					   importhandler);
		    } catch (IOException ioexception) {
			GlobalOptions.err.println("Can't read zip file "
						  + strings[i_15_] + ".");
			ioexception.printStackTrace(GlobalOptions.err);
		    }
		}
		if (zipoutputstream != null) {
		    try {
			zipoutputstream.close();
		    } catch (IOException ioexception) {
			GlobalOptions.err.println("Can't close Zipfile");
			ioexception.printStackTrace(GlobalOptions.err);
		    }
		}
	    }
	}
    }
}
