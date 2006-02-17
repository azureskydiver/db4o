/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.workers;

import java.io.*;

import com.yetac.doctor.*;
import com.yetac.doctor.cmd.*;

public class Parser extends Configuration {

    public void parse(DocsFile source) throws IOException, CloneNotSupportedException {
    	BufferedReader in=new BufferedReader(new InputStreamReader(new FileInputStream(source.path),"UTF-8"));
    	StringBuffer buf=new StringBuffer();
    	String curLine=null;
    	while((curLine=in.readLine())!=null) {
    		buf.append(curLine);
            buf.append("\r\n");
    	}
    	in.close();
        char[] bytes = buf.toString().toCharArray();
        source.setBytes(buf.toString());
        
        int inCommand = 0;
        char prev = WHITESPACE;
        
        source.addCommand(new Text(), 0);
        
        for (int i = 0; i < bytes.length; i++) {
            if (inCommand > 0) {
                Command cmd = null;
                for (int j = 0; j < COMMANDS.length; j++) {
                    if (bytes[i] == COMMANDS[j].cmd) {
                        cmd = COMMANDS[j].publicClone();
                        break;
                    }
                }
                if (cmd == null) {
                    for (int j = 0; j < NUMBERS.length; j++) {
                        if (bytes[i] == NUMBERS[j]) {
                            cmd = new Outline(j);
                            break;
                        }
                    }
                }
                if (cmd == null) {
                    if(inCommand > 1) {
                        source.addCommand(new Text(), i - 2);    
                    }
                    inCommand = 0;
                }else {
                    source.addCommand(cmd, i);
                    if(cmd instanceof End) {
                        inCommand = 0;
                        source.addCommand(new Text(), i - 2);
                    }else {
                        inCommand ++;
                    }
                }
            } else {
                if (bytes[i] == COMMAND) {
                    if (Character.isWhitespace(prev)) {
                        inCommand = 1;
                    }else {
                        if(prev == BACKSLASH) {
                            source.addCommand(new Text(),i);
                        }
                    }
                }
            }
            prev = bytes[i];
        }
    }
}