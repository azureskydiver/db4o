/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery.cats;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class TestCatConsistency {
    
    public void configure(){
        Db4o.configure().optimizeNativeQueries(false);
    }
    
    public void store(){
        storeCats();
    }
    
    public void test(){
        
        ExtObjectContainer oc = Test.objectContainer();
        oc.configure().optimizeNativeQueries(true);
        runTests();
        oc.configure().optimizeNativeQueries(false);
        runTests();

    }
    
    
    
    public void runTests(){
        
        expect(new Predicate(){
            public boolean match(Cat cat){
                return cat._age == 7;
            }
        }, null);
        
        expect(new Predicate(){
            public boolean match(Cat cat){
                return cat._age == 1;
            }
        }, new String[]{"Occam", "Vahiné" });
        
        expect(new Predicate(){
            public boolean match(Cat cat){
                return cat._father._age == 1;
            }
        }, new String[]{"Achat", "Acrobat" });
        
        expect(new Predicate(){
            public boolean match(Cat cat){
                return cat._father._father._firstName.equals("Edwin");
            }
        }, new String[]{"Achat", "Acrobat" });
        
        expect(new Predicate(){
            public boolean match(Cat cat){
                return cat._father._father._firstName.equals("Edwin")
                    || cat._father._firstName.equals("Edwin");
            }
        }, new String[]{"Achat", "Acrobat", "Occam" });

        
    }
    
    public void storeCats(){
        
        Cat winni = new Cat();
        winni._sex = Animal.MALE;
        winni._firstName = "Edwin";
        winni._lastName = "Sanddrops";
        winni._age = 12;
        
        Cat bachi = new Cat();
        bachi._sex = Animal.FEMALE;
        bachi._firstName = "Frau Bachmann";
        bachi._lastName = "von der Bärenhöhle";
        bachi._age = 10;
        
        Cat occam = new Cat();
        occam._sex = Animal.MALE;
        occam._firstName = "Occam";
        occam._lastName = "von der Bärenhöhle";
        occam._age = 1;
        occam._father = winni;
        occam._mother = bachi;
        
        Cat zora = new Cat();
        zora._sex = Animal.FEMALE;
        zora._firstName = "Vahiné";
        zora._lastName = "des Fauves et Or";
        zora._age = 1;
        
        Cat achat = new Cat();
        achat._sex = Animal.FEMALE;
        achat._firstName = "Achat";
        achat._lastName = "Leo Lenis";
        achat._father = occam;
        achat._mother = zora;
        
        Cat acrobat = new Cat();
        acrobat._sex = Animal.FEMALE;
        acrobat._firstName = "Acrobat";
        acrobat._lastName = "Leo Lenis";
        acrobat._father = occam;
        acrobat._mother = zora;
        
        Test.store(achat);
        Test.store(acrobat);
    }
    
    private void expect(Predicate predicate, String[] names){
        
        if(names == null){
            names = new String[] {};
        }
        
        List list = Test.objectContainer().query(predicate);
        
        Iterator i = list.iterator();
        while(i.hasNext()){
            Cat cat = (Cat)i.next();
            boolean good = false;
            for (int j = 0; j < names.length; j++) {
                if(names[j] != null){
                    if(cat._firstName.equals(names[j])){
                        names[j] = null;
                        good = true;
                        break;
                    }
                }
            }
            Test.ensure(good);
        }
        for (int j = 0; j < names.length; j++) {
            Test.ensure(names[j] == null);
        }
    }
    
    
    


}
