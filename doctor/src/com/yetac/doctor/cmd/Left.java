/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.writers.*;

public class Left extends Command{
    
    public void resolve() throws Exception {
    }

    public void write(DocsWriter writer) throws Exception {
        writer.write(this);
    }
}
