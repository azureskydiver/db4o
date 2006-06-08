package com.db4o.test.unit;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class Printable {

	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);		
		print(printWriter);
		return writer.toString();
	}
	
	public abstract void print(PrintWriter printWriter);

}
