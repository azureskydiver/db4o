/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.config;


/**
 * This class provides static constants for the query evaluation
 * modes that db4o supports. 
 * @see QueryConfiguration#evaluationMode(QueryEvaluationMode) 
 */
public class QueryEvaluationMode {
	
	private QueryEvaluationMode(){
		
	}
	
	/**
	 * Constant for immediate query evaluation. The query is executed fully
	 * when Query#execute() is called.
	 * @see QueryConfiguration#evaluationMode(QueryEvaluationMode) 
	 */
	public static final QueryEvaluationMode IMMEDIATE = new QueryEvaluationMode();

	/**
	 * Constant for snapshot query evaluation. When Query#execute() is called,
	 * the query processor chooses the best indexes, does all index processing
	 * and creates a snapshot of the index at this point in time. Non-indexed
	 * constraints are evaluated lazily when the application iterates through 
	 * the {@link ObjectSet} resultset of the query.
	 * @see QueryConfiguration#evaluationMode(QueryEvaluationMode) 
	 */
	public static final QueryEvaluationMode SNAPSHOT = new QueryEvaluationMode();
	
	/**
	 * Constant for lazy query evaluation. When Query#execute() is called, the
	 * query processor only chooses the best index and creates an iterator on 
	 * this index. Indexes and constraints are evaluated lazily when the 
	 * application iterates through the {@link ObjectSet} resultset of the query.
	 * @see QueryConfiguration#evaluationMode(QueryEvaluationMode) 
	 */
	public static final QueryEvaluationMode LAZY = new QueryEvaluationMode();
	
	
    private static final QueryEvaluationMode[] MODES = new QueryEvaluationMode[] {
    	QueryEvaluationMode.IMMEDIATE,
    	QueryEvaluationMode.SNAPSHOT,
    	QueryEvaluationMode.LAZY,
    };
    
    /**
     * internal method, ignore please. 
     */
    public int asInt(){
    	for (int i = 0; i < MODES.length; i++) {
    		if(MODES[i] == this){
    			return i;
    		}
		}
    	throw new IllegalStateException();
    }
    
    /**
     * internal method, ignore please. 
     */
    public static QueryEvaluationMode fromInt(int i){
    	return MODES[i];
    }

}
