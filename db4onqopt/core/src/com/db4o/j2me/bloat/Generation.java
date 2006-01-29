package com.db4o.j2me.bloat;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.reflect.self.*;

// TODO: Use plain classes for testing, not the SelfReflector test cases
// (which already implement SelfReflectable functionality)
public class Generation {

	public static void main(String[] args) {
		String outputDirName = "generated";
		ClassFileLoader loader = new ClassFileLoader();
		Enhancer context = new Enhancer(loader, outputDirName);
		
		ClassEditor ce = context.createClass(Modifiers.PUBLIC,
				"RegressionDogSelfReflectionRegistry", Type.getType(Type.classDescriptor(SelfReflectionRegistry.class.getName())),
				new Type[0]);
		MethodBuilder.createLoadClassConstMethod(context, ce);

		RegistryEnhancer registryEnhancer = new RegistryEnhancer(context, ce,
				com.db4o.test.reflect.self.Dog.class);
		registryEnhancer.generate();
		ce.commit();

		enhanceClass(context,"../bin","com.db4o.j2me.bloat.testdata.Dog");
		enhanceClass(context,"../bin","com.db4o.j2me.bloat.testdata.Animal");
	}
	
	private static void enhanceClass(Enhancer context,String path,String name) {
		ClassEditor cled = context.loadClass(path,name);
		ClassEnhancer classEnhancer = new ClassEnhancer(context, cled);
		classEnhancer.generate();
		cled.commit();
	}
}
