namespace com.db4o
{
	/// <exclude></exclude>
	public class QEEqual : com.db4o.QEAbstract
	{
		public override void IndexBitMap(bool[] bits)
		{
			bits[com.db4o.QE.EQUAL] = true;
		}
	}
}
