using System;

namespace com.db4o.test{

	public class StoreSimpleTypeDirectly{

        public void store(){
            object[] simpleTypes = new object[]{true, 32, (long)4};
            for(int i = 0; i < simpleTypes.Length; i ++){
                Tester.objectContainer().set(simpleTypes[i]);
            }
        }
	}
}
