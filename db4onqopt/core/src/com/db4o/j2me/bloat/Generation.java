package com.db4o.j2me.bloat;

import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.file.ClassFileLoader;
import EDU.purdue.cs.bloat.reflect.Modifiers;

import com.db4o.bloat.RegistryEnhancer;

public class Generation {
	private static ClassEnhancer classEnhancer;

	private static RegistryEnhancer registryEnhancer;

	public void init() {

	}

	public static void main(String[] args) {

		registryEnhancer = new RegistryEnhancer();
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

		classEnhancer = new ClassEnhancer();
		ClassEditor ced = classEnhancer.loadClass(loader, "add here classPath",
				"Dog");
		if (!(classEnhancer.inspectNoArgConstr(ced, ced.methods()))) {
			classEnhancer.addNoArgConstructor(ced);
		}
		classEnhancer.generateSelf_get(ced);
		classEnhancer.generateSelf_set(ced);
		ced.commit();

	}

}
