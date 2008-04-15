/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;

import java.lang.reflect.*;

import com.yetac.doctor.writers.*;

public class Link extends Command {

    private static final char[][] EXTERNALS = { "http:".toCharArray(),
        "mailto:".toCharArray(), "news:".toCharArray(), "ftp:".toCharArray(),
        "callto:".toCharArray(), "https:".toCharArray(),};

    public void resolve() {
        detectParameters();
        if (text == null) {
            text = parameter;
        }
    }

    public void write(DocsWriter writer) throws Exception {
        writer.write(this);
    }

    public boolean external() {
        int len = Array.getLength(EXTERNALS);
        for (int i = 0; i < len; i++) {
            boolean take = true;
            if (parameter.length() > EXTERNALS[i].length) {
                for (int j = 0; j < EXTERNALS[i].length; j++) {
                    if (parameter.charAt(j) != EXTERNALS[i][j]) {
                        take = false;
                    }
                }
                if (take) {
                    return true;
                }
            }
        }
        return false;
    }
}