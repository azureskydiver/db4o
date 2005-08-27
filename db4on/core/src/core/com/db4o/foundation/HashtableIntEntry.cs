
namespace com.db4o.foundation
{
	internal class HashtableIntEntry : j4o.lang.Cloneable, com.db4o.foundation.DeepClone
	{
		internal int i_key;

		internal object i_object;

		internal com.db4o.foundation.HashtableIntEntry i_next;

		internal HashtableIntEntry(int a_hash, object a_object)
		{
			i_key = a_hash;
			i_object = a_object;
		}

		public virtual object deepClone(object obj)
		{
			com.db4o.foundation.HashtableIntEntry hie = null;
			try
			{
				hie = (com.db4o.foundation.HashtableIntEntry)j4o.lang.JavaSystem.clone(this);
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
			}
			hie.i_object = ((com.db4o.foundation.DeepClone)i_object).deepClone(obj);
			if (i_next != null)
			{
				hie.i_next = (com.db4o.foundation.HashtableIntEntry)i_next.deepClone(obj);
			}
			return hie;
		}
	}
}
