namespace com.db4o.reflect.net
{
	public class NetReflector : com.db4o.reflect.Reflector
	{

        private com.db4o.reflect.Reflector _parent;

		private com.db4o.reflect.ReflectArray _array;


		public virtual com.db4o.reflect.ReflectArray Array(){
            if(_array == null){
                _array = new com.db4o.reflect.net.NetArray(_parent);
            }
			return _array;
		}

        public virtual bool ConstructorCallsSupported(){
            return true;
        }

        public virtual object DeepClone(object obj){
            return new NetReflector();
        }

		public virtual com.db4o.reflect.ReflectClass ForClass(j4o.lang.Class clazz){
            return new com.db4o.reflect.net.NetClass(_parent, clazz);
		}

        public virtual com.db4o.reflect.ReflectClass ForName(string className){
			j4o.lang.Class clazz = j4o.lang.Class.ForName(className);
            return clazz == null
				? null
				: new com.db4o.reflect.net.NetClass(_parent, clazz);
        }

		public virtual com.db4o.reflect.ReflectClass ForObject(object a_object){
			if (a_object == null){
				return null;
			}
			return _parent.ForClass(j4o.lang.Class.GetClassForObject(a_object));
		}

		public virtual bool IsCollection(com.db4o.reflect.ReflectClass candidate){
            if(candidate.IsArray()){
                return false;
            }
            if ( typeof(System.Collections.ICollection).IsAssignableFrom(
                ((com.db4o.reflect.net.NetClass)candidate).GetNetType())){
                return true;
            }
			return false;
		}

		public virtual bool MethodCallsSupported(){
			return true;
		}

		public static com.db4o.reflect.ReflectClass[] ToMeta(
            com.db4o.reflect.Reflector reflector, 
            j4o.lang.Class[] clazz)
        {
			com.db4o.reflect.ReflectClass[] claxx = null;
			if (clazz != null){
				claxx = new com.db4o.reflect.ReflectClass[clazz.Length];
				for (int i = 0; i < clazz.Length; i++){
					if (clazz[i] != null){
						claxx[i] = reflector.ForClass(clazz[i]);
					}
				}
			}
			return claxx;
		}

        internal static j4o.lang.Class[] ToNative(com.db4o.reflect.ReflectClass[] claxx){
            j4o.lang.Class[] clazz = null;
            if (claxx != null){
                clazz = new j4o.lang.Class[claxx.Length];
                for (int i = 0; i < claxx.Length; i++){
                    if (claxx[i] != null){
                        clazz[i] = ((com.db4o.reflect.net.NetClass)claxx[i].GetDelegate()).GetJavaClass();
                    }
                }
            }
            return clazz;
        }

        public virtual void SetParent(Reflector reflector){
            _parent = reflector;
        }
	}
}
