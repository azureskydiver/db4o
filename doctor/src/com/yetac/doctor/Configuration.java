/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor;

import com.yetac.doctor.cmd.*;
import com.yetac.doctor.writers.*;

public abstract class Configuration {

    public static final DocsWriter[] WRITERS           = { 
        new HtmlWriter(),
        new PDFWriter()                                };

    public static final String       FILE_EXTENSION    = "docs";

    public static final byte         COMMAND           = (byte) '.';
    public static final byte         WHITESPACE        = (byte) ' ';
    public static final byte         BR                = (byte) '\n';
    public static final byte         LF                = (byte) '\r';
    public static final byte         BACKSLASH         = (byte) '\\';

    public static final byte[]       NUMBERS           = new byte[10];

    public static final Command[]    COMMANDS          = { new Anchor(), new Bold(),
        new Comment(), new Condition(), new Code(), new Embed(), new End(), new Graphic(), 
        new IgnoreCR(), new Italic(), new Link(), new NewPage(), new OutputConsole(), new Source(), new Variable()    };

    static {

        for (int i = 0; i < NUMBERS.length; i++) {
            NUMBERS[i] = (byte) (("" + i).charAt(0));
        }

        for (int i = 0; i < COMMANDS.length; i++) {
            COMMANDS[i].setCmd();
        }
    }

    public static boolean isWhiteSpace(byte b) {
        return b == WHITESPACE || b == BR || b == LF;
    }
}