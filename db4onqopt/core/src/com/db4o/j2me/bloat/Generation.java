package com.db4o.j2me.bloat;

import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.file.ClassFileLoader;
import EDU.purdue.cs.bloat.reflect.Modifiers;

public class Generation {
	private static ClassEnhancer classEnhancer;

	private static RegistryEnhancer registryEnhancer;

	public static  void init() {
		registryEnhancer = new RegistryEnhancer();
		classEnhancer = new ClassEnhancer();
	}

	public static void main(String[] args) {
		init();
		String outputDirName = "DirName";
		ClassFileLoader loader = new ClassFileLoader();
		ClassEditor ce = registryEnhancer.createClass(loader, outputDirName,
				Modifiers.PUBLIC, "RegressionDogSelfReflectionRegistry", Type
						.getType("SelfReflectionRegistry"), new Type[0]);

		registryEnhancer.generateCLASSINFOField(ce);
		registryEnhancer.generateInfoForMethod(ce);
		registryEnhancer.generateArrayForMethod(ce,
				com.db4o.test.reflect.self.Dog.class);
		registryEnhancer.generateComponentTypeMethod(ce,
				com.db4o.test.reflect.self.Dog.class);

		ce.commit();

		ClassEditor cled = classEnhancer.loadClass(loader,
				"add here classPath", "Dog");
		if (!(classEnhancer.inspectNoArgConstr(cled, cled.methods()))) {
			classEnhancer.addNoArgConstructor(cled);
		}
		classEnhancer.generateSelf_get(cled);
		classEnhancer.generateSelf_set(cled);
		cled.commit();

	}

}
