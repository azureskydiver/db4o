using System;

namespace com.db4odoc.f1.messaging
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
