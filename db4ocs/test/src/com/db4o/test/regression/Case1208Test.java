package com.db4o.test.regression;

import java.io.File;
import java.util.Date;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;


/*
	This is a test that performs a big amount of 
	commit and rollback operations with *** db4o 5.5 database for java jdk1.2 ***.
	It consists in search all filenames over a filesystem
	and store these names in the database.
	The size of transactions and the type of object
	used to wrap filenames changes depending on the system
	time during executions of the test.
	Note that exists two methods doing the same thing, changing just
	the place where commit/rollback size controller is updated.
	So, in the "main" method you can choose between call the
	"saveFileNamesForTransactionProblem" or "saveFileNamesForDefragProblem" methods.
	Using "saveFileNamesForTransactionProblem" method you will get the following exception:
	=======================================================================================
	[db4o 5.5.1   2006-10-07 13:45:03] 
	 Uncaught Exception. Engine closed.
	[db4o 5.5.1   2006-10-07 13:45:03] 
	 Please mail the following to exception@db4o.com:
	 <db4o 5.5.1 stacktrace>
	java.lang.NullPointerException
		at com.db4o.internal.btree.BTreeNode.seekKey(Unknown Source)
		at com.db4o.internal.btree.BTreeNode.seekAfterKey(Unknown Source)
		at com.db4o.internal.btree.BTreeNode.seekChild(Unknown Source)
		at com.db4o.internal.btree.BTreeNode.childID(Unknown Source)
		at com.db4o.internal.btree.BTreeNode.child(Unknown Source)
		at com.db4o.internal.btree.BTreeNode.add(Unknown Source)
		...
	======================================================================================
	
	Using "saveFileNamesForTransactionProblem" method you will not get any exception during the test,
	but if you try to defrag the generated database, you will get the following exception:
	======================================================================================
	java.lang.ArrayIndexOutOfBoundsException: 69625
	at com.db4o.YapClass$3.visit(Unknown Source)
	at com.db4o.internal.btree.BTreeNode.traverseKeys(Unknown Source)
	at com.db4o.internal.btree.BTreeNode.traverseKeys(Unknown Source)
	at com.db4o.internal.btree.BTreeNode.traverseKeys(Unknown Source)
	at com.db4o.internal.btree.BTree.traverseKeys(Unknown Source)
	======================================================================================
	For defragment, I used just the following code: new Defragment().run("/testdb.yap", true);
	
	Remember that this test was made in a filesystems with 135000 files.
*/

public class Case1208Test {
	
	private ObjectContainer oc;
	private int recordCounter = 0;
	private int counterLimit = 50;
	private int filesFound = 0;
	private int rollbackCounter = 0;
	private int commitCounter = 0;
	private int partialCommitedRecords = 0;
	private int totalCommitedRecords = 0;
	
	public void saveFileNamesForDefragProblem(File root) throws Exception {

		if (!root.isDirectory()) {
			throw new Exception("root must be a directory!");
		}
		
		File[] rootFiles = root.listFiles();
		if (rootFiles != null) {
			for (int fileCount = 0; fileCount < rootFiles.length; fileCount++) {
				if (rootFiles[fileCount].isDirectory()) {
					this.saveFileNamesForDefragProblem(rootFiles[fileCount]);
				}
				this.doSet(this.getObject(rootFiles[fileCount].getAbsolutePath()));
				this.recordCounter++;
				this.controlTransaction();
			}
		}
		this.doSet(this.getObject(root.getAbsolutePath()));
		this.recordCounter++;
		this.controlTransaction();
	}
	
	public void saveFileNamesForTransactionProblem(File root) throws Exception {

		this.recordCounter++;
		
		if (!root.isDirectory()) {
			throw new Exception("root must be a directory!");
		}
		
		File[] rootFiles = root.listFiles();
		if (rootFiles != null) {
			for (int fileCount = 0; fileCount < rootFiles.length; fileCount++) {
				if (rootFiles[fileCount].isDirectory()) {
					this.saveFileNamesForTransactionProblem(rootFiles[fileCount]);
				}
				this.doSet(this.getObject(rootFiles[fileCount].getAbsolutePath()));
				this.controlTransaction();
			}
		}
		this.doSet(this.getObject(root.getAbsolutePath()));
		this.controlTransaction();
	}
	
	public void controlTransaction() {
		if (this.recordCounter > this.counterLimit) {
			System.out.println("=======[TRANSACTION ENDED]==[SIZE: " + this.counterLimit + "]=========================");
			System.out.println("Files found until here: " + this.filesFound);
			if ((System.currentTimeMillis() % 2) == 0) {
				System.out.println("executing commit number [" + ++this.commitCounter + "]...");
				this.oc.commit();
				this.totalCommitedRecords += this.partialCommitedRecords;
				this.counterLimit -= 2;
			} else {
				System.out.println("executing rollback number [" + ++this.rollbackCounter + "]...");
				this.oc.rollback();
				this.counterLimit += 5;
			}
			this.partialCommitedRecords = 0;
			this.recordCounter = 0;
		}
	}
	
	public void doSet(TestWrapperOne testObject) {
		try {
			//System.out.println("setting...");
			this.filesFound++;
			this.partialCommitedRecords++;
			this.oc.set(testObject);
		}
		catch (Exception e)
		{
			System.out.println(testObject.getFileName());
			System.out.println(e.getMessage());
		}

	}
	
	public void openDB(String dbname) {
		this.oc = Db4o.openFile(dbname);
	}
	
	public void closeDB() {
		this.oc.rollback();
		this.oc.close();
	}
	
	public TestWrapperOne getObject(String filename) {
		TestWrapperOne testObject;
		if ((System.currentTimeMillis() % 2) == 0) {
			testObject = new TestWrapperOne(filename);
		} else {
			testObject = new TestWrapperTwo(filename);
		}
		
		return testObject;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
        new Case1208Test().originalTest();
	}

	private void originalTest() {
		System.out.println("Commit/rollback test started at: " + (new Date()).toString());
        Case1208Test t = new Case1208Test();
        try {
        	File dbfile = new File("/testdb.yap");
        	dbfile.delete();
			File root = new File("/");
	        t.openDB(dbfile.getAbsolutePath());
	        //t.saveFileNamesForTransactionProblem(root);
	        t.saveFileNamesForDefragProblem(root);
	        t.closeDB();
	        System.out.println("Files found: " + t.filesFound);
	        System.out.println("Commited objects [" + t.totalCommitedRecords + "]");
	        t.openDB(dbfile.getAbsolutePath());
	        t.queryObjetcs();
	        t.closeDB();
        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("Files found before the exception: " + t.filesFound);
        } finally {
        	System.out.println("Commit and rollback test ended at: " + (new Date()).toString());
        }
	}
	
	public void queryObjetcs() {
		System.out.println("TestWrapperOne found in database: " + this.oc.query(TestWrapperOne.class).size() + "(*** this number must be equal to commited objects number ***)");
		System.out.println("TestWrapperTwo found in database: " + this.oc.query(TestWrapperTwo.class).size());
	}
	
}

class TestWrapperOne {
	private String filename;
	private int nameSize;
	
	public TestWrapperOne(String filename) {
		this.filename = filename;
		this.nameSize = filename.length();
	}
	
	public void setNameSize(int nameSize) {
		this.nameSize = nameSize;
	}
	
	public String getFileName() {
		return filename;
	}
}

class TestWrapperTwo extends TestWrapperOne {
	
	private String halfName;
	
	public TestWrapperTwo(String filename) {
		super(filename);
		if (filename.length() < 2)
			filename += "s"; // just to have more than one character
		this.halfName = filename.substring(0, filename.length() / 2);
	}
}
