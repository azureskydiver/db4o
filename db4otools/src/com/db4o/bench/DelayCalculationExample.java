/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench;


public class DelayCalculationExample {

	private static final String _logFileName1 = "slower-millis.log";
	private static final String _logFileName2 = "z3raDesk2_polepos-allopt-cs.log";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new DelayCalculationExample();
	}

	public DelayCalculationExample() {
		DelayCalculation calculation = new DelayCalculation(_logFileName1, _logFileName2);
		calculation.validateData();
		if (calculation.isValidData()) {
			System.out.println("Data is valid!");
		}
		else {
			System.err.println("Data is not valid!");
		}
		Delays delays = calculation.getDelays();
		System.out.println("delays: " + delays);
	}
}
