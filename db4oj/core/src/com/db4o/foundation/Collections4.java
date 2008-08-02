package com.db4o.foundation;

public class Collections4 {

	public static Sequence4 unmodifiableList(Sequence4 orig) {
		return new UnmodifiableSequence4(orig);
	}
	
	private static class UnmodifiableSequence4 implements Sequence4 {

		private Sequence4 _sequence; 
		
		public UnmodifiableSequence4(Sequence4 sequence) {
			_sequence = sequence;
		}		

		public void add(Object element) {
			throw new IllegalStateException();
		}

		public boolean isEmpty() {
			return _sequence.isEmpty();
		}

		public Iterator4 iterator() {
			return _sequence.iterator();
		}

		public Object get(int index) {
			return _sequence.get(index);
		}
		
	}
}
