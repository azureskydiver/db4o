/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.model;

import java.util.ListIterator;


/**
 * IGraphIterator.  A Visitor object that can traverse an object graph.
 *
 * @author djo
 */
public interface IGraphIterator extends ListIterator {
	/**
     * Method getPath.
     * 
     * Returns the path traversed to get to the current object.
     * 
	 * @return GraphPosition the path to the current object. 
	 */
	public GraphPosition getPath();
    
    /**
	 * Method setPath. Sets the current position in the graph using the
	 * specified GraphPosition.
	 * <p>
	 * Note that no checking is done to make sure that the GraphPosition object
	 * being used to reset the position was created using this IGraphIterator.
	 * If a different IGraphIterator created the GraphPosition object than is the
	 * receiver of this message, the results are undefined.
	 * 
	 * @param path The GraphPosition to use when setting the current position.
	 */
    public void setPath(GraphPosition path);
    
    /**
     * Method hasParent.  Indicates if the current element has a parent.
     * 
     * Returns true if the current element has a parent (is not a root node
     * in this object graph).
     * 
     * @return true if the current object has a parent; false otherwise.
     */
    public boolean hasParent();
    
    /**
     * Method nextMayHaveChildren.  Indicates if the next element could have
     * children.
     * 
     * Returns true if the next element could have at least one child.
     * 
     * @return true if there is a next element and the next element could have 
     * at least one child; false otherwise.
     */
    public boolean nextMayHaveChildren();
    
    /**
     * Method previousMayHaveChildren.  Indicates if the previous element could have
     * children.
     * 
     * Returns true if the previous element could have at least one child.
     * 
     * @return true if there is a and the previous element could have at least 
     * one child; false otherwise.
     */
    public boolean previousMayHaveChildren();
    
    /**
     * Method selectParent().  Traverses up the object graph to the parent
     * of the current node collection.
     */
    public void selectParent();
    
    /**
     * Method selectNextChild().  Makes the next child in the object graph 
     * the new parent, and selects its children to iterate over.
     */
    public void selectNextChild();
    
    /**
	 * Method selectPreviousChild(). Makes the previous child in the object
	 * graph the new parent, and selects its children to iterate over.
	 */
    public void selectPreviousChild();
    
    /**
     * Method reset.  Reset the graph visitor to the top-level set of nodes.
     */
    public void reset();
    
    /**
     * Method numChildren.  Returns the number of current child elements.
     * @return
     */
    public int numChildren();
}
