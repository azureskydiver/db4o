/* Copyright (C) 2004 - 2006  Versant Inc.  http://www.db4o.com */

package com.db4o.foundation;


/**
 * @exclude
 */
public class TimeStampIdGenerator {
	
	private static final int BITS_RESERVED_FOR_COUNTER = 15;
	
	private static final int BITS_RESERVED_FOR_COUNTER_IN_48BIT_ID = 6;
	
	private static final int COUNTER_LIMIT = 64;
    
	private long _counter;
	
	private long _lastTime;

	public static long idToMilliseconds(long id) {
		return id >> BITS_RESERVED_FOR_COUNTER;
	}

	public static long millisecondsToId(long milliseconds) {
		return milliseconds << BITS_RESERVED_FOR_COUNTER;
	}
	
	public TimeStampIdGenerator(long minimumNext) {
		internalSetMinimumNext(minimumNext);
	}

	public TimeStampIdGenerator() {
		this(0);
	}

	public long generate() {
		long t = now();
		if(t > _lastTime){
			_lastTime = t;
			_counter = 0;
			return millisecondsToId(t);
		}
		updateTimeOnCounterLimitOverflow();
		_counter++;
		updateTimeOnCounterLimitOverflow();
		return last();
	}

	protected long now() {
		return System.currentTimeMillis();
	}

	private final void updateTimeOnCounterLimitOverflow() {
		if(_counter < COUNTER_LIMIT){
			return;
		}
		long timeIncrement = _counter / COUNTER_LIMIT;
		_lastTime += timeIncrement;
		_counter -= (timeIncrement * COUNTER_LIMIT);
	}

	public long last() {
		return millisecondsToId(_lastTime) + _counter;
	}

	public boolean setMinimumNext(long newMinimum) {
        if(newMinimum <= last()){
            return false;
        }
        internalSetMinimumNext(newMinimum);
        return true;
	}

	private void internalSetMinimumNext(long newNext) {
		_lastTime = idToMilliseconds(newNext);
		long timePart = millisecondsToId(_lastTime);
		_counter = newNext - timePart;
		updateTimeOnCounterLimitOverflow();
	}
	
	public static long convert64BitIdTo48BitId(long id){
		return convert(
				id, 
				BITS_RESERVED_FOR_COUNTER, 
				BITS_RESERVED_FOR_COUNTER_IN_48BIT_ID);
	}
	
	public static long convert48BitIdTo64BitId(long id){
		return convert(
				id, 
				BITS_RESERVED_FOR_COUNTER_IN_48BIT_ID, 
				BITS_RESERVED_FOR_COUNTER);
	}

	private static long convert(long id, int shiftBitsFrom, int shiftBitsTo) {
		final long creationTimeInMillis = id >>> shiftBitsFrom;
		final long timeStampPart = creationTimeInMillis << shiftBitsFrom;
		final long counterPerMillisecond = id - timeStampPart;
		if(counterPerMillisecond >= COUNTER_LIMIT){
			throw new IllegalStateException("ID can't be converted");
		}
		return (creationTimeInMillis << shiftBitsTo) + counterPerMillisecond;
	}
	
}
