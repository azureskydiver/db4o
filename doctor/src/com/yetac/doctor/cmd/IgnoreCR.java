/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.writers.*;


public class IgnoreCR extends Command {
    
    public void resolve() {
        hide();
    }
    
    public void setCmd() {
        cmd = (byte)'_';
    }

    public void write(DocsWriter writer) throws Exception{
        writer.write(this);
    }

}
