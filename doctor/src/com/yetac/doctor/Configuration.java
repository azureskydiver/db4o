/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor;

import com.yetac.doctor.cmd.*;
import com.yetac.doctor.writers.*;

public abstract class Configuration {

    public static final DocsWriter[] WRITERS           = { 
        new HtmlWriter(),
        new PDFWriter()                                };

    public static final String       FILE_EXTENSION    = "docs";

    public static final char         COMMAND           = '.';
    public static final char         WHITESPACE        = ' ';
    public static final char         BR                = '\n';
    public static final char         LF                = '\r';
    public static final char         BACKSLASH         = '\\';
    public static final char         TAB               = '\t';

    public static final char[]       NUMBERS           = new char[10];

    public static final Command[]    COMMANDS          = { new Anchor(), new Bold(),
        new Comment(), new Condition(), new Code(), new Embed(), new End(), new Graphic(), 
        new IgnoreCR(), new Italic(), new Link(), new NewPage(), new OutputConsole(), 
        new Quote(), new Source(), new Variable() , new Xamine()   };

    static {

        for (int i = 0; i < NUMBERS.length; i++) {
            NUMBERS[i] = (char)('0'+i);
        }

        for (int i = 0; i < COMMANDS.length; i++) {
            COMMANDS[i].setCmd();
        }
    }
}