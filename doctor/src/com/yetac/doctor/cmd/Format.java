/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.cmd;


/**
 * 
 */
public abstract class Format extends Command {

    public void resolve() {
        detectParameters();
        if(text == null) {
            text = parameter;
        } else{
            byte[] temp = new byte[parameter.length + text.length + 1];
            System.arraycopy(parameter, 0, temp, 0, parameter.length);
            temp[parameter.length] = (byte)' ';
            System.arraycopy(text, 0, temp, parameter.length + 1, text.length);
            text = temp;
        }
    }
}
