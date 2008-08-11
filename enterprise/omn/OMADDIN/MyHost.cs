using System;
using System.Drawing;
using System.Windows.Forms;
using stdole;

namespace OMAddin
{
	// class MyHost : AxHost
	// {
	//     public MyHost() : base("59EE46BA-677D-4d20-BF10-8D8067CB8B33")
	//     {
	//     }
	//     public new static IPictureDisp GetIPictureDispFromPicture(Image image)
	//     {
	//         return (IPictureDisp)AxHost.GetIPictureDispFromPicture(image);
	//     }
	//}


	public class MyHost : System.Windows.Forms.AxHost
	{


		public MyHost()
			: base("59EE46BA-677D-4d20-BF10-8D8067CB8B33")
		{
		}

		public static IPictureDisp IPictureDisp(System.Drawing.Image Image)
		{
			return (IPictureDisp)AxHost.GetIPictureDispFromPicture(Image);
		}

	}
}
