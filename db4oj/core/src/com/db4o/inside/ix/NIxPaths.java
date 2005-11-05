/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.ix;

import com.db4o.*;
import com.db4o.foundation.*;


public class NIxPaths {
    
    Tree _paths;
    
    void add(NIxPath path){
        path.i_size = 1;
        path.i_preceding = null;
        path.i_subsequent = null;
        _paths = Tree.add(_paths, path);
    }
    
    void removeRedundancies(){
        
        // This is written very simple for ANDs only first
        
        final Collection4 add = new Collection4();
        final boolean[] stop = new boolean[]{false};
        
        _paths.traverse(new Visitor4() {
            public void visit(Object a_object) {
                if(! stop[0]){
                    NIxPath path = (NIxPath)a_object;
                    if(! path._takePreceding){
                        add.clear();
                    }
                    add.add(path);
                    if(! path._takeSubsequent){
                        stop[0] = true;
                    }
                }
            }
        });
        
        _paths = null;
        Iterator4 i = add.iterator();
        while(i.hasNext()){
            add((NIxPath)i.next());
        }
    }
    
    int count(){
        final NIxPath[] last = new NIxPath[] {null};
        final int[] sum = new int[] { 0 };
        _paths.traverse(new Visitor4() {
            public void visit(Object a_object) {
                NIxPath current = (NIxPath)a_object;
                if(last[0] == null){
                    if(current._takePreceding){
                        sum[0] += countAllPreceding(current._head);
                    }
                }else{
                    if(    (last[0]._takeSubsequent || last[0]._takeMatches) 
                        && (current._takePreceding || current._takeMatches) ){
                        sum[0] += countSpan(current, last[0], current._head, last[0]._head, current._head._next, last[0]._head._next);
                    } else if(last[0]._takeMatches){
                        sum[0] += countAllMatching(last[0]._head);
                    }
                }
                last[0] = current;
            }
        });
        if(last[0]._takeSubsequent){
            sum[0] += countAllSubsequent(last[0]._head);
        }
        return sum[0];
    }
    
    private int countAllSubsequent(NIxPathNode head){
        int count = 0;
        while (head != null) {
            count += head.countSubsequent();
            head = head._next;
        }
        return count;
    }
    
    private int countAllPreceding(NIxPathNode head){
        int count = 0;
        while (head != null) {
            count += head.countPreceding();
            head = head._next;
        }
        return count;
    }
    
    private int countAllMatching(NIxPathNode head){
        int count = 0;
        while (head != null) {
            count += head.countMatching();
            head = head._next;
        }
        return count;
    }
    
    private int countSpan(NIxPath greatPath, NIxPath smallPath, NIxPathNode a_previousGreat, NIxPathNode a_previousSmall,  NIxPathNode a_great, NIxPathNode a_small) {
        if (a_great == null) {
            if (a_small == null) {
                return a_previousGreat.countMatchingSpan(greatPath, smallPath, a_previousSmall);
            } else {
                return countGreater(a_small, a_previousGreat.countMatchingSpan(greatPath, smallPath, a_previousSmall));
            }
        } else if (a_small == null) {
            return countSmaller(a_great, a_previousGreat.countMatchingSpan(greatPath, smallPath, a_previousSmall));
        }
        if (a_great.carriesTheSame(a_small)) {
            return countSpan(greatPath, smallPath, a_great, a_small, a_great._next, a_small._next);
        }
        return a_previousGreat.countMatchingSpan(greatPath, smallPath, a_previousSmall) + countGreater(a_small, 0) + countSmaller(a_great, 0);
    }

    
    private int countGreater(NIxPathNode a_path, int a_sum) {
        if (a_path._next == null) {
            return a_sum + countSubsequent(a_path);
        } else {
            if (a_path._next._tree == a_path._tree.i_preceding) {
                a_sum += countSubsequent(a_path);
            } else {
                a_sum += a_path.countMatching();
            }
            return countGreater(a_path._next, a_sum);
        }
    }
    
    private int countPreceding(NIxPathNode a_path) {
        return Tree.size(a_path._tree.i_preceding) + a_path.countMatching();
    }
    
    private int countSmaller(NIxPathNode a_path, int a_sum) {
        if (a_path._next == null) {
            return a_sum + countPreceding(a_path);
        } else {
            if (a_path._next._tree == a_path._tree.i_subsequent) {
                a_sum += countPreceding(a_path);
            } else {
                a_sum += a_path.countMatching();
            }
            return countSmaller(a_path._next, a_sum);
        }
    }

    private int countSubsequent(NIxPathNode a_path) {
        return Tree.size(a_path._tree.i_subsequent) + a_path.countMatching();
    }
    
    
    

}
