/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.writers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yetac.doctor.Configuration;
import com.yetac.doctor.applet.ExampleRunner;
import com.yetac.doctor.cmd.*;
import com.yetac.doctor.workers.DocsFile;
import com.yetac.doctor.workers.Files;

public abstract class AbstractWriter extends Configuration implements
    DocsWriter {
    
    static final int         TAB_WHITESPACES = 4; 

    
    protected ExampleRunner runner;


    protected Files   files;
    protected int     outlineLevel;
    protected int[]   outlineNumbers = new int[10];
    protected boolean firstPage;

    private DocsFile   source;
        
    public void beginEmbedded(DocsFile source)  throws Exception{
    }

    public void end() {
    }
    
    public void endEmbedded() throws Exception{
    }

    protected abstract String extension();
    
    public Files files(){
        return files;
    }

    protected String inputPath(String name) {
        return inputPath(name, extension());
    }

    protected String inputPath(String name, String extension) {
        return files.task.inputHTML() + "/" + name + "." + extension;
    }
    
    protected void installRunner() throws Exception{
        
        File yapFile=File.createTempFile("formula1",".yap");
        
        try{
            runner=new ExampleRunner(getClass().getClassLoader(),yapFile);
        }catch(Exception e){
            System.err.println("*** Example runner could not be installed. Continuing without. Reason:");
            e.printStackTrace();
        }
    }

    protected String outputPath(String name) {
        return outputPath(name, extension());
    }

    protected String outputPath(String name, String extension) {
        return files.task.getOutputPath() + "/docs/" + name + "." + extension;
    }

    protected String outlineNumberString() {
        if (firstPage || ! files.task.outlineNumbers()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <= outlineLevel; i++) {
            sb.append(outlineNumbers[i]);
            sb.append(".");
        }
        sb.append(" ");
        return sb.toString();
    }

    protected String path() {
        return files.task.getOutputPath() + "/" + files.task.getOutputFile() + "." + extension();
    }

    public void setSource(DocsFile source) throws Exception {
        firstPage = (this.source == null);
        this.source = source;
    }

    public void start(Files _files) throws Exception {
        source = null;
        outlineLevel = 0;
        outlineNumbers = new int[10];
        this.files = _files;
    }
    
    public void write(Anchor command) throws Exception {
    }

    public void write(Bold command) throws Exception {
        write(command.text);
    }

    public void write(Command command) {
    }
    
    public void write(Comment command) {
    }

    public void write(Center command) throws Exception {
    }

    public void write(Embed command) throws Exception {
    }

    public void write(Graphic command) throws Exception {
    }
    
    public void write(IgnoreCR command) throws Exception{
        write(" ");
    }

    public void write(Left command) throws Exception {
    }

    public void write(Link command) throws Exception {
    }

    public void write(NewPage command) throws Exception {
    }

    public void write(Outline command) throws Exception {
    }

    public void write(Right command) throws Exception {
    }

    public void write(Source command) throws Exception {
    }
    
    public void write(Xamine command) throws Exception{
        write(command.text);
    }

    public abstract void write(String str);

    public void write(String str, int start, int end) {
        write(str.substring(start,end+1));
    }

    public void writeln(String str) {
        write(str+BR);
    }

    public final void write(Text command) {
        write(command.source.bytes, command.offset, command.offsetEnd);
    }

    public void write(Variable command) throws Exception {
        write(command.text);
    }

    abstract protected void writeToFile(String str);

    protected String extractSource(String src,String methodName,boolean full) throws UnsupportedEncodingException {
    	boolean isVB=".vb".equals(source.files.task.getSourceExtension());
    	return isVB
    	? extractSourceVB(src, methodName, full)
    	: extractSourceJavaCSharp(src, methodName, full);
    }
    
	private String extractSourceVB(String src, String methodName, boolean full) {
		String methodheadregexp="(public|protected|private)\\s+(shared\\s+)?(sub|class)[^\\n]*"+methodName;
        Pattern methodheadpattern=Pattern.compile(methodheadregexp,Pattern.CASE_INSENSITIVE);
        Matcher matcher=methodheadpattern.matcher(src);
        if(!matcher.find()) {
			System.err.println("Not found: "+methodName);
            return "";
        }
        int startidx=matcher.start();
        if (!full) {
        	startidx = src.indexOf('\n',startidx)+1;
        }        
        String codeEndRegexp="end (sub|class)";
        Pattern codeEndPattern=Pattern.compile(codeEndRegexp,Pattern.CASE_INSENSITIVE);
        matcher=codeEndPattern.matcher(src.substring(startidx));
        if(!matcher.find()) {
			System.err.println("End point not found: "+methodName);
            return "";
        }
        int endidx=startidx+matcher.start();
        if(full) {
        	endidx=startidx+matcher.end();
        }
        
        String code = src.substring(startidx, endidx);
        return formatMethod(methodName, code, full, true);
	}


	private String extractSourceJavaCSharp(String src, String methodName, boolean full) {
		String methodheadregexp="(public|protected|private)\\s+[^\\n]*"+methodName;
        Pattern methodheadpattern=Pattern.compile(methodheadregexp,Pattern.CASE_INSENSITIVE);
        Matcher matcher=methodheadpattern.matcher(src);
        if(!matcher.find()) {
			System.err.println("Not found: "+methodName);
            return "";
        }
        int startidx=matcher.start();
        int idx=src.indexOf('{',startidx)+1;
        int brackets=0;
        StringBuffer methodsrc=new StringBuffer();
        if(full) {
            methodsrc.append(src.substring(startidx,idx));
        }
        while(brackets>-1&&idx<src.length()) {
            char curchar=src.charAt(idx);
            switch(curchar) {
            	case '{':
            	    brackets++;
            	    break;
            	case '}':
            	    brackets--;
            	    break;
            }
            if(curchar!='}'||brackets>-1) {
                methodsrc.append(curchar);
            }
            idx++;
        }
        if(full) {
            methodsrc.append("}");
        }        
        return formatMethod(methodName, methodsrc.toString(), full, false);
	}

	private String formatMethod(String methodName, String code, boolean full, boolean vb) {
		String commented = vb ? "' " : "// " ;
		int depth=((full ? 1 : 2)+(files.task.variableIsTrue("net") ? 1 : 0))*4;
		String result=(full ? "" : commented + methodName+"\n\n")+code.trim();
        result=result.replaceAll("^\\s*","");
        result=result.replaceAll("\\t","    ");
        result=result.replaceAll("\\n {"+depth+"}","\n");
        return result;
	}
	
    static String multiple(String ofWhat, int times){
    	StringBuffer buf=new StringBuffer();
        for (int i = 0; i < times ; i++) {
        	buf.append(ofWhat);
        }
        return buf.toString();
    }
    
}