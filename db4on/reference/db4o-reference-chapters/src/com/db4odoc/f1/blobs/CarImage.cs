/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

namespace com.db4odoc.f1.blobs
{

	using com.db4o.ext;
	using com.db4o.types;
	using j4o.lang;
	using j4o.io;

	public class CarImage {
		Blob _blob;
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