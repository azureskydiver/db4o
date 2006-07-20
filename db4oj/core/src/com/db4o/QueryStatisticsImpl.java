package com.db4o;

import com.db4o.foundation.StopWatch;
import com.db4o.query.QueryStatistics;

public class QueryStatisticsImpl implements QueryStatistics {
	
	private final StopWatch _timer = new StopWatch();
	
	private int _activationCount;
	
	public QueryStatisticsImpl() {
	}

	public long executionTime() {
		return _timer.elapsed();
	}

	public void startTimer() {
		_timer.start();
	}

	public void stopTimer() {
		_timer.stop(); 
	}

	public int activationCount() {
		return _activationCount;
	}
}
