/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.yetac.doctor.workers;

import com.yetac.doctor.*;

public class Variables extends Configuration{
    
    public static byte[] getVariable(Doctor task, byte[] parameter) {
        String fieldName = new String(parameter);
        try {
            Class clazz = task.getClass();
            String str = (String)clazz.getDeclaredField(fieldName).get(task);
            return str.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    
    
    

}
