/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import com.yetac.doctor.writers.*;

public class Bold extends Format {
    
    public void write(DocsWriter writer) throws Exception{
        writer.write(this);
    }
}