/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.writers.*;

public class Anchor extends Command {

    public void resolve() {
        detectParameters();
        if(source.files.anchors.get(parameter) == null) {
            source.files.anchors.put(new String(parameter),source);
        }
    }

    public void write(DocsWriter writer) throws Exception{
        writer.write(this);
    }
}