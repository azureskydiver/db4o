using System;
using System.IO;
using System.Text;

namespace Db4oTestRunner
{
	public abstract class AbstractDb4oTesterBase : MarshalByRefObject, ITestRunner
	{
		void ITestRunner.Run(ILogger logger)
		{
			_logger = logger;
			Run();
		}

		protected abstract void Run();

		protected string LogText()
		{
			return _log.ToString();
		}

		protected string TempFilePath()
		{
			if (_filePath == null)
			{
				_filePath = Path.GetTempFileName();
			}

			return _filePath;
		}

		protected ILogger _logger;
		private string _filePath;
		private readonly StringBuilder _log = new StringBuilder();
	}
}
