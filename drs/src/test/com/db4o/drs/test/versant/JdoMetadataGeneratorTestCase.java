/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import com.db4o.drs.test.data.*;
import com.db4o.drs.versant.*;

import db4ounit.*;

public class JdoMetadataGeneratorTestCase implements TestCase {
	
	public void test() throws Exception{
		JdoMetadataGenerator generator = new JdoMetadataGenerator(new File("bin"));
		File generatedFile = generator.generate("com.db4o.drs.test.data");
		BufferedReader reader = new BufferedReader(new FileReader(generatedFile));
		String expected = "<class name=\"" +  SPCChild.class.getName() + "\"/>";
		String line = null;
		boolean found = false;
		while((line = reader.readLine()) != null){
			if(line.contains(expected)){
				found = true;
			}
		}
		reader.close();
		Assert.isTrue(found);
	}

}
