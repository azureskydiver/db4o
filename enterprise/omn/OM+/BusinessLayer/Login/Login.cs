using System;
using System.Collections.Generic;
using System.Text;

namespace OManager.BusinessLayer.Login
{
    class Login
    {
        private string m_connection;
        private string m_host;
        private string m_port;
        private string m_userName;
        private string m_passWord;

        //Write properties fo all the above and make them priavate.
        
        
        public string Connection
        {

            get { return this.m_connection; }
            set
            {
                this.m_connection = value;  
            }
       
    }

        public string Host
        {

            get { return this.m_host; }
            set
            {
                this.m_host = value;
            }

        }
        public string Port
        {

            get { return this.m_port; }
            set
            {
                this.m_port = value;
            }

        }

        public string UserName
        {

            get { return this.m_userName; }
            set
            {
                this.m_userName = value;
            }

        }
        public string PassWord
        {

            get { return this.m_passWord; }
            set
            {
                this.m_passWord = value;
            }

        }
        




    }
}
