/* TabbedPrintWriter - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import jode.bytecode.ClassInfo;
import jode.bytecode.InnerClassInfo;
import jode.type.ArrayType;
import jode.type.ClassInterfacesType;
import jode.type.NullType;
import jode.type.Type;

public class TabbedPrintWriter
{
    private int indentsize;
    private int tabWidth;
    private int lineWidth;
    private int currentIndent = 0;
    private String indentStr = "";
    private PrintWriter pw;
    private ImportHandler imports;
    private Stack scopes;
    private StringBuffer currentLine;
    private BreakPoint currentBP;
    public static final int EXPL_PAREN = 0;
    public static final int NO_PAREN = 1;
    public static final int IMPL_PAREN = 2;
    public static final int DONT_BREAK = 3;
    
    class BreakPoint
    {
	int options;
	int breakPenalty;
	int breakPos;
	int startPos;
	BreakPoint parentBP;
	Vector childBPs;
	int nesting = 0;
	int endPos;
	int whatBreak = 0;
	
	public BreakPoint(BreakPoint breakpoint_0_, int i) {
	    breakPos = i;
	    parentBP = breakpoint_0_;
	    options = 3;
	    breakPenalty = 0;
	    startPos = -1;
	    endPos = -1;
	    whatBreak = 0;
	    childBPs = null;
	}
	
	public void startOp(int i, int i_1_, int i_2_) {
	    if (startPos != -1)
		throw new InternalError("missing breakOp");
	    startPos = i_2_;
	    options = i;
	    breakPenalty = i_1_;
	    childBPs = new Vector();
	    breakOp(i_2_);
	}
	
	public void breakOp(int i) {
	    childBPs.addElement(new BreakPoint(this, i));
	}
	
	public void endOp(int i) {
	    endPos = i;
	    if (childBPs.size() == 1) {
		BreakPoint breakpoint_3_ = (BreakPoint) childBPs.elementAt(0);
		options = Math.min(options, breakpoint_3_.options);
		startPos = breakpoint_3_.startPos;
		endPos = breakpoint_3_.endPos;
		breakPenalty = breakpoint_3_.breakPenalty;
		childBPs = breakpoint_3_.childBPs;
	    }
	}
	
	public void dump(String string) {
	    if (startPos == -1)
		pw.print(string);
	    else {
		pw.print(string.substring(0, startPos));
		dumpRegion(string);
		pw.print(string.substring(endPos));
	    }
	}
	
	public void dumpRegion(String string) {
	    String string_4_
		= "{\010{}\010}<\010<>\010>[\010[]\010]`\010`'\010'"
		      .substring(options * 6, options * 6 + 6);
	    pw.print(string_4_.substring(0, 3));
	    Enumeration enumeration = childBPs.elements();
	    int i = startPos;
	    BreakPoint breakpoint_5_ = (BreakPoint) enumeration.nextElement();
	    if (breakpoint_5_.startPos >= 0) {
		pw.print(string.substring(i, breakpoint_5_.startPos));
		breakpoint_5_.dumpRegion(string);
		i = breakpoint_5_.endPos;
	    }
	    while (enumeration.hasMoreElements()) {
		breakpoint_5_ = (BreakPoint) enumeration.nextElement();
		pw.print(string.substring(i, breakpoint_5_.breakPos));
		pw.print("!\010!" + breakPenalty);
		i = breakpoint_5_.breakPos;
		if (breakpoint_5_.startPos >= 0) {
		    pw.print(string.substring(breakpoint_5_.breakPos,
					      breakpoint_5_.startPos));
		    breakpoint_5_.dumpRegion(string);
		    i = breakpoint_5_.endPos;
		}
	    }
	    pw.print(string.substring(i, endPos));
	    pw.print(string_4_.substring(3));
	}
	
	public void printLines(int i, String string) {
	    if (startPos == -1)
		pw.print(string);
	    else {
		pw.print(string.substring(0, startPos));
		printRegion(i + startPos, string);
		pw.print(string.substring(endPos));
	    }
	}
	
	public void printRegion(int i, String string) {
	    if (options == 2) {
		pw.print("(");
		i++;
	    }
	    Enumeration enumeration = childBPs.elements();
	    int i_6_ = startPos;
	    BreakPoint breakpoint_7_ = (BreakPoint) enumeration.nextElement();
	    if (breakpoint_7_.startPos >= 0) {
		pw.print(string.substring(i_6_, breakpoint_7_.startPos));
		breakpoint_7_.printRegion(i + breakpoint_7_.startPos - i_6_,
					  string);
		i_6_ = breakpoint_7_.endPos;
	    }
	    if (options == 1)
		i += indentsize;
	    String string_8_ = makeIndentStr(i);
	    while (enumeration.hasMoreElements()) {
		breakpoint_7_ = (BreakPoint) enumeration.nextElement();
		pw.print(string.substring(i_6_, breakpoint_7_.breakPos));
		pw.println();
		pw.print(string_8_);
		i_6_ = breakpoint_7_.breakPos;
		if (i_6_ < endPos && string.charAt(i_6_) == ' ')
		    i_6_++;
		if (breakpoint_7_.startPos >= 0) {
		    pw.print(string.substring(i_6_, breakpoint_7_.startPos));
		    breakpoint_7_.printRegion((i + breakpoint_7_.startPos
					       - i_6_),
					      string);
		    i_6_ = breakpoint_7_.endPos;
		}
	    }
	    pw.print(string.substring(i_6_, endPos));
	    if (options == 2)
		pw.print(")");
	}
	
	public BreakPoint commitMinPenalty(int i, int i_9_, int i_10_) {
	    if (startPos == -1 || i_9_ > endPos - startPos
		|| i_10_ == 10 * (endPos - startPos - i_9_)) {
		startPos = -1;
		childBPs = null;
		return this;
	    }
	    int i_11_ = childBPs.size();
	    if (i_11_ > 1 && options != 3) {
		int i_12_ = getBreakPenalty(i, i_9_, i_10_ + 1);
		if (i_10_ == i_12_) {
		    commitBreakPenalty(i, i_9_, i_12_);
		    return this;
		}
	    }
	    for (int i_13_ = 0; i_13_ < i_11_; i_13_++) {
		BreakPoint breakpoint_14_
		    = (BreakPoint) childBPs.elementAt(i_13_);
		int i_15_ = breakpoint_14_.startPos - startPos;
		int i_16_ = endPos - breakpoint_14_.endPos;
		int i_17_ = i_10_ - (i_13_ < i_11_ - 1 ? 1 : 0);
		if (i_17_ == breakpoint_14_.getMinPenalty(i - i_15_,
							  i_9_ - i_15_ - i_16_,
							  i_17_ + 1)) {
		    breakpoint_14_
			= breakpoint_14_.commitMinPenalty(i - i_15_,
							  i_9_ - i_15_ - i_16_,
							  i_17_);
		    breakpoint_14_.breakPos = breakPos;
		    return breakpoint_14_;
		}
	    }
	    pw.println("XXXXXXXXXXX CAN'T COMMIT");
	    startPos = -1;
	    childBPs = null;
	    return this;
	}
	
	public int getMinPenalty(int i, int i_18_, int i_19_) {
	    if (10 * -i_18_ >= i_19_)
		return i_19_;
	    if (startPos == -1)
		return 10 * -i_18_;
	    if (i_18_ > endPos - startPos)
		return 0;
	    if (i_19_ <= 1)
		return i_19_;
	    if (i_19_ > 10 * (endPos - startPos - i_18_))
		i_19_ = 10 * (endPos - startPos - i_18_);
	    int i_20_ = childBPs.size();
	    if (i_20_ == 0)
		return i_19_;
	    if (i_20_ > 1 && options != 3)
		i_19_ = getBreakPenalty(i, i_18_, i_19_);
	    for (int i_21_ = 0; i_21_ < i_20_; i_21_++) {
		BreakPoint breakpoint_22_
		    = (BreakPoint) childBPs.elementAt(i_21_);
		int i_23_ = breakpoint_22_.startPos - startPos;
		int i_24_ = endPos - breakpoint_22_.endPos;
		int i_25_ = i_21_ < i_20_ - 1 ? 1 : 0;
		i_19_ = (i_25_
			 + breakpoint_22_.getMinPenalty(i - i_23_,
							i_18_ - i_23_ - i_24_,
							i_19_ - i_25_));
	    }
	    return i_19_;
	}
	
	public void commitBreakPenalty(int i, int i_26_, int i_27_) {
	    if (options == 2) {
		i--;
		i_26_ -= 2;
	    }
	    Enumeration enumeration = childBPs.elements();
	    childBPs = new Vector();
	    int i_28_ = 0;
	    boolean bool = options == 1;
	    BreakPoint breakpoint_29_ = (BreakPoint) enumeration.nextElement();
	    BreakPoint breakpoint_30_;
	    for (/**/; enumeration.hasMoreElements();
		 breakpoint_29_ = breakpoint_30_) {
		breakpoint_30_ = (BreakPoint) enumeration.nextElement();
		int i_31_ = breakpoint_29_.breakPos;
		int i_32_ = breakpoint_30_.breakPos;
		if (i_28_ > 0) {
		    i_28_ += i_32_ - i_31_;
		    if (i_28_ <= i)
			continue;
		}
		if (i_31_ < endPos && currentLine.charAt(i_31_) == ' ')
		    i_31_++;
		if (i_32_ - i_31_ > i) {
		    int i_33_ = breakpoint_29_.startPos - i_31_;
		    int i_34_ = i_32_ - breakpoint_29_.endPos;
		    int i_35_ = breakpoint_29_.getMinPenalty(i - i_33_,
							     i - i_33_ - i_34_,
							     i_27_);
		    i_28_ = 0;
		    childBPs.addElement(breakpoint_29_.commitMinPenalty
					(i - i_33_, i - i_33_ - i_34_, i_35_));
		} else {
		    breakpoint_29_.startPos = -1;
		    breakpoint_29_.childBPs = null;
		    childBPs.addElement(breakpoint_29_);
		    i_28_ = i_32_ - i_31_;
		}
		if (bool) {
		    i -= indentsize;
		    i_26_ -= indentsize;
		    bool = false;
		}
	    }
	    int i_36_ = breakpoint_29_.breakPos;
	    if (i_28_ <= 0 || i_28_ + endPos - i_36_ > i_26_) {
		if (i_36_ < endPos && currentLine.charAt(i_36_) == ' ')
		    i_36_++;
		if (endPos - i_36_ > i_26_) {
		    int i_37_ = breakpoint_29_.startPos - i_36_;
		    int i_38_ = endPos - breakpoint_29_.endPos;
		    int i_39_
			= breakpoint_29_.getMinPenalty(i - i_37_,
						       i_26_ - i_37_ - i_38_,
						       i_27_ + 1);
		    childBPs.addElement
			(breakpoint_29_.commitMinPenalty(i - i_37_,
							 i_26_ - i_37_ - i_38_,
							 i_39_));
		} else {
		    breakpoint_29_.startPos = -1;
		    breakpoint_29_.childBPs = null;
		    childBPs.addElement(breakpoint_29_);
		}
	    }
	}
	
	public int getBreakPenalty(int i, int i_40_, int i_41_) {
	    int i_42_ = breakPenalty;
	    int i_43_ = 0;
	    if (options == 2) {
		i--;
		i_40_ -= 2;
	    }
	    if (i < 0)
		return i_41_;
	    Enumeration enumeration = childBPs.elements();
	    boolean bool = options == 1;
	    BreakPoint breakpoint_44_ = (BreakPoint) enumeration.nextElement();
	    BreakPoint breakpoint_45_;
	    for (/**/; enumeration.hasMoreElements();
		 breakpoint_44_ = breakpoint_45_) {
		breakpoint_45_ = (BreakPoint) enumeration.nextElement();
		int i_46_ = breakpoint_44_.breakPos;
		int i_47_ = breakpoint_45_.breakPos;
		if (i_43_ > 0) {
		    i_43_ += i_47_ - i_46_;
		    if (i_43_ <= i)
			continue;
		    i_42_++;
		    if (bool) {
			i -= indentsize;
			i_40_ -= indentsize;
			bool = false;
		    }
		}
		if (i_46_ < endPos && currentLine.charAt(i_46_) == ' ')
		    i_46_++;
		if (i_47_ - i_46_ > i) {
		    int i_48_ = breakpoint_44_.startPos - i_46_;
		    int i_49_ = i_47_ - breakpoint_44_.endPos;
		    i_42_
			+= 1 + breakpoint_44_.getMinPenalty(i - i_48_,
							    i - i_48_ - i_49_,
							    i_41_ - i_42_ - 1);
		    if (bool) {
			i -= indentsize;
			i_40_ -= indentsize;
			bool = false;
		    }
		    i_43_ = 0;
		} else
		    i_43_ = i_47_ - i_46_;
		if (i_42_ >= i_41_)
		    return i_41_;
	    }
	    int i_50_ = breakpoint_44_.breakPos;
	    if (i_43_ > 0) {
		if (i_43_ + endPos - i_50_ <= i_40_)
		    return i_42_;
		i_42_++;
		if (bool) {
		    i -= indentsize;
		    i_40_ -= indentsize;
		    bool = false;
		}
	    }
	    if (i_50_ < endPos && currentLine.charAt(i_50_) == ' ')
		i_50_++;
	    if (endPos - i_50_ > i_40_) {
		int i_51_ = breakpoint_44_.startPos - i_50_;
		int i_52_ = endPos - breakpoint_44_.endPos;
		i_42_ += breakpoint_44_.getMinPenalty(i - i_51_,
						      i_40_ - i_51_ - i_52_,
						      i_41_ - i_42_);
	    }
	    if (i_42_ < i_41_)
		return i_42_;
	    return i_41_;
	}
    }
    
    protected String makeIndentStr(int i) {
	String string
	    = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t                    ";
	if (i < 0)
	    return "NEGATIVEINDENT" + i;
	int i_53_ = i / tabWidth;
	i -= i_53_ * tabWidth;
	if (i_53_ <= 20 && i <= 20)
	    return string.substring(20 - i_53_, 20 + i);
	StringBuffer stringbuffer = new StringBuffer(i_53_ + i);
	for (/**/; i_53_ > 20; i_53_ -= 20)
	    stringbuffer.append(string.substring(0, 20));
	stringbuffer.append(string.substring(0, i_53_));
	for (/**/; i > 20; i -= 20)
	    stringbuffer.append(string.substring(20));
	stringbuffer.append(string.substring(40 - i));
	return stringbuffer.toString();
    }
    
    public TabbedPrintWriter(OutputStream outputstream,
			     ImportHandler importhandler, boolean bool) {
	scopes = new Stack();
	pw = new PrintWriter(outputstream, bool);
	imports = importhandler;
	init();
    }
    
    public TabbedPrintWriter(Writer writer, ImportHandler importhandler,
			     boolean bool) {
	scopes = new Stack();
	pw = new PrintWriter(writer, bool);
	imports = importhandler;
	init();
    }
    
    public TabbedPrintWriter(OutputStream outputstream,
			     ImportHandler importhandler) {
	this(outputstream, importhandler, true);
    }
    
    public TabbedPrintWriter(Writer writer, ImportHandler importhandler) {
	this(writer, importhandler, true);
    }
    
    public TabbedPrintWriter(OutputStream outputstream) {
	this(outputstream, null);
    }
    
    public TabbedPrintWriter(Writer writer) {
	this(writer, null);
    }
    
    public void init() {
	indentsize = Options.outputStyle & 0xf;
	tabWidth = 8;
	lineWidth = 79;
	currentLine = new StringBuffer();
	currentBP = new BreakPoint(null, 0);
	currentBP.startOp(3, 1, 0);
    }
    
    public void tab() {
	currentIndent += indentsize;
	indentStr = makeIndentStr(currentIndent);
    }
    
    public void untab() {
	currentIndent -= indentsize;
	indentStr = makeIndentStr(currentIndent);
    }
    
    public void startOp(int i, int i_54_) {
	currentBP = (BreakPoint) currentBP.childBPs.lastElement();
	currentBP.startOp(i, i_54_, currentLine.length());
    }
    
    public void breakOp() {
	int i = currentLine.length();
	if (i > currentBP.startPos && currentLine.charAt(i - 1) == ' ')
	    i--;
	currentBP.breakOp(i);
    }
    
    public void endOp() {
	currentBP.endOp(currentLine.length());
	currentBP = currentBP.parentBP;
	if (currentBP == null)
	    throw new NullPointerException();
    }
    
    public Object saveOps() {
	Stack stack = new Stack();
	int i = currentLine.length();
	for (/**/; currentBP.parentBP != null;
	     currentBP = currentBP.parentBP) {
	    stack.push(new Integer(currentBP.breakPenalty));
	    currentBP.options = 3;
	    currentBP.endPos = i;
	}
	return stack;
    }
    
    public void restoreOps(Object object) {
	Stack stack = (Stack) object;
	while (!stack.isEmpty()) {
	    int i = ((Integer) stack.pop()).intValue();
	    startOp(3, i);
	}
    }
    
    public void println(String string) {
	print(string);
	println();
    }
    
    public void println() {
	currentBP.endPos = currentLine.length();
	int i = lineWidth - currentIndent;
	int i_55_ = currentBP.getMinPenalty(i, i, 1073741823);
	currentBP = currentBP.commitMinPenalty(i, i, i_55_);
	pw.print(indentStr);
	currentBP.printLines(currentIndent, currentLine.toString());
	pw.println();
	currentLine.setLength(0);
	currentBP = new BreakPoint(null, 0);
	currentBP.startOp(3, 1, 0);
    }
    
    public void print(String string) {
    	// xxxxxx
    	// if(string.equals())
    	
    	
    	int pos = string.indexOf("this$");
    	if(pos >=0){
			string = string.replaceAll("this\\$", "stathis");
    	}
		currentLine.append(string);
		
    }
    
    public void printType(Type type) {
	print(getTypeString(type));
    }
    
    public void pushScope(Scope scope) {
	scopes.push(scope);
    }
    
    public void popScope() {
	scopes.pop();
    }
    
    public boolean conflicts(String string, Scope scope, int i) {
	int i_56_ = string.indexOf('.');
	if (i_56_ >= 0)
	    string = string.substring(0, i_56_);
	int i_57_ = scopes.size();
	int i_58_ = i_57_;
	while (i_58_-- > 0) {
	    Scope scope_59_ = (Scope) scopes.elementAt(i_58_);
	    if (scope_59_ == scope)
		return false;
	    if (scope_59_.conflicts(string, i))
		return true;
	}
	return false;
    }
    
    public Scope getScope(Object object, int i) {
	int i_60_ = scopes.size();
	int i_61_ = i_60_;
	while (i_61_-- > 0) {
	    Scope scope = (Scope) scopes.elementAt(i_61_);
	    if (scope.isScopeOf(object, i))
		return scope;
	}
	return null;
    }
    
    public String getInnerClassString(ClassInfo classinfo, int i) {
	InnerClassInfo[] innerclassinfos = classinfo.getOuterClasses();
	if (innerclassinfos == null)
	    return null;
	for (int i_62_ = 0; i_62_ < innerclassinfos.length; i_62_++) {
	    if (innerclassinfos[i_62_].name == null
		|| innerclassinfos[i_62_].outer == null)
		return null;
	    Scope scope
		= getScope(ClassInfo.forName(innerclassinfos[i_62_].outer), 1);
	    if (scope != null
		&& !conflicts(innerclassinfos[i_62_].name, scope, i)) {
		StringBuffer stringbuffer
		    = new StringBuffer(innerclassinfos[i_62_].name);
		int i_63_ = i_62_;
		while (i_63_-- > 0)
		    stringbuffer.append('.')
			.append(innerclassinfos[i_63_].name);
		return stringbuffer.toString();
	    }
	}
	String string
	    = getClassString(ClassInfo.forName(innerclassinfos
					       [innerclassinfos.length - 1]
					       .outer),
			     i);
	StringBuffer stringbuffer = new StringBuffer(string);
	int i_64_ = innerclassinfos.length;
	while (i_64_-- > 0)
	    stringbuffer.append('.').append(innerclassinfos[i_64_].name);
	return stringbuffer.toString();
    }
    
    public String getAnonymousClassString(ClassInfo classinfo, int i) {
	InnerClassInfo[] innerclassinfos = classinfo.getOuterClasses();
	if (innerclassinfos == null)
	    return null;
	for (int i_65_ = 0; i_65_ < innerclassinfos.length; i_65_++) {
	    if (innerclassinfos[i_65_].name == null)
		return "ANONYMOUS CLASS " + classinfo.getName();
	    Scope scope = getScope(classinfo, 2);
	    if (scope != null
		&& !conflicts(innerclassinfos[i_65_].name, scope, i)) {
		StringBuffer stringbuffer
		    = new StringBuffer(innerclassinfos[i_65_].name);
		int i_66_ = i_65_;
		while (i_66_-- > 0)
		    stringbuffer.append('.')
			.append(innerclassinfos[i_66_].name);
		return stringbuffer.toString();
	    }
	    if (innerclassinfos[i_65_].outer == null) {
		StringBuffer stringbuffer;
		if (scope != null)
		    stringbuffer = new StringBuffer("NAME CONFLICT ");
		else
		    stringbuffer = new StringBuffer("UNREACHABLE ");
		stringbuffer.append(innerclassinfos[i_65_].name);
		int i_67_ = i_65_;
		while (i_67_-- > 0)
		    stringbuffer.append('.')
			.append(innerclassinfos[i_67_].name);
		return stringbuffer.toString();
	    }
	}
	String string
	    = getClassString(ClassInfo.forName(innerclassinfos
					       [innerclassinfos.length - 1]
					       .outer),
			     i);
	StringBuffer stringbuffer = new StringBuffer(string);
	int i_68_ = innerclassinfos.length;
	while (i_68_-- > 0)
	    stringbuffer.append('.').append(innerclassinfos[i_68_].name);
	return stringbuffer.toString();
    }
    
    public String getClassString(ClassInfo classinfo, int i) {
	String string = classinfo.getName();
	if (string.indexOf('$') >= 0) {
	    if ((Options.options & 0x2) != 0) {
		String string_69_ = getInnerClassString(classinfo, i);
		if (string_69_ != null)
		    return string_69_;
	    }
	    if ((Options.options & 0x4) != 0) {
		String string_70_ = getAnonymousClassString(classinfo, i);
		if (string_70_ != null)
		    return string_70_;
	    }
	}
	if (imports != null) {
	    String string_71_ = imports.getClassString(classinfo);
	    if (!conflicts(string_71_, null, i))
		return string_71_;
	}
	if (conflicts(string, null, 4))
	    return "PKGNAMECONFLICT " + string;
	return string;
    }
    
    public String getTypeString(Type type) {
	if (type instanceof ArrayType)
	    return getTypeString(((ArrayType) type).getElementType()) + "[]";
	if (type instanceof ClassInterfacesType) {
	    ClassInfo classinfo = ((ClassInterfacesType) type).getClassInfo();
	    return getClassString(classinfo, 1);
	}
	if (type instanceof NullType)
	    return "Object";
	return type.toString();
    }
    
    public void openBrace() {
	if ((Options.outputStyle & 0x10) != 0) {
	    print(currentLine.length() > 0 ? " {" : "{");
	    println();
	} else {
	    if (currentLine.length() > 0)
		println();
	    if ((Options.outputStyle & 0x20) == 0 && currentIndent > 0)
		tab();
	    println("{");
	}
    }
    
    public void openBraceClass() {
	if (currentLine.length() > 0) {
	    if ((Options.outputStyle & 0x10) != 0)
		print(" ");
	    else
		println();
	}
	println("{");
    }
    
    public void openBraceNoIndent() {
	if ((Options.outputStyle & 0x10) != 0) {
	    print(currentLine.length() > 0 ? " {" : "{");
	    println();
	} else {
	    if (currentLine.length() > 0)
		println();
	    println("{");
	}
    }
    
    public void openBraceNoSpace() {
	if ((Options.outputStyle & 0x10) != 0)
	    println("{");
	else {
	    if (currentLine.length() > 0)
		println();
	    if ((Options.outputStyle & 0x20) == 0 && currentIndent > 0)
		tab();
	    println("{");
	}
    }
    
    public void closeBraceContinue() {
	if ((Options.outputStyle & 0x10) != 0)
	    print("} ");
	else {
	    println("}");
	    if ((Options.outputStyle & 0x20) == 0 && currentIndent > 0)
		untab();
	}
    }
    
    public void closeBraceClass() {
	print("}");
    }
    
    public void closeBrace() {
	if ((Options.outputStyle & 0x10) != 0)
	    println("}");
	else {
	    println("}");
	    if ((Options.outputStyle & 0x20) == 0 && currentIndent > 0)
		untab();
	}
    }
    
    public void closeBraceNoIndent() {
	println("}");
    }
    
    public void flush() {
	pw.flush();
    }
    
    public void close() {
	pw.close();
    }
}
