/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.writers.*;

public class Condition extends Command{
    
    public void resolve() {
        detectParameters();
    }
    
    public void setCmd() {
        cmd = (byte)'?';
    }
    
    protected void adjustNextTextCommand(int pos) {
        pos = offset + parameter.length() + 3;
        Command textCommand = source.nextCommand(index, TEXT.cmd);
        if (textCommand != null) {
            if (textCommand.offset < pos) {
                textCommand.offset = pos;
            }
        }
    }

    public void write(DocsWriter writer) throws Exception{
        if(end != null) {
            boolean flag = source.files.task.variableIsTrue(new String(parameter));
            for (int i = index + 1; i <= end.index; i++) {
                source.commands[i].ignore = ! flag;
            }
        }
    }
    
}
