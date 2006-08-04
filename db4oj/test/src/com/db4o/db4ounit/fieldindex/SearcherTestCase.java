/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.fieldindex;

import com.db4o.inside.btree.*;

import db4ounit.*;


public class SearcherTestCase implements TestCase, TestLifeCycle{
    
    private Searcher _searcher;
    
    private final int FIRST = 4;
    
    private final int LAST = 11;
    
    private final int[] EVEN_VALUES = new int[] {4, 7, 9, 11};
    
    private final int[] ODD_VALUES = new int[] {4, 7, 8, 9, 11};

    private final int[] NON_MATCHES = new int[] {3, 5, 6, 10, 12};

    private final int[] MATCHES = new int[] {4, 7, 9, 11};
    
    private final int BEFORE = FIRST - 1;
    
    private final int BEYOND = LAST + 1;
    
    public void ttestPrintResults(){
        // not a test, but nice to visualize
        int[] evenValues = new int[] {4, 7, 9, 11};
        int[] searches = new int[]{3, 4, 5, 7, 10, 11, 12};
        for (int i = 0; i < searches.length; i++) {
            int res = search(evenValues, searches[i]);
            System.out.println(res);    
        }
    }
    
    public void testCursorEndsOnSmaller(){
        Assert.areEqual(0, search(EVEN_VALUES, 6));
        Assert.areEqual(0, search(ODD_VALUES, 6));
        Assert.areEqual(2, search(EVEN_VALUES, 10));
        Assert.areEqual(3, search(ODD_VALUES, 10));
    }
    
    public void testMatchEven(){
        assertMatch(EVEN_VALUES);
    }
    
    public void testMatchOdd(){
        assertMatch(ODD_VALUES);
    }
    
    public void testNoMatchEven(){
        assertNoMatch(EVEN_VALUES);
    }
    
    public void testNoMatchOdd(){
        assertNoMatch(ODD_VALUES);
    }
    
    public void testBeyondEven(){
        assertBeyond(EVEN_VALUES);
    }
    
    public void testBeyondOdd(){
        assertBeyond(ODD_VALUES);
    }
    
    public void testNotBeyondEven(){
        assertNotBeyond(EVEN_VALUES);
    }
    
    public void testNotBeyondOdd(){
        assertNotBeyond(ODD_VALUES);
    }

    public void testBeforeEven(){
        assertBefore(EVEN_VALUES);
    }
    
    public void testBeforeOdd(){
        assertBefore(ODD_VALUES);
    }
    
    public void testNotBeforeEven(){
        assertNotBefore(EVEN_VALUES);
    }
    
    public void testNotBeforeOdd(){
        assertNotBefore(ODD_VALUES);
    }
    
    public void testEmptySet(){
        _searcher = new Searcher(SearchTarget.ANY, 0);
        
        if(_searcher.incomplete()){
            Assert.fail();
        }
        
        Assert.areEqual(0, _searcher.cursor());

        
    }


    private void assertMatch(int[] values) {
        for (int i = 0; i < MATCHES.length; i++) {
            int res = search(values, MATCHES[i]);
            Assert.isTrue(_searcher.foundMatch());
        }
    }

    private void assertNoMatch(int[] values) {
        for (int i = 0; i < NON_MATCHES.length; i++) {
            int res = search(values, NON_MATCHES[i]);
            Assert.isFalse(_searcher.foundMatch());
        }
    }
    
    private void assertBeyond(int[] values) {
        int res = search(values, BEYOND);
        Assert.areEqual(values.length - 1, res);
        Assert.isTrue(_searcher.beyondLast());
    }

    private void assertNotBeyond(int[] values) {
        int res = search(values, LAST);
        Assert.areEqual(values.length - 1, res);
        Assert.isFalse(_searcher.beyondLast());
    }
    
    private void assertBefore(int[] values) {
        int res = search(values, BEFORE);
        Assert.areEqual(0, res);
        Assert.isTrue(_searcher.beforeFirst());
    }

    private void assertNotBefore(int[] values) {
        int res = search(values, FIRST);
        Assert.areEqual(0, res);
        Assert.isFalse(_searcher.beforeFirst());
    }

    
    
    
    private int search(int[] values, int value){
        
        _searcher = new Searcher(SearchTarget.ANY, values.length);
        
        while(_searcher.incomplete()){
            _searcher.resultIs( values[_searcher.cursor()] - value );
        }
        
        return _searcher.cursor();
    }

    public void setUp() throws Exception {
        _searcher = null;
    }

    public void tearDown() throws Exception {
        
    }

}
