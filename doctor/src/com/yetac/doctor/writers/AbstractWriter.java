/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.writers;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yetac.doctor.Configuration;
import com.yetac.doctor.applet.ExampleRunner;
import com.yetac.doctor.cmd.Anchor;
import com.yetac.doctor.cmd.Bold;
import com.yetac.doctor.cmd.Center;
import com.yetac.doctor.cmd.Command;
import com.yetac.doctor.cmd.Comment;
import com.yetac.doctor.cmd.Embed;
import com.yetac.doctor.cmd.Graphic;
import com.yetac.doctor.cmd.IgnoreCR;
import com.yetac.doctor.cmd.Left;
import com.yetac.doctor.cmd.Link;
import com.yetac.doctor.cmd.NewPage;
import com.yetac.doctor.cmd.Outline;
import com.yetac.doctor.cmd.Right;
import com.yetac.doctor.cmd.Source;
import com.yetac.doctor.cmd.Text;
import com.yetac.doctor.cmd.Variable;
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
    
    protected byte[]           conversionBuffer = new byte[1000];
    protected int              bufferPos;

    
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
    
    protected void toBuffer(byte b) {
        if (bufferPos > conversionBuffer.length -1) {
            byte[] temp = new byte[conversionBuffer.length + 1000];
            System.arraycopy(conversionBuffer, 0, temp, 0,
                conversionBuffer.length);
            conversionBuffer = temp;
        }
        conversionBuffer[bufferPos++] = b;
    }

    protected void toBuffer(byte[] bs) {
        for (int i = 0; i < bs.length; i++) {
            toBuffer(bs[i]);
        }
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
        String methodheadregexp="(public|protected|private) [^\\n]*"+methodName;
        Pattern methodheadpattern=Pattern.compile(methodheadregexp,Pattern.CASE_INSENSITIVE);
        Matcher matcher=methodheadpattern.matcher(src);
        if(!matcher.find()) {
			System.err.println("Not found: "+methodName);
            return new byte[0];
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
        int depth=((full ? 1 : 2)+(files.task.variableIsTrue("net") ? 1 : 0))*4;
        String result=(full ? "" : "["+methodName+"]\n\n")+methodsrc.toString().trim();
        result=result.replaceAll("^\\s*","");
        result=result.replaceAll("\\t","    ");
        result=result.replaceAll("\\n {"+depth+"}","\n");
        return result.getBytes("iso-8859-1");
    }
    
    static byte[] multiple(byte[] ofWhat, int times){
        int len = ofWhat.length;
        int pos = 0;
        byte[] res = new byte[len * times];
        for (int i = 0; i < times ; i++) {
            System.arraycopy(ofWhat, 0, res,pos, len);
            pos += len;
        }
        return res;
    }
    
}