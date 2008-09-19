using System.Collections;

namespace Db4objects.Db4o.Typehandlers
{
	public partial class CollectionTypeHandler 
	{
		private void AddToCollection(ICollection collection, object element) 
		{
			((IList)collection).Add(element);
		}
	
		private void ClearCollection(ICollection collection) 
		{
			((IList)collection).Clear();
		}
	}
}
