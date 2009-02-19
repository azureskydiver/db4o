namespace OManager.BusinessLayer.Login
{
    public class ConnParams
    {
        private readonly string m_connection;
        private readonly string m_host;
        private readonly int m_port;
        private readonly string m_userName;
        private readonly string m_passWord;

		public ConnParams(string connection) : this(connection, null, null, null, 0)
		{
		}

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
            get { return m_connection; }
        }

        public string Host
        {
            get { return m_host; }           

        }
        public int Port
        {
            get { return m_port; }           
        }

        public string UserName
        {
            get { return m_userName; }            
        }
        public string PassWord
        {

            get { return m_passWord; }
        }        
    }
}
