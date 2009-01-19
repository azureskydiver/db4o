using System.Windows.Forms;
using stdole;

namespace OMAddin
{
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
