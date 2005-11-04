namespace com.db4o
{
	/// <exclude></exclude>
	public class QEEqual : com.db4o.QEAbstract
	{
		public override void indexBitMap(bool[] bits)
		{
			bits[1] = true;
		}
	}
}
