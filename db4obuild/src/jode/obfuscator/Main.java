/* Main - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Random;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import jode.GlobalOptions;

public class Main
{
    public static boolean swapOrder = false;
    public static final int OPTION_STRONGOVERLOAD = 1;
    public static final int OPTION_PRESERVESERIAL = 2;
    public static int options = 2;
    private static final LongOpt[] longOptions
	= { new LongOpt("cp", 1, null, 99),
	    new LongOpt("classpath", 1, null, 99),
	    new LongOpt("destpath", 1, null, 100),
	    new LongOpt("help", 0, null, 104),
	    new LongOpt("version", 0, null, 86),
	    new LongOpt("verbose", 2, null, 118),
	    new LongOpt("debug", 2, null, 68) };
    public static final String[] stripNames
	= { "unreach", "inner", "lvt", "lnt", "source" };
    public static final int STRIP_UNREACH = 1;
    public static final int STRIP_INNERINFO = 2;
    public static final int STRIP_LVT = 4;
    public static final int STRIP_LNT = 8;
    public static final int STRIP_SOURCE = 16;
    public static int stripping = 0;
    public static Random rand = new Random(123456L);
    private static ClassBundle bundle;
    
    public static void usage() {
	PrintWriter printwriter = GlobalOptions.err;
	printwriter.println("usage: jode.Obfuscator flags* script");
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
	printwriter.println
	    ("  -D, --debug=...      use --debug=help for more information.");
    }
    
    public static ClassBundle getClassBundle() {
	return bundle;
    }
    
    public static void main(String[] strings) {
	if (strings.length == 0)
	    usage();
	else {
	    String string = null;
	    String string_0_ = null;
	    GlobalOptions.err.println
		("Jode (c) 1998-2001 Jochen Hoenicke <jochen@gnu.org>");
	    bundle = new ClassBundle();
	    boolean bool = false;
	    Getopt getopt = new Getopt("jode.obfuscator.Main", strings,
				       "hVvc:d:D:", longOptions, true);
	    for (int i = getopt.getopt(); i != -1; i = getopt.getopt()) {
		switch (i) {
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
		    string_0_ = getopt.getOptarg();
		    break;
		case 118: {
		    String string_1_ = getopt.getOptarg();
		    if (string_1_ == null)
			GlobalOptions.verboseLevel++;
		    else {
			try {
			    GlobalOptions.verboseLevel
				= Integer.parseInt(string_1_);
			} catch (NumberFormatException numberformatexception) {
			    GlobalOptions.err.println
				("jode.obfuscator.Main: Argument `" + string_1_
				 + "' to --verbose must be numeric:");
			    bool = true;
			}
		    }
		    break;
		}
		case 68: {
		    String string_2_ = getopt.getOptarg();
		    if (string_2_ == null)
			string_2_ = "help";
		    bool = bool | !GlobalOptions.setDebugging(string_2_);
		    break;
		}
		default:
		    bool = true;
		}
	    }
	    if (!bool) {
		if (getopt.getOptind() != strings.length - 1)
		    GlobalOptions.err
			.println("You must specify exactly one script.");
		else {
		    try {
			String string_3_ = strings[getopt.getOptind()];
			ScriptParser scriptparser
			    = (new ScriptParser
			       (string_3_.equals("-")
				? (InputStreamReader) (new InputStreamReader
						       (System.in))
				: new FileReader(string_3_)));
			scriptparser.parseOptions(bundle);
		    } catch (IOException ioexception) {
			GlobalOptions.err
			    .println("IOException while reading script file.");
			ioexception.printStackTrace(GlobalOptions.err);
			return;
		    } catch (ParseException parseexception) {
			GlobalOptions.err
			    .println("Syntax error in script file: ");
			GlobalOptions.err.println(parseexception.getMessage());
			if (GlobalOptions.verboseLevel > 5)
			    parseexception.printStackTrace(GlobalOptions.err);
			return;
		    }
		    if (string != null)
			bundle.setOption("classpath",
					 Collections.singleton(string));
		    if (string_0_ != null)
			bundle.setOption("dest",
					 Collections.singleton(string_0_));
		    bundle.run();
		}
	    }
	}
    }
}
