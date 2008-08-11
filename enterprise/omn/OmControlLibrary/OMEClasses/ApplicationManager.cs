using System;
using System.Text;
using System.Resources;
using System.Threading;
using System.Globalization;
using System.Collections.Generic;
using OMControlLibrary.Common;
using System.Windows.Forms;

namespace OMControlLibrary
{
	public class ApplicationManager
	{

		#region Private Member

		//Resource related member variables
		private static ResourceManager m_ResourceManager;
		private static LanguageCodes m_SelectedLanguage = LanguageCodes.English;

		#endregion


		#region Properties

		public static ResourceManager LanguageResource
		{
			get
			{
				return m_ResourceManager;
			}
		}


		#endregion


		#region Public Methods

		public static bool CheckLocalAndSetLanguage()
		{
			try
			{
				if (CultureInfo.CurrentCulture.Name.Equals(Common.Constants.JAPANESE_CULTURE))
					return SetLanguage(LanguageCodes.Japanese);
				else
					return SetLanguage(LanguageCodes.English);

			}
			catch (Exception)
			{
				return false;
			}
		}


		/// <summary>
		/// This function sets the current UI culture and resource manager
		/// </summary>
		/// <param name="lngCode">LanguageCode</param>
		/// <returns>True or False.</returns>
		static internal bool SetLanguage(LanguageCodes lngCode)
		{
			try
			{
				bool languageStatus = false;
				string languageCulture = Common.Constants.ENGLISH_CULTURE;
				if (lngCode == LanguageCodes.English)
				{
					m_ResourceManager = new ResourceManager(Common.Constants.RESOURCE_NAME, typeof(OMControlLibrary.ViewBase).Assembly);
					languageCulture = Common.Constants.ENGLISH_CULTURE;
				}
				else if (lngCode == LanguageCodes.Japanese)
				{
					m_ResourceManager = new ResourceManager(Common.Constants.RESOURCE_NAME_JP, typeof(OMControlLibrary.ViewBase).Assembly);
					languageCulture = Common.Constants.JAPANESE_CULTURE;
				}
				else
				{
					m_ResourceManager = new ResourceManager(Common.Constants.RESOURCE_NAME, typeof(OMControlLibrary.ViewBase).Assembly);
					languageCulture = Common.Constants.ENGLISH_CULTURE;
				}

				languageStatus = true;
				Thread.CurrentThread.CurrentCulture = new CultureInfo(languageCulture, true);
				System.Threading.Thread.CurrentThread.CurrentUICulture =
					new CultureInfo(languageCulture, true);

				m_SelectedLanguage = lngCode;
				return languageStatus;
			}
			catch (Exception)
			{
				return false;
			}
		}//End of the function SetLnaguage.

		#endregion
	}
}
