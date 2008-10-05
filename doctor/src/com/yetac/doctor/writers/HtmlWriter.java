/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.writers;

import java.io.*;
import java.util.Stack;

import com.yetac.doctor.applet.*;
import com.yetac.doctor.cmd.Anchor;
import com.yetac.doctor.cmd.Bold;
import com.yetac.doctor.cmd.Code;
import com.yetac.doctor.cmd.Graphic;
import com.yetac.doctor.cmd.Italic;
import com.yetac.doctor.cmd.Link;
import com.yetac.doctor.cmd.Outline;
import com.yetac.doctor.cmd.OutputConsole;
import com.yetac.doctor.cmd.Source;
import com.yetac.doctor.workers.DocsFile;
import com.yetac.doctor.workers.Files;

public class HtmlWriter extends AbstractWriter {
    
    private RandomAccessFile outline;
    private RandomAccessFile current;
    private DocsFile currentOutlineTarget;
    
    private Stack            embedInto;
    
    private String           preHeader;
    private String           preDiv;
    private String           preTable;
    private int              outLineNumber;

    static final String      HTML_BR          = "<br>\r\n";
    static final String      HTML_WHITESPACE  = "&nbsp;";
    static final String      HTML_TAB = multiple(HTML_WHITESPACE, TAB_WHITESPACES);
    
    static final char        CLOSING_BRACKET    = '>';

    private boolean          ignoreCRforOutline;
    private boolean          executableInCurrent;

    public void start(Files _files) throws Exception {
        super.start(_files);
        embedInto = new Stack();
        executableInCurrent=false;
        files.task.setVariable("html", new Boolean(true));
        
        if(! files.task.isInteractive() && files.task.doShowCodeExecutionResults()){
            installRunner();
        }
        
        files.copyFile(inputPath("docs", "css"), outputPath("docs", "css"));
        try {
            files.copyFile(inputPath("docs_ie", "css"), outputPath("docs_ie", "css"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        files.copyFile(inputPath("outline", "css"), outputPath("outline", "css"));
        files.copyAllFiles(files.task.inputImages(),files.task.getOutputPath()+"/docs");
        if(files.task.isInteractive()){
            String templateHtmlFileName = "docs";
            if(new File(inputPath("interactive")).exists()){
                templateHtmlFileName = "interactive";    
            }
            preDiv="<div id=\"pagecontainer\">";
            preTable="<table><tr><td width=\"5\">&nbsp;</td><td>";
	        preHeader=readTag(inputPath(templateHtmlFileName), "<body");
        }else{
            preHeader=readTag(inputPath("docs"), "<body");
        }
        String outlinePath = outputPath("outline");
        new File(outlinePath).delete();
        outline = new RandomAccessFile(outlinePath, "rw");
        String outlineHeader = readTag(inputPath("outline"), "<body");
        outline.write(toBytes(outlineHeader));
        String outlineImage = files.task.getOutlineImage();
        if(outlineImage != null){
        	
            outlineImage = "<img src=\"" + outlineImage + "\" border=\"0\" />";
            String linkHome = files.task.getLinkHome();
            if(linkHome != null){
                outlineImage = "<a href=\"" + linkHome + "\" target=\"_blank\">" + outlineImage + "</a>";
            }
            outline.writeBytes(outlineImage);
        }
        outline.writeBytes("<ul>\r\n");
    }
    
    private void writeIndexHtml(String frontPage) {
        String path = files.task.getOutputPath() + "/index.html";
        new File(path).delete();
        try {
            RandomAccessFile raf = new RandomAccessFile(path, "rw");
            raf.writeBytes("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">\r\n");
            raf.writeBytes("<html>\r\n");
            raf.writeBytes("<head>\r\n");
            raf.writeBytes("</head>\r\n");
            raf.writeBytes("<frameset cols=\"350,*\">\r\n");
            raf.writeBytes("<frame name=\"outline\" target=\"main\" src=\"./docs/outline.html\" leftMargin=0 topMargin=0 marginheight=0 marginwidth=0 bottomMargin=0 rightMargin=0>\r\n");
            raf.writeBytes("<frame name=\"main\" src=\"./docs/");
            raf.writeBytes(frontPage);
            raf.writeBytes("\" target=\"main\" leftMargin=0>\r\n");
            raf.writeBytes("<noframes>\r\n");
            raf.writeBytes("<body>\r\n");
            raf.writeBytes("This document requires your browser to support frames.\r\n");
            raf.writeBytes("</body>\r\n");
            raf.writeBytes("</noframes>\r\n");
            raf.writeBytes("</frameset>\r\n");
            raf.writeBytes("</html>\r\n");
            raf.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    
    public void beginEmbedded(DocsFile source) throws Exception{
        String path = outputPath(source.title) ;
        new File(path).delete();
        embedInto.push(new HtmlWriterStackEntry(currentOutlineTarget, current, outlineLevel));
        current = new RandomAccessFile(path, "rw");
        currentOutlineTarget = source;
        writeToFile(preHeader);
        if(preDiv != null){
            writeToFile(preDiv);
        }
        if(preTable != null){
            writeToFile(preTable);
        }
        
    }
    
    public void end() {
        files.task.setVariable("HTML", new Boolean(false));
        try {
            endCurrent();
            while (outlineLevel > 0) {
                outline.writeBytes("</ul>\r\n");
                outlineLevel--;
            }
            outline.writeBytes("</ul>\r\n</body>\r\n</html>");
            outline.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void endEmbedded() throws Exception{
        endCurrent();
        HtmlWriterStackEntry parent = (HtmlWriterStackEntry)embedInto.pop();
        current = parent.raf;
        currentOutlineTarget = parent.outlineTarget;
        while (outlineLevel < parent.outlineLevel) {
            writeToFile("<ul>\r\n");
            outline.writeBytes("<ul>\r\n");
            outlineLevel++;
        }
        while (outlineLevel > parent.outlineLevel) {
            writeToFile("</ul>\r\n");
            outline.writeBytes("</ul>\r\n");
            outlineNumbers[outlineLevel--] = 0;
        }
    }

    public void setSource(DocsFile source) throws Exception {
        super.setSource(source);
        if(firstPage) {
            writeIndexHtml(source.title + ".html");
        }
        endCurrent();
        String path = outputPath(source.title) ;
        new File(path).delete();
        current = new RandomAccessFile(path, "rw");
        currentOutlineTarget = source;
        writeToFile(preHeader);
        if(preDiv != null){
            writeToFile(preDiv);
        }
        if(firstPage) {
            if(new File(inputPath("frontpage")).exists()){
                String preFirstBody = readBody(inputPath("frontpage"));
                if(preFirstBody != null){
                    writeToFile(preFirstBody);
                }
            }
        }
        if(preTable != null){
            writeToFile(preTable);
        }
    }

    private void endCurrent() throws Exception {
        if (current != null) {
            write("<br><br><br>");
            write("<p align=\"center\"><a href=\"http://www.db4o.com/\" target=_top><small>www.db4o.com</small></a></p>");
            // helps scrolling jump anchors to the top of the browser
            for (int i = 0; i < 50; i++) {
                write("<br>");
            }
            write("</td></tr></table></div>");
            if(executableInCurrent) {
                write("<div class=\"console\">");
                write("<applet name=\""+ DoctorConsoleApplet.APPLETNAME + "\" code=\"com.yetac.doctor.applet.DoctorConsoleApplet\" archive=\"");
                write(files.task.getArchive());
                write("\" width=\"100%\" height=\"150\"><param name=\"yapfile\" value=\"formula1.db4o\"/>");
                executableInCurrent=false;
            }
            write("</body></html>");
            current.close();
            current = null;
            currentOutlineTarget = null;
        }
    }

    public String extension() {
        return "html";
    }

    private String readTag(String path, String until) throws Exception {
        String buffer = files.readFileStr(path);
        int matchidx=buffer.indexOf(until);
        if(matchidx<0) {
        	return null;
        }
        matchidx=buffer.indexOf(CLOSING_BRACKET,matchidx+until.length());
        if(matchidx<0) {
        	return null;
        }
        return buffer.substring(0, matchidx+1);
    }
    
    private String readBody(String path) throws Exception {
        String buffer = files.readFileStr(path);
        int begin = buffer.indexOf("<body>");
        int end = buffer.indexOf("</body>",begin);
        return buffer.substring(begin+6,end);
    }
    
    public void write(Anchor command) throws Exception {
        write("<a name=\"");
        write(command.parameter);
        write("\"></a>");
    }
    
    public void write(Bold command) throws Exception{
        write("<b>");
        write(command.text);
        write("</b>");
    }
    
    public void write(Graphic command) throws Exception {
        write("<img border=\"0\" src=\"");
        write(command.parameter);
        write("\" />");
    }

    public void write(Italic command) throws Exception{
        write("<em>");
        write(command.text);
        write("</em>");
    }

    public void write(OutputConsole command) throws Exception{
        writeOutputBlock(command.text);
    }

    private void writeOutputBlock(String text) {
        write("<table width=\"100%\" cellpadding=\"3\" cellspacing=\"0\" border=\"0\"><tr><td class=\"co\">");
        write("<b>OUTPUT:</b><br/>");
        write(text);
        write("</td></table></table>");
    }

    public void write(Link command) throws Exception {
        DocsFile file = (DocsFile) command.source.files.anchors.get(new String(
            command.parameter));
        write("<a href=\"");
        if (!command.external()) {
            if (file != null) {
                write(file.title + "." + extension());
            }
            write("#");
        }
        write(command.parameter);
        write("\"");
        if (command.external()) {
            writeToFile(" ");
            write("target=\"_blank\"");
        }
        write(">");
        write(command.text);
        write("</a>");
    }

    public void write(Outline command) throws Exception {
        
        HtmlWriterStackEntry parent = null;
        
        if(! embedInto.empty()){
            parent = (HtmlWriterStackEntry)embedInto.peek();
            if(parent.embedOutLineLevel == -1){
                parent.embedOutLineLevel = command.level;
            }
            if(parent.embedOutLineLevel != command.level){
                parent = null;
            }else{
		        while (parent.outlineLevel < command.level) {
		            parent.raf.writeBytes("<ul>\r\n");
		            parent.outlineLevel++;
		        }
		        while (parent.outlineLevel > command.level) {
		            parent.raf.writeBytes("</ul>\r\n");
		            parent.outlineLevel --;
		            
		        }
            }
        }
        
        if(! firstPage) {
	        while (outlineLevel < command.level) {
	            writeToFile("<ul>\r\n");
	            outline.writeBytes("<ul>\r\n");
	            outlineLevel++;
	        }
	        while (outlineLevel > command.level) {
	            writeToFile("</ul>\r\n");
	            outline.writeBytes("</ul>\r\n");
	            outlineNumbers[outlineLevel--] = 0;
	        }
	        outlineNumbers[outlineLevel]++;
        }
        String hx = outlineLevel == 0 ? "h1" : "h2";

        String anchorName = "outline" + outLineNumber++;
        write("<a name=\"");
        write(anchorName);
        write("\"></a><br><");
        write(hx);
        write(">");
        String numbers = outlineNumberString();
        write(numbers);
        write(command.parameter);
        write("</");
        write(hx);
        write(">");
        if( parent != null){
	        RandomAccessFile raf =  parent.raf;
            raf.writeBytes("<");
            raf.writeBytes(hx);
            raf.writeBytes("><a href=\"");
            raf.writeBytes(currentOutlineTarget.title);
            raf.writeBytes(".html#");
            raf.writeBytes(anchorName);
            raf.writeBytes("\">");
            raf.writeBytes(numbers);
            raf.write(toBytes(command.parameter));
            raf.writeBytes("</a></");
            raf.writeBytes(hx);
            raf.writeBytes(">\r\n");
        }
        
        outline.writeBytes("<li><a href=\"");
        outline.writeBytes(currentOutlineTarget.title);
        outline.writeBytes(".html#");
        outline.writeBytes(anchorName);
        outline.writeBytes("\">");
        outline.writeBytes(numbers);
        outline.write(toBytes(command.parameter));
        outline.writeBytes("</a></li>\r\n");
        ignoreCRforOutline = true;
    }

    private void writeSourceCodeBlock(String code,Source command) throws UnsupportedEncodingException {
        writeToFile("<table width=\"100%\" cellpadding=\"3\" cellspacing=\"0\" border=\"0\"><tr><td class=\"lg\">\r\n");
        writeToFile("<code>");
        if(command!=null&&command.getMethodName()!=null) {
            code=extractSource(code,command.getMethodName(),command.getParamValue(Source.CMD_FULL));
            if(code.length()==0) {
            	throw new RuntimeException("Method '"+command.getClassName()+"#"+command.getMethodName()+"' not found.");
            }
        }
        String codestr=new String(code);
        codestr=codestr.replaceAll("&","&amp;");
        codestr=codestr.replaceAll("<","&lt;");

        write(codestr);

        writeToFile("</code></td>");
        if(files.task.isInteractive()){
            if(command!=null&&command.getMethodName()!=null&&command.getParamValue(Source.CMD_RUN)) {
                writeToFile("<td class=\"lg\" align=\"left\" valign=\"bottom\" width=43>");
            	if(files.task.variableIsTrue("net")){
            		write("<input type='button' class='button' value='Run' ");
            		write("onclick='window.external.RunExample(\"" + command.getClassName() + "\", \"" + command.getMethodName() + "\")' />");
    
            	} else {
    	            executableInCurrent=true;
    	    		write("<applet code=\"com.yetac.doctor.applet.DoctorRunExampleApplet\" archive=\"");
    	    		write(files.task.getArchive());
    	    		write("\" width=\"40\" height=\"30\">");
    	    		write("<param name=\"exampleclass\" value=\""+command.getClassName()+"\"/>");
    	    		write("<param name=\"examplemethod\" value=\""+command.getMethodName()+"\"/>");
    	    		write("</applet>");
            	}
                writeToFile("</td>");
            }
            writeToFile("</tr></table>\r\n");
        }else{
            writeToFile("</tr></table>\r\n");
            if(command!= null && files.task.doShowCodeExecutionResults()){
                String methodname=command.getMethodName();
                if(methodname!=null&&command.getParamValue(Source.CMD_RUN)) {
                    try {
                        ByteArrayOutputStream out=new ByteArrayOutputStream();
                        runner.runExample(command.getClassName(),command.getMethodName(),out);
                        out.close();
                        if(command.getParamValue(Source.CMD_OUTPUT)) {
                            writeOutputBlock(new String(out.toByteArray(),"ISO-8859-1"));
                        }
                    }
                    catch(Exception exc) {
                        exc.printStackTrace();
                    }
                }
            }
        }
    }
        
    public void write(Source command) throws Exception {
        File file = command.getFile();
        if (!file.exists()) {
            System.err.println("File not found: " + file.getAbsolutePath());
            return;
        }
        String bytes = files.readFileStr(file);
        writeSourceCodeBlock(bytes,command);
    }

    public void write(Code command) throws Exception {
        writeSourceCodeBlock(command.text,null);
    }

    public void write(String str) {
    	StringBuffer buf=new StringBuffer();
        char prev = WHITESPACE;
        for (int i = 0; i < str.length(); i++) {
        	char ch=str.charAt(i);
            if(ch == TAB){
                buf.append(HTML_TAB);
            } else if (ch == WHITESPACE
                && (prev == WHITESPACE || (i < str.length()-1 && str.charAt(i + 1) == WHITESPACE))) {
            	buf.append(HTML_WHITESPACE);
            } else if (ch == LF || ch == BR) {
                if(! ignoreCRforOutline) {
                	buf.append(HTML_BR);
                }
                ignoreCRforOutline = false;
                if (i < str.length() - 1) {
                    if (str.charAt(i + 1) == LF || str.charAt(i + 1) == BR) {
                        i++;
                    }
                }
            } else {
            	buf.append(ch);
            }
            prev = ch;
        }
        writeToFile(buf.toString());
    }

    protected void writeToFile(String str) {
    	try {
			current.write(toBytes(str));
		} catch (IOException e) {
            throw new RuntimeException(e);
		}
    }

    private byte[] toBytes(String str) {
    	try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException exc) {
			throw new RuntimeException(exc);
		}
    }
}

