/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4odoc.f1.messaging;


class MyClientServerMessage {

    private String info;

    public MyClientServerMessage(String info){
        this.info = info;
    }
    
    public String toString(){
        return "MyClientServerMessage: " + info;
    }

}

