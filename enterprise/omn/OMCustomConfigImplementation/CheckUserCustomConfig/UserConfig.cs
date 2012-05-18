using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using OMCustomConfigImplementation.CustomConfigAssemblyInfo;
using OManager.DataLayer.Connection;

namespace OMCustomConfigImplementation.UserCustomConfig
{
    class UserConfig : MarshalByRefObject, IUserConfig
    {
        public bool CheckIfCustomConfigImplemented(bool local)
        {
            return ManageCustomConfig.CheckConfig(local);
        }
    }
}
