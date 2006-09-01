namespace com.db4o
{
	/// <exclude></exclude>
	public class MappingNotFoundException : j4o.lang.RuntimeException
	{
		private const long serialVersionUID = -1771324770287654802L;

		private int _id;

		public MappingNotFoundException(int id)
		{
			this._id = id;
		}

		public virtual int Id()
		{
			return _id;
		}

		public override string ToString()
		{
			return base.ToString() + " : " + _id;
		}
	}
}
