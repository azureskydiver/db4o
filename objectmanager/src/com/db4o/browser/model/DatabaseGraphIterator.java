/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;

import java.util.Iterator;
import java.util.LinkedList;

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
    
    private GraphPosition path = new GraphPosition(); // The parent stack
    
    private IModelNode[] startModel;	// The list of classes in database
    private IModelNode[] currentFamily;	// The current child nodes
    private int currentIndex=-1;		// Index == one less than i.next()
    
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
        result.push(currentFamily, getCurrentIndex());
        return result;
	}

    /* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#setPath(com.db4o.browser.model.GraphPosition)
	 */
	public void setPath(GraphPosition path) {
		GraphPosition copy = new GraphPosition(path);	// Avoid modifying the original
        GraphPathNode currentParent = copy.pop();
        
        this.currentFamily = currentParent.children;
        this.setCurrentIndex(currentParent.selectedChild);

		this.path = copy;
	}
	
	/**
	 * (Non-API) For unit testing purposes only!
	 */
	public IModelNode[] getCurrentFamily() {
		return currentFamily;
	}
	

	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#setSelectedPath(com.db4o.browser.model.GraphPosition)
	 */
	public void setSelectedPath(GraphPosition path) {
		setPath(path);
		fireSelectionChangedEvent();
	}
	
	private LinkedList selectionChangedListeners = new LinkedList();
	
	private void fireSelectionChangedEvent() {
		for (Iterator i = selectionChangedListeners.iterator(); i.hasNext();) {
			IGraphIteratorSelectionListener listener = (IGraphIteratorSelectionListener) i.next();
			listener.selectionChanged();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#addSelectionChangedListener(com.db4o.browser.gui.controllers.tree.SelectionChangedController)
	 */
	public void addSelectionChangedListener(IGraphIteratorSelectionListener selectionListener) {
		selectionChangedListeners.add(selectionListener);
	}
	
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#removeSelectionChangedListener(com.db4o.browser.gui.controllers.tree.SelectionChangedController)
	 */
	public void removeSelectionChangedListener(IGraphIteratorSelectionListener selectionListener) {
		selectionChangedListeners.remove(selectionListener);
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#nextMayHaveChildren()
	 */
	public boolean nextHasChildren() {
        if (!hasNext())
            return false;
		return currentFamily[getCurrentIndex()+1].hasChildren();
	}
    
    /* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#previousMayHaveChildren()
	 */
	public boolean previousHasChildren() {
        if (!hasPrevious())
            return false;
		return currentFamily[getCurrentIndex()].hasChildren();
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
        setCurrentIndex(-1);
	}
    
	/* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#selectChild()
	 */
	public void selectNextChild() {
        if (hasNext()) {
        	IModelNode child = (IModelNode) next();
			previous();
            path.push(currentFamily, getCurrentIndex());
            currentFamily = child.children();
        } else {
            throw new IllegalArgumentException("There is no next child to select");
        }
        setCurrentIndex(-1);
	}
    
    /* (non-Javadoc)
	 * @see com.db4o.browser.model.IGraphIterator#selectPreviousChild()
	 */
	public void selectPreviousChild() {
        if (hasPrevious()) {
            IModelNode child = (IModelNode) previous();
            path.push(currentFamily, getCurrentIndex());
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
        setCurrentIndex(parentNode.selectedChild);
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
		return getCurrentIndex() < currentFamily.length-1;
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#hasPrevious()
	 */
	public boolean hasPrevious() {
		return getCurrentIndex() >= 0;
	}
    
	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	public Object next() {
        setCurrentIndex(getCurrentIndex() + 1);
		return currentFamily[getCurrentIndex()];
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#nextIndex()
	 */
	public int nextIndex() {
		return getCurrentIndex()+1;
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#previous()
	 */
	public Object previous() {
        Object result = currentFamily[getCurrentIndex()];
        setCurrentIndex(getCurrentIndex() - 1);
		return result;
	}
    
	/* (non-Javadoc)
	 * @see java.util.ListIterator#previousIndex()
	 */
	public int previousIndex() {
		return getCurrentIndex();
	}
    
	private void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	private int getCurrentIndex() {
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof DatabaseGraphIterator)) {
			return false;
		}
		
		return obj.hashCode() == hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return path.hashCode() + currentFamily.length + getCurrentIndex() + 1;
	}
}
