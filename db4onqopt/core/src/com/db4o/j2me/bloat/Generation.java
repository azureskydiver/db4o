package com.db4o.j2me.bloat;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.reflect.self.*;

public class Generation {

	public static void main(String[] args) {
		String outputDirName = "generated";
		ClassFileLoader loader = new ClassFileLoader();
		Enhancer context = new Enhancer(loader, outputDirName);
		
		ClassEditor ce = context.createClass(Modifiers.PUBLIC,
				"RegressionDogSelfReflectionRegistry", Type.getType("L"
						+ SelfReflectionRegistry.class.getName() + ";"),
				new Type[0]);
		MethodBuilder.createLoadClassConstMethod(context, ce);

		RegistryEnhancer registryEnhancer = new RegistryEnhancer(context, ce,
				com.db4o.test.reflect.self.Dog.class);
		registryEnhancer.generate();
		ce.commit();

		ClassEditor cled = context.loadClass("../unittests/bin",
				"com.db4o.reflect.self.Dog");
		ClassEnhancer classEnhancer = new ClassEnhancer(context, cled);

		if (!(classEnhancer.inspectNoArgConstr(cled.methods()))) {
			classEnhancer.addNoArgConstructor();
		}
		classEnhancer.generate();
		cled.commit();

	}
}
