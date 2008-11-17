package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @decaf.ignore
 */
public class CircularBufferTestCase implements TestCase {
	
	private static final int BUFFER_SIZE = 4;

	final CircularBuffer4<Integer> buffer = new CircularBuffer4<Integer>(BUFFER_SIZE);
	
	public void testAddFirstRemoveLast() {
		for (int i=1; i<11; ++i) {
			buffer.addFirst(i);
			assertRemoveLast(i);
		}
	}
	
	public void testAddFirstBounds() {
		fillBuffer();
		assertIllegalAddFirst();
		buffer.removeLast();
		buffer.addFirst(5);
		assertIllegalAddFirst();
		buffer.removeLast();
		buffer.addFirst(6);
	}

	private void assertIllegalAddFirst() {
	    Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() {
				buffer.addFirst(3);
			}
		});
    }
	
	public void testRemoveLastBounds() {
		for (int i=0; i<3; ++i) {
			assertIllegalRemoveLast();
			
			buffer.addFirst(1);
			buffer.addFirst(3);
			assertRemoveLast(1);
			assertRemoveLast(3);
			
			assertIllegalRemoveLast();
		}
	}
	
	private void assertIllegalRemoveFirst() {
	    Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() {
				buffer.removeFirst();
			}
		});
    }

	private void assertIllegalRemoveLast() {
	    Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() {
				buffer.removeLast();
			}
		});
    }
	
	private void assertRemoveLast(int value) {
	    Assert.areEqual(value, (int)buffer.removeLast());
    }
	
	public void testRemove() {
		assertIllegalRemove(42);
		
		buffer.addFirst(1);
		assertRemove(1);
		
		fillBuffer();
		assertRemovals(1, 2, 3, 4);
		
		fillBuffer();
		assertRemovals(2, 3, 4, 1);
		
		fillBuffer();
		assertRemovals(3, 2, 4, 1);
		
		fillBuffer();
		assertRemovals(4, 3, 2, 1);
		
		fillBuffer();
		assertRemovals(4, 1, 2, 3);
		
		fillBuffer();
		assertRemoveLast(1);
		assertRemoveLast(2);
		assertRemoveLast(3);
		assertRemoveLast(4);
		
	}

	private void assertRemovals(int... indexes) {
		for(int i : indexes){
			assertRemove(i);	
		}
		assertIllegalRemoveLast();
		assertIllegalRemoveFirst();
	}

	private void assertRemove(int value) {
	    buffer.remove(value);
		assertIllegalRemove(value);
    }

	private void assertIllegalRemove(final int value) {
	    Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() {
				buffer.remove(value);
			}
		});
    }
	
	private void fillBuffer() {
		for (int i = 1; i <= BUFFER_SIZE; i++) {
			buffer.addFirst(i);	
		}
	}

}
