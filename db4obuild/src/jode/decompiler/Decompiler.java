/* Decompiler - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package jode.decompiler;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import jode.GlobalOptions;
import jode.bytecode.ClassInfo;
import jode.bytecode.SearchPath;

public class Decompiler
{
    private SearchPath searchPath = null;
    private int importPackageLimit = 2147483647;
    private int importClassLimit = 1;
    public static final char altPathSeparatorChar = ',';
    private static final String[] optionStrings
	= { "lvt", "inner", "anonymous", "push", "pretty", "decrypt",
	    "onetime", "immediate", "verify", "contrafo" };
    
    public void setClassPath(String string) {
	searchPath = new SearchPath(string);
    }
    
    public void setClassPath(String[] strings) {
	StringBuffer stringbuffer = new StringBuffer(strings[0]);
	for (int i = 1; i < strings.length; i++)
	    stringbuffer.append(',').append(strings[i]);
	searchPath = new SearchPath(stringbuffer.toString());
    }
    
    public void setOption(String string, String string_0_) {
	if (string.equals("style")) {
	    if (string_0_.equals("gnu"))
		Options.outputStyle = 66;
	    else if (string_0_.equals("sun"))
		Options.outputStyle = 20;
	    else if (string_0_.equals("pascal"))
		Options.outputStyle = 36;
	    else
		throw new IllegalArgumentException("Invalid style "
						   + string_0_);
	} else if (string.equals("import")) {
	    int i = string_0_.indexOf(',');
	    int i_1_ = Integer.parseInt(string_0_.substring(0, i));
	    if (i_1_ == 0)
		i_1_ = 2147483647;
	    int i_2_ = Integer.parseInt(string_0_.substring(i + 1));
	    if (i_2_ == 0)
		i_2_ = 2147483647;
	    if (i_2_ < 0 || i_1_ < 0)
		throw new IllegalArgumentException
			  ("Option import doesn't allow negative parameters");
	    importPackageLimit = i_1_;
	    importClassLimit = i_2_;
	} else if (string.equals("verbose"))
	    GlobalOptions.verboseLevel = Integer.parseInt(string_0_);
	else {
	    for (int i = 0; i < optionStrings.length; i++) {
		if (string.equals(optionStrings[i])) {
		    if (string_0_.equals("0") || string_0_.equals("off")
			|| string_0_.equals("no"))
			Options.options &= 1 << i ^ 0xffffffff;
		    else if (string_0_.equals("1") || string_0_.equals("on")
			     || string_0_.equals("yes"))
			Options.options |= 1 << i;
		    else
			throw new IllegalArgumentException("Illegal value for "
							   + string);
		    return;
		}
	    }
	    throw new IllegalArgumentException("Illegal option: " + string);
	}
    }
    
    public void setErr(PrintWriter printwriter) {
	GlobalOptions.err = printwriter;
    }
    
    public void decompile
	(String string, Writer writer, ProgressListener progresslistener)
	throws IOException {
	if (searchPath == null) {
	    String string_3_ = System.getProperty("java.class.path")
				   .replace(File.pathSeparatorChar, ',');
	    searchPath = new SearchPath(string_3_);
	}
	ClassInfo.setClassPath(searchPath);
	ClassInfo classinfo = ClassInfo.forName(string);
	ImportHandler importhandler
	    = new ImportHandler(importPackageLimit, importClassLimit);
	TabbedPrintWriter tabbedprintwriter
	    = new TabbedPrintWriter(writer, importhandler, false);
	ClassAnalyzer classanalyzer
	    = new ClassAnalyzer(null, classinfo, importhandler);
	classanalyzer.dumpJavaFile(tabbedprintwriter, progresslistener);
	writer.flush();
    }
}
