namespace com.db4o.reflect.net
{
	public class NetReflector : com.db4o.reflect.Reflector
	{

        private com.db4o.reflect.Reflector _parent;

		private com.db4o.reflect.ReflectArray _array;


		public virtual com.db4o.reflect.ReflectArray array(){
            if(_array == null){
                _array = new com.db4o.reflect.net.NetArray(_parent);
            }
			return _array;
		}

        public virtual bool constructorCallsSupported(){
            return true;
        }

        public virtual object deepClone(object obj){
            return new NetReflector();
        }

		public virtual com.db4o.reflect.ReflectClass forClass(j4o.lang.Class clazz){
            return new com.db4o.reflect.net.NetClass(_parent, clazz);
		}

        public virtual com.db4o.reflect.ReflectClass forName(string className){
			j4o.lang.Class clazz = j4o.lang.Class.forName(className);
            return clazz == null
				? null
				: new com.db4o.reflect.net.NetClass(_parent, clazz);
        }

		public virtual com.db4o.reflect.ReflectClass forObject(object a_object){
			if (a_object == null){
				return null;
			}
			return _parent.forClass(j4o.lang.Class.getClassForObject(a_object));
		}

		public virtual bool isCollection(com.db4o.reflect.ReflectClass candidate){
            if(candidate.isArray()){
                return false;
            }
            if ( typeof(System.Collections.ICollection).IsAssignableFrom(
                ((com.db4o.reflect.net.NetClass)candidate).getNetType())){
                return true;
            }
			return false;
		}

		public virtual bool methodCallsSupported(){
			return true;
		}

		public static com.db4o.reflect.ReflectClass[] toMeta(
            com.db4o.reflect.Reflector reflector, 
            j4o.lang.Class[] clazz)
        {
			com.db4o.reflect.ReflectClass[] claxx = null;
			if (clazz != null){
				claxx = new com.db4o.reflect.ReflectClass[clazz.Length];
				for (int i = 0; i < clazz.Length; i++){
					if (clazz[i] != null){
						claxx[i] = reflector.forClass(clazz[i]);
					}
				}
			}
			return claxx;
		}

        internal static j4o.lang.Class[] toNative(com.db4o.reflect.ReflectClass[] claxx){
            j4o.lang.Class[] clazz = null;
            if (claxx != null){
                clazz = new j4o.lang.Class[claxx.Length];
                for (int i = 0; i < claxx.Length; i++){
                    if (claxx[i] != null){
                        clazz[i] = ((com.db4o.reflect.net.NetClass)claxx[i].getDelegate()).getJavaClass();
                    }
                }
            }
            return clazz;
        }

        public virtual void setParent(Reflector reflector){
            _parent = reflector;
        }
	}
}
