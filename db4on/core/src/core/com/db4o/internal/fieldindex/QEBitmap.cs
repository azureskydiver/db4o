namespace com.db4o.@internal.fieldindex
{
	internal class QEBitmap
	{
		public static com.db4o.@internal.fieldindex.QEBitmap ForQE(com.db4o.@internal.query.processor.QE
			 qe)
		{
			bool[] bitmap = new bool[4];
			qe.IndexBitMap(bitmap);
			return new com.db4o.@internal.fieldindex.QEBitmap(bitmap);
		}

		private QEBitmap(bool[] bitmap)
		{
			_bitmap = bitmap;
		}

		private bool[] _bitmap;

		public virtual bool TakeGreater()
		{
			return _bitmap[com.db4o.@internal.query.processor.QE.GREATER];
		}

		public virtual bool TakeEqual()
		{
			return _bitmap[com.db4o.@internal.query.processor.QE.EQUAL];
		}

		public virtual bool TakeSmaller()
		{
			return _bitmap[com.db4o.@internal.query.processor.QE.SMALLER];
		}
	}
}
