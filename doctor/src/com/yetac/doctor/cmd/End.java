/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.*;
import com.yetac.doctor.writers.*;

public class End extends Command{
    
    public void setCmd() {
        cmd = Configuration.COMMAND;
    }

    public void resolve() {
        char identifier = source.byteAt(offset +1);
        Command command = null;
        int textOffsetInc = 1;
        if(! Character.isWhitespace(identifier)) {
            command = source.previousCommand(index, identifier);
            textOffsetInc = 2;
        }else {
            command = source.previousCommand(index);
            while(command != null && command.cmd == TEXT.cmd) {
                command = source.previousCommand(command.index);
            }
            if(identifier == Configuration.WHITESPACE) {
                textOffsetInc = 2;
            }
        }
        if(command != null) {
            command.end = this;
        }
        Command textCommand = source.nextCommand(index, TEXT.cmd);
        if(textCommand != null) {
            textCommand.offset = offset + textOffsetInc;
        }
    }

    public void write(DocsWriter writer) throws Exception{
    }
}
