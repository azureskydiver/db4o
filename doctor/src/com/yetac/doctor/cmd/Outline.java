/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.*;
import com.yetac.doctor.writers.*;

public class Outline extends Command{
    
    public int level;
    
    public Outline(int level) {
        this.level = level;
        cmd = Configuration.NUMBERS[level];
    }

    public void resolve() {
        detectParameters();
        if(text != null) {
            parameter = parameter+' '+text;
        }
    }

    public void write(DocsWriter writer) throws Exception{
        writer.write(this);
    }
    

}
