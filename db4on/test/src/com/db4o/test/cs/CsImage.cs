/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Drawing;

namespace com.db4o.test.cs
{
	/// <summary>
	/// Summary description for CsImage.
	/// </summary>
	public class CsImage
	{

        private static String IMAGE_PATH = "C:\\CO.jpg";

        Image image;
        Bitmap bitmap;

        public void configure(){
            Db4o.configure().objectClass(typeof(Image)).translate(new TNull());
            Db4o.configure().objectClass(typeof(Bitmap)).translate(new TSerializable());
        }

//        public void storeOne(){
//            image = Image.FromFile(IMAGE_PATH);
//            bitmap = (Bitmap)Image.FromFile(IMAGE_PATH);
//        }

        public void testOne(){
            Test.ensure(image != null);
            Test.ensure(bitmap != null);
        }

	}
}
