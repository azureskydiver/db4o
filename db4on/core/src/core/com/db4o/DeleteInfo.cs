namespace com.db4o
{
	internal class DeleteInfo : com.db4o.TreeInt
	{
		internal bool _delete;

		internal int _cascade;

		internal com.db4o.YapObject _reference;

		public DeleteInfo(int id, com.db4o.YapObject reference, bool delete, int cascade)
			 : base(id)
		{
			_reference = reference;
			_delete = delete;
			_cascade = cascade;
		}

		public override object ShallowClone()
		{
			com.db4o.DeleteInfo deleteinfo = new com.db4o.DeleteInfo(0, _reference, _delete, 
				_cascade);
			return ShallowCloneInternal(deleteinfo);
		}
	}
}
