using System;
using System.Collections.Generic;
using System.Text;

namespace OManager.BusinessLayer.Login
{
    public class ConnParams
    {
        private string m_connection;
        private string m_host;
        private int m_port;
        private string m_userName;
        private string m_passWord;

        public ConnParams(string connection, string host, string username, string password, int port)
        {
            m_connection = connection;
            m_host = host;
            m_userName = username;
            m_passWord = password;
            m_port = port; 

        }
        public string Connection
        {
            get { return this.m_connection; }
        }

        public string Host
        {
            get { return this.m_host; }           

        }
        public int Port
        {
            get { return this.m_port; }           
        }

        public string UserName
        {
            get { return this.m_userName; }            
        }
        public string PassWord
        {

            get { return this.m_passWord; }
        }        
    }
}
