package com.db4o.cs.common;

/**
 * Holds static operation commands.
 *
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 11:55:51 AM
 */
public interface Operations {
	byte LOGIN = 5;
	byte CLASS_METADATA = 11;
	byte SET = 51;
	byte SET_END = 52;
	byte BATCH = 54;
	byte BULK = 55; // single Update/Query combo sent
	byte QUERY = 61;
	byte COMMIT = 71;
	byte DELETE = 81;
	byte CLOSE = 127;
	byte GETBYID = 62;
}
