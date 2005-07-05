/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */
package com.db4o.ix;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * Query Index Path
 */
class QxPath extends TreeInt{
    
    private final QxProcessor _processor;
    
    private QCon _constraint;
    
    private final QxPath _parent;
    
    private IxTraverser[] _indexTraversers;
    
    private Tree _candidates; 
    
    
    QxPath(QxProcessor processor, QxPath parent, QCon constraint){
        super(0);
        _processor = processor;
        _parent = parent;
        _constraint = constraint;
    }
    
    void buildPaths(){
        
        int id = _constraint.identityID();
        if(id > 0){
            processChildCandidates(new TreeInt(id));
            return;
        }

        boolean isLeaf = true;
        Iterator4 i = _constraint.iterateChildren();
        while(i.hasNext()){
            isLeaf = false;
            QCon childConstraint = (QCon)i.next();
            if(childConstraint.canLoadByIndex()){
                new QxPath(_processor, this, childConstraint).buildPaths();
            }
        }
        if(! isLeaf){
            return;
        }
        if(! _constraint.canLoadByIndex()){
            return;
        }
        
        if(! _constraint.canBeIndexLeaf()){
            return;
        }
        
        _indexTraversers = new IxTraverser[]{new IxTraverser()};
        
        i_key = ((QConObject)_constraint).findBoundsQuery(_indexTraversers[0]);
        if(i_key >= 0){
            _processor.addPath(this);
        }
    }
    
    
    void load(){
        if(_indexTraversers != null){
            for (int i = 0; i < _indexTraversers.length; i++) {
                _indexTraversers[i].visitAll(new Visitor4() {
                    public void visit(Object a_object) {
                        int id = ((Integer)a_object).intValue();
                        if(_candidates == null){
                            _candidates = new TreeInt(id);
                        }else{
                            _candidates = _candidates.add(new TreeInt(id));
                        }
                    }
                });
            }
        }
        
        if(_parent == null){
            return;
        }
        
        QxPath parentPath = new QxPath(_processor, _parent._parent , _parent._constraint);
        parentPath.processChildCandidates(_candidates);
        return;
        
    }
    
    void processChildCandidates(Tree candidates){
        
        if(candidates == null){
            _processor.addPath(this);
            return;
        }
        
        if(_parent == null){
            _candidates = candidates;
            _processor.addPath(this);
            return;
        }
        
        _indexTraversers = new IxTraverser[candidates.size()];
        final int[] ix = new int[]{0};
        final boolean[] err = new boolean[] {false};
        candidates.traverse(new Visitor4() {
            public void visit(Object a_object) {
                _indexTraversers[ix[0]] = new IxTraverser();
                int count = _indexTraversers[ix[0]++].findBoundsQuery(_constraint, new Integer(((TreeInt)a_object).i_key));
                if(count >= 0){
                    i_key += count;
                }else{
                    err[0] = true;
                }
            }
        });
        if(err[0]){
            return;
        }
        _processor.addPath(this);
    }
    
    

    public boolean isTopLevelComplete() {
        
        if(_parent == null){
            
            //FIXME: and if all joins are evaluated
            
            return true;
            
        }
        return false;
    }
    
    Tree toQCandidates(QCandidates candidates){
        return TreeInt.toQCandidate((TreeInt)_candidates, candidates);
    }
    
    
    

}
