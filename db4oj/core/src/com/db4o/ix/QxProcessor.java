/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.ix;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class QxProcessor {
    
    private Tree _paths;
    private QxPath _best;

    void addPath(QxPath path){
        _paths = Tree.add(_paths, path);
    }
    
    private void buildPaths(QCandidates candidates){
        Iterator4 i = candidates.iterateConstraints();
        while(i.hasNext()){
            QCon qCon = (QCon)i.next();
            qCon.setCandidates(candidates);
            if(! qCon.hasJoins()){
                new QxPath(this, null, qCon).buildPaths();
            }
        }
    }

    public boolean run(QCandidates candidates){
        buildPaths(candidates);
        if(_paths == null){
            return false;
        }
        return chooseBestPath();
    }
    
    private boolean chooseBestPath(){
        while(_paths != null){
            QxPath path = (QxPath)_paths.first();
            _paths = _paths.removeFirst();
            if(path.isTopLevelComplete()){
                _best = path;
                return true;
            }
            path.load();
        }
        return false;
    }
    
    public Tree toQCandidates(QCandidates candidates){
        return _best.toQCandidates(candidates);
    }

}
