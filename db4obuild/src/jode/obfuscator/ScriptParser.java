/* ScriptParser - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.obfuscator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

public class ScriptParser
{
    static int NO_TOKEN = -2;
    static int EOF_TOKEN = -1;
    static int STRING_TOKEN = 0;
    static int NEW_TOKEN = 1;
    static int EQUALS_TOKEN = 2;
    static int COMMA_TOKEN = 3;
    static int OPENBRACE_TOKEN = 4;
    static int CLOSEBRACE_TOKEN = 5;
    static int IDENTIFIER_TOKEN = 6;
    static int NUMBER_TOKEN = 7;
    Scanner scanner;
    
    class Scanner
    {
	BufferedReader input;
	String value;
	String line;
	int column;
	int linenr;
	int pushback = ScriptParser.NO_TOKEN;
	
	public Scanner(Reader reader) {
	    input = new BufferedReader(reader);
	}
	
	public void readString() throws ParseException {
	    StringBuffer stringbuffer = new StringBuffer();
	    while (column < line.length()) {
		char c = line.charAt(column++);
		if (c == '\"') {
		    value = stringbuffer.toString();
		    return;
		}
		if (c == '\\') {
		    c = line.charAt(column++);
		    switch (c) {
		    case 'n':
			stringbuffer.append('\n');
			break;
		    case 't':
			stringbuffer.append('\t');
			break;
		    case 'r':
			stringbuffer.append('\r');
			break;
		    case 'u':
			if (column + 4 <= line.length()) {
			    try {
				char c_0_
				    = (char) (Integer.parseInt
					      (line.substring(column,
							      column + 4),
					       16));
				column += 4;
				stringbuffer.append(c_0_);
			    } catch (NumberFormatException numberformatexception) {
				throw new ParseException
					  (linenr,
					   "Invalid unicode escape character");
			    }
			} else
			    throw new ParseException
				      (linenr,
				       "Invalid unicode escape character");
			break;
		    default:
			stringbuffer.append(c);
		    }
		} else
		    stringbuffer.append(c);
	    }
	    throw new ParseException(linenr,
				     "String spans over multiple lines");
	}
	
	public void readIdentifier() {
	    int i = column - 1;
	    for (/**/;
		 (column < line.length()
		  && Character.isUnicodeIdentifierPart(line.charAt(column)));
		 column++) {
		/* empty */
	    }
	    value = line.substring(i, column);
	}
	
	public void readNumber() {
	    boolean bool = false;
	    int i = column - 1;
	    if (line.charAt(i) == '0' && line.charAt(column) == 'x') {
		column++;
		bool = true;
	    }
	    for (/**/; column < line.length(); column++) {
		char c = line.charAt(column);
		if (!Character.isDigit(c)
		    && (!bool || (c < 'A' || c > 'F') && (c < 'a' || c > 'f')))
		    break;
	    }
	    value = line.substring(i, column);
	}
	
	public void pushbackToken(int i) {
	    if (pushback != ScriptParser.NO_TOKEN)
		throw new IllegalStateException
			  ("Can only handle one pushback");
	    pushback = i;
	}
	
	public int getToken() throws ParseException, IOException {
	    if (pushback != ScriptParser.NO_TOKEN) {
		int i = pushback;
		pushback = ScriptParser.NO_TOKEN;
		return i;
	    }
	    value = null;
	    for (;;) {
		if (line == null) {
		    line = input.readLine();
		    if (line == null)
			return ScriptParser.EOF_TOKEN;
		    linenr++;
		    column = 0;
		}
		while (column < line.length()) {
		    char c = line.charAt(column++);
		    if (!Character.isWhitespace(c)) {
			if (c != '#') {
			    if (c == '=')
				return ScriptParser.EQUALS_TOKEN;
			    if (c == ',')
				return ScriptParser.COMMA_TOKEN;
			    if (c == '{')
				return ScriptParser.OPENBRACE_TOKEN;
			    if (c == '}')
				return ScriptParser.CLOSEBRACE_TOKEN;
			    if (c == '\"') {
				readString();
				return ScriptParser.STRING_TOKEN;
			    }
			    if (Character.isDigit(c) || c == '+' || c == '-') {
				readNumber();
				return ScriptParser.NUMBER_TOKEN;
			    }
			    if (Character.isUnicodeIdentifierStart(c)) {
				readIdentifier();
				if (value.equals("new"))
				    return ScriptParser.NEW_TOKEN;
				return ScriptParser.IDENTIFIER_TOKEN;
			    }
			    throw new ParseException(linenr,
						     ("Illegal character `" + c
						      + "'"));
			}
			break;
		    }
		}
		line = null;
	    }
	}
	
	public String getValue() {
	    return value;
	}
	
	public int getLineNr() {
	    return linenr;
	}
    }
    
    public ScriptParser(Reader reader) {
	scanner = new Scanner(reader);
    }
    
    public Object parseClass() throws ParseException, IOException {
	int i = scanner.getLineNr();
	int i_1_ = scanner.getToken();
	if (i_1_ != IDENTIFIER_TOKEN)
	    throw new ParseException(i, "Class name expected");
	Object object;
	try {
	    Class var_class = Class.forName("jode.obfuscator.modules."
					    + scanner.getValue());
	    object = var_class.newInstance();
	} catch (ClassNotFoundException classnotfoundexception) {
	    throw new ParseException(scanner.getLineNr(),
				     ("Class `" + scanner.getValue()
				      + "' not found"));
	} catch (Exception exception) {
	    throw new ParseException(scanner.getLineNr(),
				     ("Class `" + scanner.getValue()
				      + "' not valid: "
				      + exception.getMessage()));
	}
	i_1_ = scanner.getToken();
	if (i_1_ == OPENBRACE_TOKEN) {
	    if (!(object instanceof OptionHandler))
		throw new ParseException(scanner.getLineNr(),
					 ("Class `"
					  + object.getClass().getName()
					  + "' doesn't handle options."));
	    parseOptions((OptionHandler) object);
	    if (scanner.getToken() != CLOSEBRACE_TOKEN)
		throw new ParseException(scanner.getLineNr(), "`}' expected");
	} else
	    scanner.pushbackToken(i_1_);
	return object;
    }
    
    public void parseOptions(OptionHandler optionhandler)
	throws ParseException, IOException {
	int i = scanner.getToken();
	for (;;) {
	    if (i == EOF_TOKEN || i == CLOSEBRACE_TOKEN) {
		scanner.pushbackToken(i);
		break;
	    }
	    if (i != IDENTIFIER_TOKEN)
		throw new ParseException(scanner.getLineNr(),
					 "identifier expected");
	    String string = scanner.getValue();
	    if (scanner.getToken() != EQUALS_TOKEN)
		throw new ParseException(scanner.getLineNr(),
					 "equal sign expected");
	    int i_2_ = scanner.getLineNr();
	    LinkedList linkedlist = new LinkedList();
	    do {
		i = scanner.getToken();
		if (i == NEW_TOKEN)
		    linkedlist.add(parseClass());
		else if (i == STRING_TOKEN)
		    linkedlist.add(scanner.getValue());
		else if (i == NUMBER_TOKEN)
		    linkedlist.add(new Integer(scanner.getValue()));
		i = scanner.getToken();
	    } while (i == COMMA_TOKEN);
	    try {
		optionhandler.setOption(string, linkedlist);
	    } catch (IllegalArgumentException illegalargumentexception) {
		throw new ParseException(i_2_,
					 (optionhandler.getClass().getName()
					  + ": "
					  + illegalargumentexception
						.getMessage()));
	    } catch (RuntimeException runtimeexception) {
		throw new ParseException(i_2_,
					 (optionhandler.getClass().getName()
					  + ": Illegal value: "
					  + runtimeexception.getClass()
						.getName()
					  + ": "
					  + runtimeexception.getMessage()));
	    }
	}
    }
}
