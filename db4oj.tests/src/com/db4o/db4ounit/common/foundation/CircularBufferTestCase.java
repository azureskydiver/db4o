package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @decaf.ignore
 */
public class CircularBufferTestCase implements TestCase {

	final CircularBuffer4<Integer> buffer = new CircularBuffer4<Integer>(2);
	
	public void testAddFirstRemoveLast() {
		for (int i=1; i<11; ++i) {
			buffer.addFirst(i);
			assertRemoveLast(i);
		}
	}
	
	public void testAddFirstBounds() {
		buffer.addFirst(1);
		buffer.addFirst(2);
		assertIllegalAddFirst();
		buffer.removeLast();
		buffer.addFirst(4);
		assertIllegalAddFirst();
		buffer.removeLast();
		buffer.addFirst(5);
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
		
		buffer.addFirst(1);
		buffer.addFirst(2);
		
		assertRemove(1);
		assertRemove(2);
		
		buffer.addFirst(1);
		buffer.addFirst(2);
		assertRemove(2);
		buffer.addFirst(4);
		
		assertRemoveLast(1);
		assertRemoveLast(4);
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

}
