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
        	text=parameter+' '+text;
        }
    }
}
