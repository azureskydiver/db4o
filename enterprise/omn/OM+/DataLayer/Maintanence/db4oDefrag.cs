using System;
using System.Collections.Generic;
using System.Text;
using  OManager.BusinessLayer.Login;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.Defragment;
using OManager.DataLayer.Connection;

using OME.Logging.Common;
using OME.Logging.Tracing;
namespace OManager.DataLayer.Maintanence
{
    //add methods for defrag and backup
    class db4oDefrag
    {
        private string m_connectionPath;

        public db4oDefrag(string path)
        {
            m_connectionPath = path;
        }

        private const int DEFAULT_OBJECT_COMMIT_FREQUENCY = 500000;

        public void db4oDefragDatabase()
        {
            try
            {               
                DefragmentConfig defragConfig = new DefragmentConfig(m_connectionPath);
                defragConfig.Db4oConfig(newConfiguration());
                defragConfig.ForceBackupDelete(true);
                defragConfig.ObjectCommitFrequency(DEFAULT_OBJECT_COMMIT_FREQUENCY);
                Defragment.Defrag(defragConfig);
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);              

            }
        }
        

        public IConfiguration newConfiguration()
        {
            Db4oFactory.Configure().ReadOnly(false);
            IConfiguration config = Db4oFactory.NewConfiguration();
            config.ActivationDepth(int.MaxValue);
            config.UpdateDepth(int.MaxValue) ;
            return config;

        }

    }

}
