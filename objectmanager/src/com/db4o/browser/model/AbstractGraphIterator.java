/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;

import java.util.Iterator;
import java.util.LinkedList;

import com.db4o.browser.model.nodes.IModelNode;

public class AbstractGraphIterator implements IGraphIterator {

    protected IDatabase database;
    protected IModelNode[] startModel;
    
    private GraphPosition path = new GraphPosition();
    private IModelNode[] currentFamily;
    private int currentIndex = -1;

    public GraphPosition getPath() {
    	GraphPosition result = new GraphPosition(path);
        result.push(currentFamily, getCurrentIndex());
        return result;
    }

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

    public void addSelectionChangedListener(IGraphIteratorSelectionListener selectionListener) {
    	selectionChangedListeners.add(selectionListener);
    }

    public void removeSelectionChangedListener(IGraphIteratorSelectionListener selectionListener) {
    	selectionChangedListeners.remove(selectionListener);
    }

    public boolean nextHasChildren() {
        if (!hasNext())
            return false;
    	return currentFamily[getCurrentIndex()+1].hasChildren();
    }

    public boolean previousHasChildren() {
        if (!hasPrevious())
            return false;
    	return currentFamily[getCurrentIndex()].hasChildren();
    }

    public boolean hasParent() {
    	return path.hasParent();
    }

    public void reset() {
        path = new GraphPosition();
        
        currentFamily = startModel;
        setCurrentIndex(-1);
    }

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

    public void selectPreviousChild() {
        if (hasPrevious()) {
            IModelNode child = (IModelNode) previous();
            path.push(currentFamily, getCurrentIndex());
            currentFamily = child.children();
        } else {
            throw new IllegalArgumentException("There is no previous child to select");
        }
    }

    public void selectParent() {
        if (!hasParent()) {
            throw new IllegalArgumentException("Already at top level of graph");
        }
    	GraphPathNode parentNode = path.pop();
        currentFamily = parentNode.children;
        setCurrentIndex(parentNode.selectedChild);
    }

    public void add(Object o) {
    	// FIXME: Not implemented (yet)
    }

    public int numChildren() {
    	return currentFamily.length;
    }

    public boolean hasNext() {
    	return getCurrentIndex() < currentFamily.length-1;
    }

    public boolean hasPrevious() {
    	return getCurrentIndex() >= 0;
    }

    public Object next() {
        setCurrentIndex(getCurrentIndex() + 1);
    	return currentFamily[getCurrentIndex()];
    }

    public int nextIndex() {
    	return getCurrentIndex()+1;
    }

    public Object previous() {
        Object result = currentFamily[getCurrentIndex()];
        setCurrentIndex(getCurrentIndex() - 1);
    	return result;
    }

    public int previousIndex() {
    	return getCurrentIndex();
    }

    private void setCurrentIndex(int currentIndex) {
    	this.currentIndex = currentIndex;
    }

    private int getCurrentIndex() {
    	return currentIndex;
    }

    public void remove() {
        // Not implemented
    }

    public void set(Object o) {
    	// Not implemented
    }

    public boolean equals(Object obj) {
    	if (!(obj instanceof DatabaseGraphIterator)) {
    		return false;
    	}
    	
    	return obj.hashCode() == hashCode();
    }

    public int hashCode() {
    	return path.hashCode() + currentFamily.length + getCurrentIndex() + 1;
    }

}
