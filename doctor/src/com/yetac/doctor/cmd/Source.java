/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.yetac.doctor.*;
import com.yetac.doctor.writers.DocsWriter;

public class Source extends Command{
    public final static String CMD_RUN="run";
    public final static String CMD_OUTPUT="out";
    public final static String CMD_FULL="{}";
    
    private Map parsedparams;
    
    public void resolve() {
        detectParameters();
        parseParameters();
    }

    private void parseParameters() {
        parsedparams=new HashMap();
        setParamValue(CMD_RUN,true);
        setParamValue(CMD_OUTPUT,true);
        setParamValue(CMD_FULL,false);
        if(text==null) {
            return;
        }
        String paramstr=new String(text);
        int idx=0;
        while(idx<paramstr.length()) {
            boolean value=(paramstr.charAt(idx)=='+');
            idx++;
            int lastidx=idx;
            while(idx<paramstr.length()&&!Character.isWhitespace(paramstr.charAt(idx))) {
                idx++;
            }
            String name=paramstr.substring(lastidx,idx);
            parsedparams.put(name,Boolean.valueOf(value));
            while(idx<paramstr.length()&&Character.isWhitespace(paramstr.charAt(idx))) {
                idx++;
            }
        }
    }

    public void write(DocsWriter writer) throws Exception{
        writer.write(this);
    }
    
    public File getFile() throws Exception{
        String path = getClassName();
        path = path.replaceAll("\\.", "/");
        path = resolve(path);
        path = source.files.task.getInputSource() + "/" +  path + source.files.task.getSourceExtension();
        return new File(path);
    }

	private String resolve(String path) throws Exception {
		return source.files.task.getSourcePathResolver().resolve(path);
	}

    public String getClassName(){
        String path = new String(parameter); 
        int crossidx=path.indexOf('#');
        if(crossidx>0) {
            path=path.substring(0,crossidx);
        }
        return path;
    }

    public String getMethodName() {
        String path = new String(parameter); 
        int crossidx=path.indexOf('#');
        if(crossidx<0) {
            return null;
        }
        return path.substring(crossidx+1);
    }

    protected void setParamValue(String name,boolean value) {
        parsedparams.put(name,Boolean.valueOf(value));
    }
    
    public boolean getParamValue(String name) {
        Boolean value=(Boolean)parsedparams.get(name);
        return (value==null ? false : value.booleanValue());
    }
}
