namespace com.db4o.inside.fieldindex
{
	internal class QEBitmap
	{
		public static com.db4o.inside.fieldindex.QEBitmap ForQE(com.db4o.QE qe)
		{
			bool[] bitmap = new bool[4];
			qe.IndexBitMap(bitmap);
			return new com.db4o.inside.fieldindex.QEBitmap(bitmap);
		}

		private QEBitmap(bool[] bitmap)
		{
			_bitmap = bitmap;
		}

		private bool[] _bitmap;

		public virtual bool TakeGreater()
		{
			return _bitmap[com.db4o.QE.GREATER];
		}

		public virtual bool TakeEqual()
		{
			return _bitmap[com.db4o.QE.EQUAL];
		}

		public virtual bool TakeSmaller()
		{
			return _bitmap[com.db4o.QE.SMALLER];
		}
	}
}
