package contacts;

import java.io.*;

public class Console {
	
	public static String prompt(String prompt) {
		System.out.println(prompt);
		return readLine();
	}

	private static String readLine() {
		try {
			return new BufferedReader(new InputStreamReader(System.in)).readLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
