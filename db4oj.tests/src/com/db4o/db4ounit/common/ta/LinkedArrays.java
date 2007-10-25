/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import com.db4o.activation.*;
import com.db4o.ta.*;

import db4ounit.*;


public class LinkedArrays implements CanAssertActivationDepth {
    
    
    
    public static class Item implements CanAssertActivationDepth{
        
        public String _name;
        
        public LinkedArrays _linkedArrays;
        
        public Item(){
            
        }
        
        public Item(int depth){
            if(depth > 1){
                _name = new Integer(depth).toString();
                _linkedArrays = newLinkedArrays(depth -1);
            }
        }

        public void assertActivationDepth(int depth) {
            nullAssert(_name, depth);
            nullAssert(_linkedArrays, depth);
            if(depth < 1){
                return;
            }
            recurseAssertActivationDepth(_linkedArrays, depth);
        }
        
    }
    
    
    public static class ActivatableItem implements Activatable, CanAssertActivationDepth {
        
        public String _name;
        
        public LinkedArrays _linkedArrays;
        
        public ActivatableItem(){
            
        }
        
        public ActivatableItem(int depth){
            if(depth > 1){
                _name = new Integer(depth).toString();
                _linkedArrays = newLinkedArrays(depth - 1);
            }
        }
        
        private transient Activator _activator;
        
        public void activate() {
            if(_activator != null) {
                _activator.activate();
            }
        }

        public void bind(Activator activator) {
            if(_activator != null || activator == null) {
                throw new IllegalStateException();
            }
            _activator = activator;
        }

        public void assertActivationDepth(int depth) {
            nullAssert(_name, depth);
            nullAssert(_linkedArrays, depth);
            if(depth < 1){
                return;
            }
            recurseAssertActivationDepth(_linkedArrays,depth );
        }
        
    }
    
    
    public LinkedArrays _next;
    
    public Object _objectArray;
    
    public Object[] _untypedArray;
    
    public String[] _stringArray;
    
    public int[] _intArray;
    
    public Item[] _itemArray;
    
    public ActivatableItem[] _activatableItemArray;
    
    public LinkedArrays[] _linkedArrays;
    
    
    public static LinkedArrays newLinkedArrays(int depth){
        
        if(depth < 1){
            return null;
        }
        
        LinkedArrays la = new LinkedArrays();
        
        depth--;
        
        if(depth < 1){
            return la;
        }
        
        la._next = newLinkedArrays(depth);
        
        depth--;
        
        la._objectArray = new Object[] {newItem(depth)};
        la._untypedArray = new Object[] {newItem(depth)};
        la._stringArray = new String[] { new Integer(depth).toString()};
        la._intArray = new int[] {depth};
        la._itemArray = new Item[]{newItem(depth)};
        la._activatableItemArray = new ActivatableItem[] { newActivatableItem(depth) };
        la._linkedArrays = new LinkedArrays[] { newLinkedArrays(depth)};
        
        return la;
    }
    
    private static Item newItem(int depth){
        if(depth < 1){
            return null;
        }
        return new Item(depth);
    }
    
    private static ActivatableItem newActivatableItem(int depth){
        if(depth < 1){
            return null;
        }
        return new ActivatableItem(depth);
    }


    public void assertActivationDepth(int depth) {
        nullAssert(_next, depth);
        nullAssert(_objectArray, depth);
        nullAssert(_untypedArray, depth);
        nullAssert(_stringArray, depth);
        nullAssert(_intArray, depth);
        nullAssert(_itemArray, depth);
        nullAssert(_linkedArrays, depth);
        nullAssert(_activatableItemArray, depth);

        if(depth < 1){
            return;
        }
        
        depth--;
        
        Assert.isNotNull(_stringArray[0]);
        Assert.areEqual(depth, _intArray[0]);
        
        recurseAssertActivationDepth(((Object[])_objectArray)[0], depth);
        recurseAssertActivationDepth(_untypedArray[0], depth);
        recurseAssertActivationDepth(_itemArray[0], depth);
        recurseAssertActivationDepth(_activatableItemArray[0], depth);
        recurseAssertActivationDepth(_linkedArrays[0], depth);
        
    }
    
    static void recurseAssertActivationDepth(Object obj, int depth){
        nullAssert(obj, depth);
        if(obj == null){
            return;
        }
        ((CanAssertActivationDepth)obj).assertActivationDepth(depth-1);
    }
    
    static void nullAssert(Object obj, int depth){
        if(depth < 1){
            Assert.isNull(obj);
        }else{
            Assert.isNotNull(obj);
        }
    }
    
}
