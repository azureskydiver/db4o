package com.yetac.doctor.cmd;

import com.yetac.doctor.writers.DocsWriter;


public class Code extends Format {
    public void write(DocsWriter writer) throws Exception {
        writer.write(this);
    }
}
