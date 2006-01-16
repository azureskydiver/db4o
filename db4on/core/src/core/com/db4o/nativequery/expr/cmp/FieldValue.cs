namespace com.db4o.nativequery.expr.cmp
{
	public class FieldValue : com.db4o.nativequery.expr.cmp.ComparisonOperand
	{
		private int _parentIdx;

		private com.db4o.foundation.Collection4 _fieldNames = new com.db4o.foundation.Collection4
			();

		public FieldValue(int parentIdx, string name)
		{
			_parentIdx = parentIdx;
			descend(name);
		}

		public FieldValue(int parentIdx, string[] fieldNames)
		{
			_parentIdx = parentIdx;
			_fieldNames.addAll(fieldNames);
		}

		public FieldValue(int parentIdx, com.db4o.foundation.Iterator4 fieldNames)
		{
			_parentIdx = parentIdx;
			_fieldNames.addAll(fieldNames);
		}

		public virtual com.db4o.nativequery.expr.cmp.FieldValue descend(string fieldName)
		{
			_fieldNames.add(fieldName);
			return this;
		}

		public virtual com.db4o.foundation.Iterator4 fieldNames()
		{
			return _fieldNames.strictIterator();
		}

		public virtual int parentIdx()
		{
			return _parentIdx;
		}

		public override bool Equals(object other)
		{
			if (this == other)
			{
				return true;
			}
			if (other == null || j4o.lang.Class.getClassForObject(this) != j4o.lang.Class.getClassForObject
				(other))
			{
				return false;
			}
			com.db4o.nativequery.expr.cmp.FieldValue casted = (com.db4o.nativequery.expr.cmp.FieldValue
				)other;
			if (_fieldNames.size() != casted._fieldNames.size())
			{
				return false;
			}
			com.db4o.foundation.Iterator4 firstIter = _fieldNames.iterator();
			com.db4o.foundation.Iterator4 secondIter = casted._fieldNames.iterator();
			while (firstIter.hasNext())
			{
				if (!firstIter.next().Equals(secondIter.next()))
				{
					return false;
				}
			}
			return _parentIdx == casted._parentIdx;
		}

		public override int GetHashCode()
		{
			int hashCode = 0;
			com.db4o.foundation.Iterator4 firstIter = _fieldNames.iterator();
			while (firstIter.hasNext())
			{
				hashCode *= 29 + firstIter.next().GetHashCode();
			}
			return hashCode * 29 + _parentIdx;
		}

		public override string ToString()
		{
			j4o.lang.StringBuffer str = new j4o.lang.StringBuffer();
			str.append(_parentIdx);
			for (com.db4o.foundation.Iterator4 nameIter = fieldNames(); nameIter.hasNext(); )
			{
				string fieldName = (string)nameIter.next();
				str.append('.');
				str.append(fieldName);
			}
			return str.ToString();
		}

		public virtual void accept(com.db4o.nativequery.expr.cmp.ComparisonOperandVisitor
			 visitor)
		{
			visitor.visit(this);
		}
	}
}
