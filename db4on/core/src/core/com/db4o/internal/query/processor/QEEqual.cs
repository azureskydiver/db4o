namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public class QEEqual : com.db4o.@internal.query.processor.QEAbstract
	{
		public override void IndexBitMap(bool[] bits)
		{
			bits[com.db4o.@internal.query.processor.QE.EQUAL] = true;
		}
	}
}
