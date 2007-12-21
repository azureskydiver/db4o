/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.delaying;

import java.io.*;


public class DelayCalculationExample {

	private static final String _logFileName1 = "db4o-io-benchmark-results-30000_faster.log";
	private static final String _logFileName2 = "db4o-io-benchmark-results-30000_slower.log";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new DelayCalculationExample();
	}

	public DelayCalculationExample() {
		DelayCalculation calculation;
		try {
			calculation = new DelayCalculation(_logFileName1, _logFileName2);
			calculation.validateData();
			if (calculation.isValidData()) {
				System.out.println("Data is valid!");
			}
			else {
				System.err.println("Data is not valid!");
			}
			Delays delays = calculation.getDelays();
			System.out.println("delays: " + delays);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
