/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Drawing;
using com.db4o.config;

namespace com.db4o.test.cs
{
#if !CF_1_0 && !CF_2_0
	/// <summary>
	/// Summary description for CsImage.
	/// </summary>
	public class CsImage
	{

        private static String IMAGE_PATH = "C:\\CO.jpg";

        Image image;
        Bitmap bitmap;

        public void Configure(){
            Db4o.Configure().ObjectClass(typeof(Image)).Translate(new TNull());
            Db4o.Configure().ObjectClass(typeof(Bitmap)).Translate(new com.db4o.config.TSerializable());
        }

//        public void StoreOne(){
//            image = Image.FromFile(IMAGE_PATH);
//            bitmap = (Bitmap)Image.FromFile(IMAGE_PATH);
//        }

        public void TestOne(){
            Tester.Ensure(image != null);
            Tester.Ensure(bitmap != null);
        }

	}
#endif
}
