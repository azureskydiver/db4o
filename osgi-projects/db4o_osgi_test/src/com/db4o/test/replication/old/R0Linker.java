/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.test.replication.old;

import com.db4o.*;


class R0Linker {
    
    R0 r0;
    R1 r1;
    R2 r2;
    R3 r3;
    R4 r4;
    
    R0Linker(){
        r0 = new R0();
        r1 = new R1();
        r2 = new R2();
        r3 = new R3();
        r4 = new R4();
    }
    
    void setNames(String name){
        r0.name = "0" + name;
        r1.name = "1" + name;
        r2.name = "2" + name;
        r3.name = "3" + name;
        r4.name = "4" + name;
    }
    
    void linkCircles(){
        linkList();
        r1.circle1 = r0;
        r2.circle2 = r0;
        r3.circle3 = r0;
        r4.circle4 = r0;
    }
    
    void linkList(){
        r0.r1 = r1;
        r1.r2 = r2;
        r2.r3 = r3;
        r3.r4 = r4;
    }
    
    void linkThis(){
        r0.r0 = r0;
        r1.r1 = r1;
        r2.r2 = r2;
        r3.r3 = r3;
        r4.r4 = r4;
    }
    
    void linkBack(){
        r1.r0 = r0;
        r2.r1 = r1;
        r3.r2 = r2;
        r4.r3 = r3;
    }
    
    public void store(ObjectContainer oc){
        oc.set(r4);
        oc.set(r3);
        oc.set(r2);
        oc.set(r1);
        oc.set(r0);
    }
    
    

}
