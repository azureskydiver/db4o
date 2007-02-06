namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public class QField : com.db4o.foundation.Visitor4, com.db4o.types.Unversioned
	{
		[System.NonSerialized]
		internal com.db4o.@internal.Transaction i_trans;

		public string i_name;

		[System.NonSerialized]
		internal com.db4o.@internal.FieldMetadata i_yapField;

		public int i_yapClassID;

		public int i_index;

		public QField()
		{
		}

		public QField(com.db4o.@internal.Transaction a_trans, string name, com.db4o.@internal.FieldMetadata
			 a_yapField, int a_yapClassID, int a_index)
		{
			i_trans = a_trans;
			i_name = name;
			i_yapField = a_yapField;
			i_yapClassID = a_yapClassID;
			i_index = a_index;
			if (i_yapField != null)
			{
				if (!i_yapField.Alive())
				{
					i_yapField = null;
				}
			}
		}

		internal virtual bool CanHold(com.db4o.reflect.ReflectClass claxx)
		{
			return i_yapField == null || i_yapField.CanHold(claxx);
		}

		internal virtual object Coerce(object a_object)
		{
			com.db4o.reflect.ReflectClass claxx = null;
			com.db4o.reflect.Reflector reflector = i_trans.Reflector();
			if (a_object != null)
			{
				if (a_object is com.db4o.reflect.ReflectClass)
				{
					claxx = (com.db4o.reflect.ReflectClass)a_object;
				}
				else
				{
					claxx = reflector.ForObject(a_object);
				}
			}
			else
			{
				return a_object;
			}
			if (i_yapField == null)
			{
				return a_object;
			}
			return i_yapField.Coerce(claxx, a_object);
		}

		internal virtual com.db4o.@internal.ClassMetadata GetYapClass()
		{
			if (i_yapField != null)
			{
				return i_yapField.GetFieldYapClass(i_trans.Stream());
			}
			return null;
		}

		internal virtual com.db4o.@internal.FieldMetadata GetYapField(com.db4o.@internal.ClassMetadata
			 yc)
		{
			if (i_yapField != null)
			{
				return i_yapField;
			}
			com.db4o.@internal.FieldMetadata yf = yc.GetYapField(i_name);
			if (yf != null)
			{
				yf.Alive();
			}
			return yf;
		}

		public virtual com.db4o.@internal.FieldMetadata GetYapField()
		{
			return i_yapField;
		}

		internal virtual bool IsArray()
		{
			return i_yapField != null && i_yapField.GetHandler() is com.db4o.@internal.handlers.ArrayHandler;
		}

		internal virtual bool IsClass()
		{
			return i_yapField == null || i_yapField.GetHandler().GetTypeID() == com.db4o.@internal.Const4
				.TYPE_CLASS;
		}

		internal virtual bool IsSimple()
		{
			return i_yapField != null && i_yapField.GetHandler().GetTypeID() == com.db4o.@internal.Const4
				.TYPE_SIMPLE;
		}

		internal virtual com.db4o.@internal.Comparable4 PrepareComparison(object obj)
		{
			if (i_yapField != null)
			{
				return i_yapField.PrepareComparison(obj);
			}
			if (obj == null)
			{
				return com.db4o.@internal.Null.INSTANCE;
			}
			com.db4o.@internal.ClassMetadata yc = i_trans.Stream().ProduceYapClass(i_trans.Reflector
				().ForObject(obj));
			com.db4o.@internal.FieldMetadata yf = yc.GetYapField(i_name);
			if (yf != null)
			{
				return yf.PrepareComparison(obj);
			}
			return null;
		}

		internal virtual void Unmarshall(com.db4o.@internal.Transaction a_trans)
		{
			if (i_yapClassID != 0)
			{
				com.db4o.@internal.ClassMetadata yc = a_trans.Stream().GetYapClass(i_yapClassID);
				i_yapField = yc.i_fields[i_index];
			}
		}

		public virtual void Visit(object obj)
		{
			((com.db4o.@internal.query.processor.QCandidate)obj).UseField(this);
		}

		public override string ToString()
		{
			if (i_yapField != null)
			{
				return "QField " + i_yapField.ToString();
			}
			return base.ToString();
		}
	}
}
