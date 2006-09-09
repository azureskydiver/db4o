/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

public class BindFileSize {
	
	static final int LENGTH = 10000;
	
	String foo;
	
	public BindFileSize(){
	}
	
	public BindFileSize(int length){
		StringBuffer sb = new StringBuffer(length);
		for (int i = 0; i < length; i++) {
            sb.append("g");
        }  
		this.foo = sb.toString();
	}
    
	public void store(){
		Test.deleteAllInstances(this);
		Test.store(new BindFileSize(LENGTH));
	}
	
	public void testGrowth(){
		int call = 0;
        
        Test.reOpen();
		
		BindFileSize bfs =  (BindFileSize)Test.getOne(this);
		long id = Test.objectContainer().getID(bfs);
		for (int i = 0; i < 12; i++) {
			bfs = new BindFileSize(LENGTH);
			Test.objectContainer().bind(bfs, id);
			Test.objectContainer().set(bfs);
			Test.commit();
			checkFileSize(call++);
			Test.reOpen();
		}
	}
	
	private void checkFileSize(int call){
		if(Test.canCheckFileSize()){
			int newFileLength = Test.fileLength();
			
			// Interesting for manual tests:
			// System.out.println(newFileLength);
			
			if(call == 6){
				// consistency reached, start testing
				jumps = 0;
				fileLength = newFileLength;
			}else if(call > 6){
				if(newFileLength > fileLength){
					if(jumps < 4){
						fileLength = newFileLength;
						jumps ++;
						// allow two further steps in size
						// may be necessary for commit space extension
					}else{
						// now we want constant behaviour
						Test.error();
					}
				}
			}
		}
	}
	
	private static transient int fileLength;
	private static transient int jumps; 



}
