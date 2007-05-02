using System;

namespace Db4objects.Db4odoc.Messaging
{
	class MyClientServerMessage 
	{
		private string _info;

		public MyClientServerMessage(String info)
		{
			this._info = info;
		}
    
		public override String ToString()
		{
			return "MyClientServerMessage: " + _info;
		}
	}
}
