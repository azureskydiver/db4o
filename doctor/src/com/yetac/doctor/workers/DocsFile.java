/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.yetac.doctor.workers;

import java.io.*;

import com.yetac.doctor.*;
import com.yetac.doctor.cmd.*;
import com.yetac.doctor.writers.*;

public class DocsFile extends Configuration implements Comparable {
    
    public Files files;
    public String bytes;
    
    public String title;
    public String name;
    public String path;
    
    public Command[] commands;
    public int commandCount;
    
    private static final int INC_COMMANDS = 30;
    
    public int write;
    
    public DocsFile(Files files, File file) {
        this.files = files;
        name = file.getName();
        title = name;
        int pos = title.lastIndexOf(".");
        if(pos > 0) {
            title = title.substring(0, pos);
        }
        path = file.getAbsolutePath();
    }
    
    public void addCommand(Command command, int offset) {
        if(offset >= 0) {
	        if(commandCount >= commands.length) {
	            Command[] temp = commands;
	            commands = new Command[commands.length + INC_COMMANDS];
	            System.arraycopy(temp, 0, commands, 0, temp.length);
	        }
	        commands[commandCount] = command;
	        command.index = commandCount;
	        command.offset = offset;
	        commandCount ++;
	        command.source = this;
        }
    }
    
    public int compareTo(Object o) {
        DocsFile other = (DocsFile)o;
        return name.compareTo(other.name);
   }
    
    public void resolve() throws Exception {
        for (int i = commandCount - 1; i >= 0; i--) {
            commands[i].resolve();
        }
    }
    
    public void setBytes(String bytes) {
        this.bytes = bytes;
        commands = new Command[0];
    }
    
    public void write(DocsWriter writer) throws Exception{
        writer.setSource(this);
        writeCommands(writer);
    }
    
    public void writeEmbedded(DocsWriter writer) throws Exception{
        writer.beginEmbedded(this);
        writeCommands(writer);
        writer.endEmbedded();
    }
    
    public void writeCommands(DocsWriter writer) throws Exception{
        for (int i = 0; i < commandCount; i++) {
            if(commands[i].writeable()) {
                commands[i].write(writer);
            }
        }
    }
    
    public char byteAt(int pos) {
        if(pos < bytes.length()) {
            return bytes.charAt(pos);
        }
        return WHITESPACE;
    }
    
    public Command previousCommand(int index, char cmd) {
        index --;
        while(index >= 0) {
            if(commands[index].cmd == cmd) {
                return commands[index];
            }
            index --;
        }
        return null;
    }
    
    public Command previousCommand(int index) {
        if(index > 0) {
            return commands[index -1];
        }
        return null; 
    }
    
    public Command nextCommand(int index, char cmd) {
        index ++;
        while(index < commandCount) {
            if(commands[index].cmd == cmd) {
                return commands[index];
            }
            index ++;
        }
        return null;
    }
    
    public String toString(){
        return name;
    }
    
    
}
