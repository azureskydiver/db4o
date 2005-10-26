using System;

namespace com.db4o.test.nativequeries.cats
{

	public class Cat : Animal
	{
        public String _firstName;
    
        public String _lastName;
    
        public int _age;
    
        public Cat _father;
    
        public Cat _mother;
    
        public String getFirstName(){
            return _firstName;
        }
    
        public int getAge(){
            return _age;
        }
    
        public String getFullName(){
            return _firstName + " " + _lastName;
        }
    }
}
