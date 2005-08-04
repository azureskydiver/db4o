namespace com.db4o.reflect.net
{
	/// <summary>Reflection implementation for Class to map to .NET reflection.</summary>
	/// <remarks>Reflection implementation for Class to map to .NET reflection.</remarks>
	public class NetClass : com.db4o.reflect.ReflectClass{

		private readonly com.db4o.reflect.Reflector _reflector;

		private readonly j4o.lang.Class _clazz;

        private readonly System.Type _type;

		private com.db4o.reflect.ReflectConstructor _constructor;

		private object[] constructorParams;

		public NetClass(com.db4o.reflect.Reflector reflector, j4o.lang.Class clazz){
			_reflector = reflector;
			_clazz = clazz;
            _type = clazz.getNetType();
		}

		public virtual com.db4o.reflect.ReflectClass getComponentType(){
			return _reflector.forClass(_clazz.getComponentType());
		}

		public virtual com.db4o.reflect.ReflectConstructor[] getDeclaredConstructors(){
			j4o.lang.reflect.Constructor[] constructors = _clazz.getDeclaredConstructors();
			com.db4o.reflect.ReflectConstructor[] reflectors = new com.db4o.reflect.ReflectConstructor
				[constructors.Length];
			for (int i = 0; i < constructors.Length; i++){
				reflectors[i] = new com.db4o.reflect.net.NetConstructor(_reflector, constructors[i]);
			}
			return reflectors;
		}

		public virtual com.db4o.reflect.ReflectField getDeclaredField(string name){
			try{
				return new com.db4o.reflect.net.NetField(_reflector, _clazz.getDeclaredField(name));
			}catch (System.Exception e){
				return null;
			}
		}

		public virtual com.db4o.reflect.ReflectField[] getDeclaredFields(){
			j4o.lang.reflect.Field[] fields = _clazz.getDeclaredFields();
			com.db4o.reflect.ReflectField[] reflectors = new com.db4o.reflect.ReflectField[fields.Length];
			for (int i = 0; i < reflectors.Length; i++){
				reflectors[i] = new com.db4o.reflect.net.NetField(_reflector, fields[i]);
			}
			return reflectors;
		}

        public virtual com.db4o.reflect.ReflectClass getDelegate(){
            return this;
        }

		public virtual com.db4o.reflect.ReflectMethod getMethod(
            string methodName, 
            com.db4o.reflect.ReflectClass[] paramClasses){
			try{
				j4o.lang.reflect.Method method = _clazz.getMethod(methodName, com.db4o.reflect.net.NetReflector
					.toNative(paramClasses));
				if (method == null){
					return null;
				}
				return new com.db4o.reflect.net.NetMethod(_reflector, method);
			}
			catch (System.Exception e){
				return null;
			}
		}

		public virtual string getName(){
			return _clazz.getName();
		}

		public virtual com.db4o.reflect.ReflectClass getSuperclass(){
			return _reflector.forClass(_clazz.getSuperclass());
		}

		public virtual bool isAbstract(){
			return j4o.lang.reflect.Modifier.isAbstract(_clazz.getModifiers());
		}

		public virtual bool isArray(){
			return _clazz.isArray();
		}

		public virtual bool isAssignableFrom(com.db4o.reflect.ReflectClass type){
			if (!(type is com.db4o.reflect.net.NetClass)){
				return false;
			}
			return _clazz.isAssignableFrom(((com.db4o.reflect.net.NetClass)type).getJavaClass(
				));
		}

		public virtual bool isInstance(object obj){
			return _clazz.isInstance(obj);
		}

		public virtual bool isInterface(){
			return _clazz.isInterface();
		}

        public virtual bool isCollection() {
            return _reflector.isCollection(this);
        }

		public virtual bool isPrimitive(){
			return _clazz.isPrimitive();
		}
		
		public virtual bool isSecondClass() {
            return isPrimitive();
        }

		public virtual object newInstance(){
			try{
				if (_constructor == null){
					return _clazz.newInstance();
				}
				return _constructor.newInstance(constructorParams);
			}
			catch (System.Exception t){
			}
			return null;
		}

		internal virtual j4o.lang.Class getJavaClass()
		{
			return _clazz;
		}

        public virtual System.Type getNetType() {
            return _type;
        }

        public virtual com.db4o.reflect.Reflector reflector(){
            return _reflector;
        }

        public virtual bool skipConstructor(bool flag){
			if (flag){

                ReflectConstructor constructor = Compat.serializationConstructor(getNetType());

				if (constructor != null){
					try
					{
						object o = constructor.newInstance(null);
						if (o != null)
						{
							useConstructor(constructor, null);
							return true;
						}
					}
					catch (System.Exception e)
					{
					}
				}
			}
			useConstructor(null, null);
			return false;
		}

        public virtual object[] toArray(object obj) {
            // handled in GenericClass
            return null;
        }

		public override string ToString(){
			return "CClass: " + _clazz.getName();
		}

		public virtual void useConstructor(
            com.db4o.reflect.ReflectConstructor constructor, 
            object[] _params){
			_constructor = constructor;
			constructorParams = _params;
		}
	}
}
