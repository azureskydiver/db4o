/* LongOpt - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package gnu.getopt;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class LongOpt
{
    public static final int NO_ARGUMENT = 0;
    public static final int REQUIRED_ARGUMENT = 1;
    public static final int OPTIONAL_ARGUMENT = 2;
    protected String name;
    protected int has_arg;
    protected StringBuffer flag;
    protected int val;
    private ResourceBundle _messages
	= ResourceBundle.getBundle("gnu/getopt/MessagesBundle",
				   Locale.getDefault());
    
    public LongOpt
	(String string, int i, StringBuffer stringbuffer, int i_0_)
	throws IllegalArgumentException {
	if (i != 0 && i != 1 && i != 2) {
	    Object[] objects = { new Integer(i).toString() };
	    throw new IllegalArgumentException
		      (MessageFormat.format
		       (_messages.getString("getopt.invalidValue"), objects));
	}
	name = string;
	has_arg = i;
	flag = stringbuffer;
	val = i_0_;
    }
    
    public String getName() {
	return name;
    }
    
    public int getHasArg() {
	return has_arg;
    }
    
    public StringBuffer getFlag() {
	return flag;
    }
    
    public int getVal() {
	return val;
    }
}
