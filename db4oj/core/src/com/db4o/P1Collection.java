/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.types.*;

/**
 * base class for database aware collections
 * @exclude 
 * @persistent
 */
public abstract class P1Collection extends P1Object implements Db4oCollection{
    
    // This is an off-by-one variable. 
    // 0 means default, use standard activation depth
    // a value greater than 0 means (value - 1)
    private transient int i_activationDepth;
    
    transient boolean i_deleteRemoved;


// Used for asReplicated     
    
//    int _idFrom;
//    
//    int _idTo;
    
    
    public void activationDepth(int a_depth){
        i_activationDepth = a_depth + 1;
    }
    
    public void deleteRemoved(boolean a_flag){
        i_deleteRemoved = a_flag;
    }
    
    int elementActivationDepth(){
        return i_activationDepth - 1;
    }

    
// Test fix for defragment duplication problem. Not really a good idea.     
    
//    Object asReplicated(Transaction trans, Transaction a_trans) {
//        
//        YapStream origin = getTrans().i_stream;
//        YapStream destination = a_trans.i_stream;
//        
//        P1Collection replica = null;
//        
//        if(_idFrom == 0  || origin.getByID(_idFrom) != this){
//            _idFrom = (int)origin.getID(this);
//        }else{
//            if(_idTo != 0){
//                Object obj = destination.getByID(_idTo);
//                if(obj instanceof P1Collection){
//                    replica = (P1Collection)obj;
//                    if(replica._idFrom != this._idFrom){
//                        replica = null;
//                    }
//                }
//            }
//        }
//        
//        if(replica != null){
//            return replica;
//        }
//
//        replica = (P1Collection)replicate(getTrans(), a_trans);
//
//        replica._idFrom = this._idFrom;
//        replica._idTo = (int)destination.getID(replica);
//        
//        this._idTo = replica._idTo;
//        
//        store(1);
//        replica.store(1);
//
//        return replica;
//    }


}
