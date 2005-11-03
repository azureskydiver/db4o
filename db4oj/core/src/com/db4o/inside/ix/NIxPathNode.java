/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.ix;

import com.db4o.*;


public class NIxPathNode {
    
    IxTree              _tree;
    
    int                 _comparisonResult;

    int[]               _lowerAndUpperMatch;

    NIxPathNode         _next;
    

    /**
     * returns 0, if keys are equal  
     * returns negative if compared key (a_to) is smaller
     * returns positive if compared key (a_to) is greater
     */
    public int compare(NIxPathNode other) {
        if(_next == null){
            
            if(other._next != null){
                return other.ascending() ? 1 : -1; 
            }
            
            if(_lowerAndUpperMatch == null){
                if(Debug.ixTrees){
                    Debug.expect(other._lowerAndUpperMatch == null);
                }
                return 0;
            }
            
            if(_lowerAndUpperMatch[0] != other._lowerAndUpperMatch[0]){
                return other._lowerAndUpperMatch[0] - _lowerAndUpperMatch[0]; 
            }
            
            if(_lowerAndUpperMatch[1] != other._lowerAndUpperMatch[1]){
                
                // Will this ever happen?
                
                return other._lowerAndUpperMatch[1] - _lowerAndUpperMatch[1]; 
            }
            
            return 0;
        }
        
        if(other._next == null){
            return ascending() ? -1 : 1;
        }
        
        IxTree otherNext = other._next._tree;
        
        if(_tree.i_subsequent == otherNext){
            return 1;
        }

        if(Debug.ixTrees){
            Debug.expect(_tree.i_preceding == otherNext);
        }
        
        return -1;
    }
    
    boolean ascending(){
        return _tree.i_subsequent == _next._tree;
    }
    

}
