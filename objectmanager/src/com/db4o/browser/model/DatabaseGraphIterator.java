/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;

import com.db4o.browser.model.nodes.ClassNode;
import com.db4o.browser.model.nodes.IModelNode;
import com.db4o.ext.StoredClass;

/**
 * DatabaseGraphIterator.  A visitor that can traverse the contents of an 
 * object database file.
 *
 * @author djo
 */
public class DatabaseGraphIterator implements IGraphIterator {
    
    private Database database;
    private StoredClass[] start;
    
    private GraphPosition path = new GraphPosition();
    
    private IModelNode[] startModel;
    private IModelNode[] currentFamily;
    private int currentIndex=-1;
    
    /**
     * (non-API)
     * Constructor DatabaseGraphIterator.  Constructs a DatabaseGraphIterator that can
     * traverse all the objects in a database graph.
     * 
     * @param database The Database to traverse
     * @param classes The StoredClasses to consider as the root
     */
    public DatabaseGraphIterator(Database database, StoredClass[] classes) {
        this.database = database;
        this.start = classes;
        
        startModel = new IModelNode[start.length];
        for (int i = 0; i < start.length; i++) {
			startModel[i] = new ClassNode(start[i], database);
		}
        reset();
    }

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#getPath()
	 */
	public GraphPosition getPath() {
		GraphPosition result = new GraphPosition(path);
        result.push(currentFamily, currentIndex);
        return result;
	}

    /* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#setPath(com.db4o.browser.model.GraphPosition)
	 */
	public void setPath(GraphPosition path) {
		this.path = path;
        GraphPathNode currentParent = path.pop();
        
        this.currentFamily = currentParent.children;
        this.currentIndex = currentParent.selectedChild;
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#nextMayHaveChildren()
	 */
	public boolean nextMayHaveChildren() {
        if (!hasNext())
            return false;
		return currentFamily[currentIndex+1].mayHaveChildren();
	}
    
    /* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#previousMayHaveChildren()
	 */
	public boolean previousMayHaveChildren() {
        if (!hasPrevious())
            return false;
		return currentFamily[currentIndex].mayHaveChildren();
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#hasParent()
	 */
	public boolean hasParent() {
		return path.hasParent();
	}

    /* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#reset()
	 */
	public void reset() {
        path = new GraphPosition();
        
        currentFamily = startModel;
        currentIndex=-1;
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#selectChild()
	 */
	public void selectNextChild() {
        if (hasNext()) {
        	IModelNode child = (IModelNode) next();
            path.push(currentFamily, currentIndex);
            currentFamily = child.children();
        } else {
            throw new IllegalArgumentException("There is no next child to select");
        }
        currentIndex=-1;
	}
    
    /* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#selectPreviousChild()
	 */
	public void selectPreviousChild() {
        if (hasPrevious()) {
            IModelNode child = (IModelNode) previous();
            path.push(currentFamily, currentIndex);
            currentFamily = child.children();
        } else {
            throw new IllegalArgumentException("There is no previous child to select");
        }
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#selectParent()
	 */
	public void selectParent() {
        if (!hasParent()) {
            throw new IllegalArgumentException("Already at top level of graph");
        }
		GraphPathNode parentNode = path.pop();
        currentFamily = parentNode.children;
        currentIndex = parentNode.selectedChild;
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#add(java.lang.Object)
	 */
	public void add(Object o) {
		// FIXME: Not implemented (yet)
	}
    
    /* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#numChildren()
	 */
	public int numChildren() {
		return currentFamily.length;
	}
    
	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext() {
		return currentIndex < currentFamily.length-1;
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		return currentIndex >= 0;
	}
    
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
        ++currentIndex;
		return currentFamily[currentIndex];
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		return currentIndex+1;
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
        Object result = currentFamily[currentIndex];
        --currentIndex;
		return result;
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
		return currentIndex;
	}
    
	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
        // Not implemented
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#set(java.lang.Object)
	 */
	public void set(Object o) {
		// Not implemented
	}
}
