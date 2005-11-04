namespace com.db4o.inside.freespace
{
	public class FreespaceVisitor
	{
		internal int _key;

		internal int _value;

		private bool _visited;

		public virtual void visit(int key, int value)
		{
			_key = key;
			_value = value;
			_visited = true;
		}

		public virtual bool visited()
		{
			return _visited;
		}
	}
}
