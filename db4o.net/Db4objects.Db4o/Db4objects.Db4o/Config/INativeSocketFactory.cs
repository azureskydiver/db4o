/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

using Db4objects.Db4o.Foundation;
using Sharpen.Net;

namespace Db4objects.Db4o.Config
{
	/// <summary>Create platform native server and client sockets.</summary>
	/// <remarks>Create platform native server and client sockets.</remarks>
	public interface INativeSocketFactory : IDeepClone
	{
		Sharpen.Net.Socket CreateSocket(string hostName, int port);

		ServerSocket CreateServerSocket(int port);
	}
}
