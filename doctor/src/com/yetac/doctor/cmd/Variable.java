/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.workers.*;
import com.yetac.doctor.writers.*;

public class Variable extends Command {

    public void resolve() {
        detectParameters();
        text = Variables.getVariable(source.files.task, parameter);
    }
    
    public void setCmd() {
        cmd = (byte)'#';
    }

    public void write(DocsWriter writer) throws Exception {
        writer.write(this);
    }

}