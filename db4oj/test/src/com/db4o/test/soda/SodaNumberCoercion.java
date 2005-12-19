package com.db4o.test.soda;

import com.db4o.query.*;
import com.db4o.test.*;

public class SodaNumberCoercion {
	private static final String DOUBLEFIELD = "_doubleValue";
	private static final String FLOATFIELD = "_floatValue";
	private static final String LONGFIELD = "_longValue";
	private static final String INTFIELD = "_intValue";
	private static final String SHORTFIELD = "_shortValue";
	private static final String BYTEFIELD = "_byteValue";
	private static final Float FLOATVALUE = new Float(100);
	private static final Double DOUBLEVALUE = new Double(100);
	private static final Long LONGVALUE = new Long(100);
	private static final Integer INTVALUE = new Integer(100);
	private static final Short SHORTVALUE = new Short((short)100);
	private static final Byte BYTEVALUE = new Byte((byte)100);

	public static class Thing {
		public byte _byteValue;
		public short _shortValue;
		public int _intValue;
		public long _longValue;
		public float _floatValue;
		public double _doubleValue;

		public Thing(byte byteValue,short shortValue,int intValue,long longValue,float floatValue,double doubleValue) {
			_byteValue=byteValue;
			_shortValue=shortValue;
			_intValue=intValue;
			_longValue=longValue;
			_floatValue=floatValue;
			_doubleValue=doubleValue;
		}
	}

	public void store() {
		Test.store(new Thing((byte)10,(short)10,10,10L,10f,10d));
		Test.store(new Thing((byte)100,(short)100,100,100L,100f,100d));
		Test.store(new Thing((byte)42,(short)42,42,42L,42f,42d));
	}

	public void testIntegerTypes() {
		assertSingleCoercionResult(BYTEFIELD,SHORTVALUE);
		assertSingleCoercionResult(BYTEFIELD,INTVALUE);
		assertSingleCoercionResult(BYTEFIELD,LONGVALUE);

		assertSingleCoercionResult(SHORTFIELD,BYTEVALUE);
		assertSingleCoercionResult(SHORTFIELD,INTVALUE);
		assertSingleCoercionResult(SHORTFIELD,LONGVALUE);

		assertSingleCoercionResult(INTFIELD,BYTEVALUE);
		assertSingleCoercionResult(INTFIELD,SHORTVALUE);
		assertSingleCoercionResult(INTFIELD,LONGVALUE);

		assertSingleCoercionResult(LONGFIELD,BYTEVALUE);
		assertSingleCoercionResult(LONGFIELD,SHORTVALUE);
		assertSingleCoercionResult(LONGFIELD,INTVALUE);
	}

	public void testFloatingPointTypes() {
		assertSingleCoercionResult(FLOATFIELD,DOUBLEVALUE);
		assertSingleCoercionResult(DOUBLEFIELD,FLOATVALUE);
	}

	public void testMixed() {
		assertSingleCoercionResult(BYTEFIELD, FLOATVALUE);
		assertSingleCoercionResult(BYTEFIELD, DOUBLEVALUE);
		assertSingleCoercionResult(SHORTFIELD, FLOATVALUE);
		assertSingleCoercionResult(SHORTFIELD, DOUBLEVALUE);
		assertSingleCoercionResult(INTFIELD, FLOATVALUE);
		assertSingleCoercionResult(INTFIELD, DOUBLEVALUE);
		assertSingleCoercionResult(LONGFIELD, FLOATVALUE);
		assertSingleCoercionResult(LONGFIELD, DOUBLEVALUE);
		assertSingleCoercionResult(FLOATFIELD, BYTEVALUE);
		assertSingleCoercionResult(FLOATFIELD, SHORTVALUE);
		assertSingleCoercionResult(FLOATFIELD, INTVALUE);
		assertSingleCoercionResult(FLOATFIELD, LONGVALUE);
		assertSingleCoercionResult(DOUBLEFIELD, BYTEVALUE);
		assertSingleCoercionResult(DOUBLEFIELD, SHORTVALUE);
		assertSingleCoercionResult(DOUBLEFIELD, INTVALUE);
		assertSingleCoercionResult(DOUBLEFIELD, LONGVALUE);
	}
	
	private void assertSingleCoercionResult(String fieldName,Number value) {
		Query q = Test.query();
		q.constrain(Thing.class);
		q.descend(fieldName).constrain(value);
		Test.ensureEquals(1, q.execute().size());
	}
}
