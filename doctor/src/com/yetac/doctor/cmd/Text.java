/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.*;
import com.yetac.doctor.writers.*;

public class Text extends Command {

    public int offsetEnd;

    public void resolve() {
        if (index < source.commandCount - 1) {
            Command nextCommand = source.commands[index + 1];
            offsetEnd = nextCommand.offset - 2;
            if(offsetEnd >= 0 && source.bytes[offsetEnd] == Configuration.WHITESPACE) {
                offsetEnd--;
            }
        } else {
            offsetEnd = source.bytes.length - 1;
        }
    }

    public boolean writeable() {
        return offsetEnd > offset && super.writeable();
    }

    public void write(DocsWriter writer) throws Exception{
        writer.write(this);
    }
}