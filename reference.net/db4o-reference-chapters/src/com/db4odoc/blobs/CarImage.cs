/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

namespace Db4objects.Db4odoc.Blobs
{

    using Db4objects.Db4o.Ext;
    using Db4objects.Db4o.Types;
	using Sharpen.Lang;
	using Sharpen.IO;

	public class CarImage {
		IBlob _blob = null;
		private string _file = null;
		private string inFolder = "blobs\\in\\";	
		private string outFolder = "blobs\\out\\";
		
		public CarImage() {
			
		}

		public string FileName
		{
			get
			{
				return _file;
			}
            
			set
			{
				_file = value;
			}
		}
		
		public bool ReadFile()
		{
			_blob.ReadFrom(new File(inFolder + _file));
			double status = _blob.GetStatus();
			while(status >  Status.COMPLETED){
					Thread.Sleep(50);
					status = _blob.GetStatus();
			}
			return (status == Status.COMPLETED);
		}
		
		public bool WriteFile()
		{
			_blob.WriteTo(new File(outFolder + _file));
			double status = _blob.GetStatus();
			while(status > Status.COMPLETED){
					Thread.Sleep(50);
					status = _blob.GetStatus();
			}
			return (status == Status.COMPLETED);
		}
	}
}