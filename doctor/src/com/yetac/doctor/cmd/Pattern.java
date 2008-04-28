/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.writers.DocsWriter;

public class Pattern extends Command {

    public void resolve() {
        detectParameters();
        String fieldName = new String(parameter);
        System.out.println(fieldName);
        System.out.println(source.files.task.getPattern(fieldName));
        text = (String)source.files.task.getPattern(fieldName);
    }
    
    public void write(DocsWriter writer) throws Exception {
        writer.write(this);
    }

}