/* Getopt - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package gnu.getopt;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Getopt
{
    protected static final int REQUIRE_ORDER = 1;
    protected static final int PERMUTE = 2;
    protected static final int RETURN_IN_ORDER = 3;
    protected String optarg;
    protected int optind;
    protected boolean opterr = true;
    protected int optopt = 63;
    protected String nextchar;
    protected String optstring;
    protected LongOpt[] long_options;
    protected boolean long_only;
    protected int longind;
    protected boolean posixly_correct;
    protected boolean longopt_handled;
    protected int first_nonopt = 1;
    protected int last_nonopt = 1;
    protected String[] argv;
    protected int ordering;
    protected String progname;
    private ResourceBundle _messages
	= ResourceBundle.getBundle("gnu/getopt/MessagesBundle",
				   Locale.getDefault());
    
    public Getopt(String string, String[] strings, String string_0_) {
	this(string, strings, string_0_, null, false);
    }
    
    public Getopt(String string, String[] strings, String string_1_,
		  LongOpt[] longopts) {
	this(string, strings, string_1_, longopts, false);
    }
    
    public Getopt(String string, String[] strings, String string_2_,
		  LongOpt[] longopts, boolean bool) {
	if (string_2_.length() == 0)
	    string_2_ = " ";
	progname = string;
	argv = strings;
	optstring = string_2_;
	long_options = longopts;
	long_only = bool;
	if (System.getProperty("gnu.posixly_correct", null) == null)
	    posixly_correct = false;
	else {
	    posixly_correct = true;
	    _messages = ResourceBundle.getBundle("gnu/getopt/MessagesBundle",
						 Locale.US);
	}
	if (string_2_.charAt(0) == '-') {
	    ordering = 3;
	    if (string_2_.length() > 1)
		optstring = string_2_.substring(1);
	} else if (string_2_.charAt(0) == '+') {
	    ordering = 1;
	    if (string_2_.length() > 1)
		optstring = string_2_.substring(1);
	} else if (posixly_correct)
	    ordering = 1;
	else
	    ordering = 2;
    }
    
    public void setOptstring(String string) {
	if (string.length() == 0)
	    string = " ";
	optstring = string;
    }
    
    public int getOptind() {
	return optind;
    }
    
    public void setOptind(int i) {
	optind = i;
    }
    
    public void setArgv(String[] strings) {
	argv = strings;
    }
    
    public String getOptarg() {
	return optarg;
    }
    
    public void setOpterr(boolean bool) {
	opterr = bool;
    }
    
    public int getOptopt() {
	return optopt;
    }
    
    public int getLongind() {
	return longind;
    }
    
    protected void exchange(String[] strings) {
	int i = first_nonopt;
	int i_3_ = last_nonopt;
	int i_4_ = optind;
	while (i_4_ > i_3_ && i_3_ > i) {
	    if (i_4_ - i_3_ > i_3_ - i) {
		int i_5_ = i_3_ - i;
		for (int i_6_ = 0; i_6_ < i_5_; i_6_++) {
		    String string = strings[i + i_6_];
		    strings[i + i_6_] = strings[i_4_ - (i_3_ - i) + i_6_];
		    strings[i_4_ - (i_3_ - i) + i_6_] = string;
		}
		i_4_ -= i_5_;
	    } else {
		int i_7_ = i_4_ - i_3_;
		for (int i_8_ = 0; i_8_ < i_7_; i_8_++) {
		    String string = strings[i + i_8_];
		    strings[i + i_8_] = strings[i_3_ + i_8_];
		    strings[i_3_ + i_8_] = string;
		}
		i += i_7_;
	    }
	}
	first_nonopt += optind - last_nonopt;
	last_nonopt = optind;
    }
    
    protected int checkLongOption() {
	LongOpt longopt = null;
	longopt_handled = true;
	boolean bool = false;
	boolean bool_9_ = false;
	longind = -1;
	int i = nextchar.indexOf("=");
	if (i == -1)
	    i = nextchar.length();
	for (int i_10_ = 0; i_10_ < long_options.length; i_10_++) {
	    if (long_options[i_10_].getName()
		    .startsWith(nextchar.substring(0, i))) {
		if (long_options[i_10_].getName()
			.equals(nextchar.substring(0, i))) {
		    longopt = long_options[i_10_];
		    longind = i_10_;
		    bool_9_ = true;
		    break;
		}
		if (longopt == null) {
		    longopt = long_options[i_10_];
		    longind = i_10_;
		} else
		    bool = true;
	    }
	}
	if (bool && !bool_9_) {
	    if (opterr) {
		Object[] objects = { progname, argv[optind] };
		System.err.println(MessageFormat.format
				   (_messages.getString("getopt.ambigious"),
				    objects));
	    }
	    nextchar = "";
	    optopt = 0;
	    optind++;
	    return 63;
	}
	if (longopt != null) {
	    optind++;
	    if (i != nextchar.length()) {
		if (longopt.has_arg != 0) {
		    if (nextchar.substring(i).length() > 1)
			optarg = nextchar.substring(i + 1);
		    else
			optarg = "";
		} else {
		    if (opterr) {
			if (argv[optind - 1].startsWith("--")) {
			    Object[] objects = { progname, longopt.name };
			    System.err.println
				(MessageFormat.format
				 (_messages.getString("getopt.arguments1"),
				  objects));
			} else {
			    Object[] objects
				= { progname,
				    new Character
					(argv[optind - 1].charAt(0))
					.toString(),
				    longopt.name };
			    System.err.println
				(MessageFormat.format
				 (_messages.getString("getopt.arguments2"),
				  objects));
			}
		    }
		    nextchar = "";
		    optopt = longopt.val;
		    return 63;
		}
	    } else if (longopt.has_arg == 1) {
		if (optind < argv.length) {
		    optarg = argv[optind];
		    optind++;
		} else {
		    if (opterr) {
			Object[] objects = { progname, argv[optind - 1] };
			System.err.println(MessageFormat.format
					   (_messages
						.getString("getopt.requires"),
					    objects));
		    }
		    nextchar = "";
		    optopt = longopt.val;
		    if (optstring.charAt(0) == ':')
			return 58;
		    return 63;
		}
	    }
	    nextchar = "";
	    if (longopt.flag != null) {
		longopt.flag.setLength(0);
		longopt.flag.append(longopt.val);
		return 0;
	    }
	    return longopt.val;
	}
	longopt_handled = false;
	return 0;
    }
    
    public int getopt() {
	optarg = null;
	if (nextchar == null || nextchar.equals("")) {
	    if (last_nonopt > optind)
		last_nonopt = optind;
	    if (first_nonopt > optind)
		first_nonopt = optind;
	    if (ordering == 2) {
		if (first_nonopt != last_nonopt && last_nonopt != optind)
		    exchange(argv);
		else if (last_nonopt != optind)
		    first_nonopt = optind;
		for (/**/;
		     optind < argv.length && (argv[optind].equals("")
					      || argv[optind].charAt(0) != '-'
					      || argv[optind].equals("-"));
		     optind++) {
		    /* empty */
		}
		last_nonopt = optind;
	    }
	    if (optind != argv.length && argv[optind].equals("--")) {
		optind++;
		if (first_nonopt != last_nonopt && last_nonopt != optind)
		    exchange(argv);
		else if (first_nonopt == last_nonopt)
		    first_nonopt = optind;
		last_nonopt = argv.length;
		optind = argv.length;
	    }
	    if (optind == argv.length) {
		if (first_nonopt != last_nonopt)
		    optind = first_nonopt;
		return -1;
	    }
	    if (argv[optind].equals("") || argv[optind].charAt(0) != '-'
		|| argv[optind].equals("-")) {
		if (ordering == 1)
		    return -1;
		optarg = argv[optind++];
		return 1;
	    }
	    if (argv[optind].startsWith("--"))
		nextchar = argv[optind].substring(2);
	    else
		nextchar = argv[optind].substring(1);
	}
	if (long_options != null
	    && (argv[optind].startsWith("--")
		|| long_only && (argv[optind].length() > 2
				 || (optstring.indexOf(argv[optind].charAt(1))
				     == -1)))) {
	    int i = checkLongOption();
	    if (longopt_handled)
		return i;
	    if (!long_only || argv[optind].startsWith("--")
		|| optstring.indexOf(nextchar.charAt(0)) == -1) {
		if (opterr) {
		    if (argv[optind].startsWith("--")) {
			Object[] objects = { progname, nextchar };
			System.err.println
			    (MessageFormat.format
			     (_messages.getString("getopt.unrecognized"),
			      objects));
		    } else {
			Object[] objects
			    = { progname, new Character
					      (argv[optind].charAt(0))
					      .toString(), nextchar };
			System.err.println
			    (MessageFormat.format
			     (_messages.getString("getopt.unrecognized2"),
			      objects));
		    }
		}
		nextchar = "";
		optind++;
		optopt = 0;
		return 63;
	    }
	}
	int i = nextchar.charAt(0);
	if (nextchar.length() > 1)
	    nextchar = nextchar.substring(1);
	else
	    nextchar = "";
	String string = null;
	if (optstring.indexOf(i) != -1)
	    string = optstring.substring(optstring.indexOf(i));
	if (nextchar.equals(""))
	    optind++;
	if (string == null || i == 58) {
	    if (opterr) {
		if (posixly_correct) {
		    Object[] objects
			= { progname, new Character((char) i).toString() };
		    System.err.println(MessageFormat.format
				       (_messages.getString("getopt.illegal"),
					objects));
		} else {
		    Object[] objects
			= { progname, new Character((char) i).toString() };
		    System.err.println(MessageFormat.format
				       (_messages.getString("getopt.invalid"),
					objects));
		}
	    }
	    optopt = i;
	    return 63;
	}
	if (string.charAt(0) == 'W' && string.length() > 1
	    && string.charAt(1) == ';') {
	    if (!nextchar.equals(""))
		optarg = nextchar;
	    else {
		if (optind == argv.length) {
		    if (opterr) {
			Object[] objects
			    = { progname, new Character((char) i).toString() };
			System.err.println(MessageFormat.format
					   (_messages
						.getString("getopt.requires2"),
					    objects));
		    }
		    optopt = i;
		    if (optstring.charAt(0) == ':')
			return 58;
		    return 63;
		}
		nextchar = argv[optind];
		optarg = argv[optind];
	    }
	    i = checkLongOption();
	    if (longopt_handled)
		return i;
	    nextchar = null;
	    optind++;
	    return 87;
	}
	if (string.length() > 1 && string.charAt(1) == ':') {
	    if (string.length() > 2 && string.charAt(2) == ':') {
		if (!nextchar.equals("")) {
		    optarg = nextchar;
		    optind++;
		} else
		    optarg = null;
		nextchar = null;
	    } else {
		if (!nextchar.equals("")) {
		    optarg = nextchar;
		    optind++;
		} else {
		    if (optind == argv.length) {
			if (opterr) {
			    Object[] objects
				= { progname,
				    new Character((char) i).toString() };
			    System.err.println
				(MessageFormat.format
				 (_messages.getString("getopt.requires2"),
				  objects));
			}
			optopt = i;
			if (optstring.charAt(0) == ':')
			    return 58;
			return 63;
		    }
		    optarg = argv[optind];
		    optind++;
		}
		nextchar = null;
	    }
	}
	return i;
    }
}
