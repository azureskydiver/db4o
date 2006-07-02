package com.db4o.devtools.ant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class IO {
	public static void writeAll(File file, String contents) throws IOException {
		FileWriter writer = new FileWriter(file);
		try {
			writer.write(contents);
		} finally {
			writer.close();
		}
	}

	public static String readAll(File file) throws IOException {
		StringBuilder builder = new StringBuilder();
		FileReader reader = new FileReader(file);
		try {
			char[] buffer = new char[1024];
			int count;
			while (-1 != (count = reader.read(buffer))) {
				builder.append(buffer, 0, count);
			}
		} finally {
			reader.close();
		}
		return builder.toString();
	}
}
