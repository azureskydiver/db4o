/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;


public class CascadeDeleteDeleted {
    
    public String name;
    
    public Object untypedMember;
    public CddMember typedMember;
    
    public CascadeDeleteDeleted(){
    }
    
    public CascadeDeleteDeleted(String name){
        this.name = name;
    }
    
    public void configure(){
        Db4o.configure().objectClass(this).cascadeOnDelete(true);
    }
    
    public void store(){
        
        Test.deleteAllInstances(CddMember.class);        
        
        membersFirst("membersFirst commit");
        membersFirst("membersFirst");
        
        twoRef("twoRef");
        twoRef("twoRef commit");
        twoRef("twoRef delete");
        twoRef("twoRef delete commit");
    }

    private void membersFirst(String name){
        CascadeDeleteDeleted cdd = new CascadeDeleteDeleted(name);
        cdd.untypedMember = new CddMember();
        cdd.typedMember = new CddMember();
        Test.store(cdd);
    }

    private void twoRef(String name){
        CascadeDeleteDeleted cdd = new CascadeDeleteDeleted(name);
        cdd.untypedMember = new CddMember();
        cdd.typedMember = new CddMember();
        CascadeDeleteDeleted cdd2 = new CascadeDeleteDeleted(name);
        cdd2.untypedMember = cdd.untypedMember;
        cdd2.typedMember = cdd.typedMember;
        Test.store(cdd);
        Test.store(cdd2);
        
    }
    
    
    public void test(){
        tMembersFirst("membersFirst commit");
        tMembersFirst("membersFirst");
        tTwoRef("twoRef");
        tTwoRef("twoRef commit");
        tTwoRef("twoRef delete");
        tTwoRef("twoRef delete commit");
        Test.ensureOccurrences(CddMember.class, 0);
    }
    
    private void tMembersFirst(String name){
        boolean commit = name.indexOf("commit") > 1;
        ExtObjectContainer oc = Test.objectContainer();
        Query q = oc.query();
        q.constrain(this.getClass());
        q.descend("name").constrain(name);
        ObjectSet objectSet = q.execute();
        CascadeDeleteDeleted cdd = (CascadeDeleteDeleted)objectSet.next();
        oc.delete(cdd.untypedMember);
        oc.delete(cdd.typedMember);
        if(commit){
            oc.commit();
        }
        oc.delete(cdd);
        if(!commit){
            oc.commit();
        }
    }
    
    private void tTwoRef(String name){
        boolean commit = name.indexOf("commit") > 1;
        boolean delete = name.indexOf("delete") > 1;
        ExtObjectContainer oc = Test.objectContainer();
        Query q = oc.query();
        q.constrain(this.getClass());
        q.descend("name").constrain(name);
        ObjectSet objectSet = q.execute();
        CascadeDeleteDeleted cdd = (CascadeDeleteDeleted)objectSet.next();
        CascadeDeleteDeleted cdd2 = (CascadeDeleteDeleted)objectSet.next();
        if(delete){
            oc.delete(cdd.untypedMember);
            oc.delete(cdd.typedMember);
        }
        oc.delete(cdd);
        if(commit){
            oc.commit();
        }
        oc.delete(cdd2);
        if(!commit){
            oc.commit();
        }
    }

    
    public static class CddMember{
        public String name;
        
    }
    
}
