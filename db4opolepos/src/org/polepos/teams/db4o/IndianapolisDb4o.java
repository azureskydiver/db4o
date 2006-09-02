/* 
This file is part of the PolePosition database benchmark
http://www.polepos.org

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */


package org.polepos.teams.db4o;

import org.polepos.circuits.indianapolis.*;
import org.polepos.framework.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;


public class IndianapolisDb4o extends Db4oDriver implements IndianapolisDriver{
    
    private static int maximumPayload;
    
    public void takeSeatIn(Car car, TurnSetup setup) throws CarMotorFailureException{
        indexField(fieldNext());
        indexField(fieldPayload());
        super.takeSeatIn(car, setup);
    }
    
    private void indexField(String fieldName){
        ObjectClass objectClass = Db4o.configure().objectClass( IndianapolisList.class );
        objectClass.objectField( fieldName).indexed( true );
    }

    public void write() {
        IndianapolisList list = IndianapolisList.generate(setup().getObjectCount());
        maximumPayload = list.getPayload();
        begin();
        store(list);
        commit();
    }
    
    public void queryRange(){
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            Query q = db().query();
            q.constrain(IndianapolisList.class);
            Query qPayload = q.descend(fieldPayload());
            qPayload.constrain(new Integer(1)).greater();
            qPayload.constrain(new Integer(3)).smaller();
            doQuery(q);
        }
    }
    
    public void query5Links(){
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            Query q = db().query();
            Query qChild = q;
            for (int j = 0; j < 5; j++) {
                qChild = qChild.descend(fieldNext());
            }
            qChild.descend(fieldPayload()).constrain(new Integer(1));
            doQuery(q);
        }
    }
    
    public void queryPreferShortPath(){
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            Query q = db().query();
            q.constrain(IndianapolisList.class);
            q.descend(fieldNext()).descend(fieldNext()).descend(fieldPayload()).constrain(new Integer(maximumPayload - 4));
            q.descend(fieldNext()).descend(fieldPayload()).constrain(new Integer(maximumPayload - 2));
            doQuery(q);
        }
    }
    
    public void queryOr(){
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            Query q = db().query();
            q.constrain(IndianapolisList.class);
            Constraint cMax = q.descend(fieldPayload()).constrain(new Integer(maximumPayload));
            Constraint cMin = q.descend(fieldPayload()).constrain(new Integer(1));
            cMax.or(cMin);
            doQuery(q);
        }
    }
    
    public void queryOrRange(){
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            Query q = db().query();
            q.constrain(IndianapolisList.class);
            Constraint c1 = q.descend(fieldPayload()).constrain(new Integer(1)).greater();
            Constraint c2 = q.descend(fieldPayload()).constrain(new Integer(3)).smaller();
            Constraint c3 = q.descend(fieldPayload()).constrain(new Integer(maximumPayload - 2)).greater();
            Constraint c4 = q.descend(fieldPayload()).constrain(new Integer(maximumPayload)).smaller();
            Constraint cc1 = c1.and(c2);
            Constraint cc2 = c3.and(c4);
            cc1.or(cc2);
            doQuery(q);
        }
    }
    
    public void queryNotGreater(){
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            Query q = db().query();
            q.constrain(IndianapolisList.class);
            q.descend(fieldPayload()).constrain(new Integer(2)).greater().not();
            doQuery(q);
        }
    }
    
    public void queryNotRange(){
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            Query q = db().query();
            q.constrain(IndianapolisList.class);
            Constraint c1 = q.descend(fieldPayload()).constrain(new Integer(2)).greater();
            Constraint c2 = q.descend(fieldPayload()).constrain(new Integer(maximumPayload)).smaller();
            c1.and(c2).not();
            doQuery(q);
        }
    }
    
    public void queryOrTwoLevels(){
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            Query q = db().query();
            q.constrain(IndianapolisList.class);
            Constraint c1 = q.descend(fieldPayload()).constrain(new Integer(2)).smaller();
            Constraint c2 = q.descend(fieldNext()).descend(fieldPayload()).constrain(new Integer(maximumPayload - 2)).greater();
            c1.or(c2);
            doQuery(q);
        }
    }
    
    public void addSingleObjectAndCommit(){
        begin();
        int count = setup().getSelectCount();
        for (int i = 1; i <= count; i++) {
            store(new IndianapolisList());
            commit();
        }
    }
    
    private String fieldNext(){
        return IndianapolisList.FIELD_NEXT;
    }
    
    private String fieldPayload(){
        return IndianapolisList.FIELD_PAYLOAD;
    }

}
