/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.writers;

import java.io.UnsupportedEncodingException;

import com.yetac.doctor.*;
import com.yetac.doctor.cmd.*;
import com.yetac.doctor.workers.*;

public abstract class AbstractWriter extends Configuration implements
    DocsWriter {

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

    public void write(String str) {
        write(str.getBytes());
    }

    public void writeln(String str) {
        write(str);
        writeToFile(new byte[] { BR}, 0, 1);
    }

    public void write(Text command) {
        write(command.source.bytes, command.offset, command.offsetEnd);
    }

    public void write(Variable command) throws Exception {
        write(command.text);
    }

    public void write(byte[] bytes) {
        if (bytes != null) {
            write(bytes, 0, bytes.length - 1);
        }
    }

    public void write(byte[] bytes, int start, int end) {
        writeToFile(bytes, start, end);
    }

    abstract protected void writeToFile(byte[] bytes, int start, int end);

    protected byte[] extractMethod(byte[] orig,String methodName,boolean full) throws UnsupportedEncodingException {
        String src=new String(orig,"iso-8859-1");
        String startstr="public static void "+methodName;
        int startidx=src.indexOf(startstr);
        if(startidx<0) {
            System.err.println("Method '"+methodName+"' not found.");
        }
        int idx=src.indexOf('{',startidx)+1;
        int brackets=0;
        StringBuffer methodsrc=new StringBuffer();
        if(full) {
            methodsrc.append(startstr);
            methodsrc.append('{');
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
        int depth=((full ? 1 : 2)+(files.task.variableIsTrue("net") ? 1 : 0))*4;
        String result=(full ? "" : "["+methodName+"]\n\n")+methodsrc.toString().trim();
        result=result.replaceAll("^\\s*","");
        result=result.replaceAll("\\t","    ");
        result=result.replaceAll("\\n {"+depth+"}","\n");
        return result.getBytes("iso-8859-1");
    }
}