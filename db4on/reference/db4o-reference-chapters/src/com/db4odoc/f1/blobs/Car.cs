/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

using System;
    
namespace com.db4odoc.f1.blobs
{
	public class Car
	{
		string _model;
		CarImage _img;
		
		public Car(string model)
		{
			_model = model;
			_img=new CarImage();
			_img.FileName = _model+".jpg";
		}
        
		public CarImage CarImage 
		{
			get 
			{
				return _img;
			}
		}
		
		override public string ToString()
		{
			return string.Format("{0}({1})", _model, _img.FileName);
		}
	}
}
