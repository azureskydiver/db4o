/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.semaphoreEvent;


class SemaphoreMessage {

    private String info;

    public SemaphoreMessage(String info){
        this.info = info;
    }
    
    public String toString(){
        return "MyClientServerMessage: " + info;
    }

}

