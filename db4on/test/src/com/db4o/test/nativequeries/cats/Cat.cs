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
    
        public String GetFirstName(){
            return _firstName;
        }
    
        public int GetAge(){
            return _age;
        }
    
        public String GetFullName(){
            return _firstName + " " + _lastName;
        }

		public Cat GetFather() 
		{
			return _father;
		}
    }
}
