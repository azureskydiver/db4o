/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.workers.*;
import com.yetac.doctor.writers.*;

public abstract class Command implements Cloneable {

    public DocsFile   source;

    public char       cmd;

    public int        offset;

    public int        index;

    public End        end;

    public boolean    ignore;

    public String     parameter;

    public String     text;

    static final Text TEXT = new Text();

    public Command publicClone() throws CloneNotSupportedException {
        return (Command) this.clone();
    }

    public abstract void resolve() throws Exception;

    public void setCmd() {
        Class clazz = this.getClass();
        String name = clazz.getName();
        int pos = name.lastIndexOf('.');
        String ident = name.substring(pos + 1, pos + 2).toLowerCase();
        char[] chars = new char[1];
        ident.getChars(0, 1, chars, 0);
        cmd =  chars[0];
    }

    public boolean writeable() {
        return !ignore;
    }

    public abstract void write(DocsWriter writer) throws Exception;

    public void detectParameters() {
        int i = offset + 1;
        char[] bytes = source.bytes.toCharArray();

        int search = 0;
        int[] pos = new int[3];

        while (i < bytes.length) {
            if (Character.isWhitespace(bytes[i])) {
                pos[search++] = i + 1;
            }
            if (search == 2) {
                break;
            }
            i++;
        }

        if (i >= bytes.length) {
            pos[search++] = i + 1;
        }

        int endPos = pos[1] + 1;

        if (pos[1] > 0) {
            int len = pos[1] - pos[0] - 1;
            parameter = new String(bytes,pos[0],len);
            if (end != null) {
                len = end.offset - pos[1] - 2;
                if (len > 0) {
                    text = new String(bytes, pos[1], len);
                }
                endPos = end.offset + 3;
            } else {
                endPos = pos[1] - 1;
            }
        }
        adjustNextTextCommand(endPos);
    }

    public void hide() {
        int i = offset + 1;
        char[] bytes = source.bytes.toCharArray();
        while (i < bytes.length && Character.isWhitespace(bytes[i++]));
        i-=1;
        adjustNextTextCommand(i);
    }

    protected void adjustNextTextCommand(int pos) {
        Command textCommand = source.nextCommand(index, TEXT.cmd);
        if (textCommand != null) {
            if (textCommand.offset < pos) {
                textCommand.offset = pos;
            }
        }
    }

    public String toString() {
        return source.bytes.substring(offset, endOffset());
    }
    
    protected int endOffset(){
        int pos = offset + 10;
        if(end != null){
            pos = end.offset;
        }
        return pos;
    }

}