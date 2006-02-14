namespace db4obrowser
{
    using System;
    using System.IO;
    using System.Reflection;
    using com.db4o;
    using com.db4o.f1;
    
    public class ExampleRunner
    {   
        /// <summary>
        /// Executes the method passed as argument if its signature is
        /// correct.
        /// </summary>
        /// <returns>true if the method was executed, false otherwise</returns>
        delegate bool Executor(MethodInfo method);
        
        Executor[] _executors = new Executor[] {
            new Executor(PlainExecutor),
            new Executor(ContainerExecutor),
            new Executor(LocalServerExecutor),
            new Executor(RemoteServerExecutor)
        };
        
        public void Reset()
        {
            File.Delete(Util.YapFileName);
        }
        
        public void Run(string typeName, string method, TextWriter console)
        {
            TextWriter saved = Console.Out;         
            Console.SetOut(console);
            try
            {
                RunExample(typeName, method);               
            }
            finally
            {
                Console.SetOut(saved);
            }
        }
        
        void RunExample(string typeName, string method)
        {
        	Type type = typeof(com.db4o.f1.Util).Assembly.GetType(typeName);
        	MethodInfo example = type.GetMethod(method, BindingFlags.IgnoreCase|BindingFlags.Static|BindingFlags.Public);
            
            bool found = false;
            foreach (Executor _e in _executors)
            {
                if (_e(example))
                {
                    found = true;
                    break;
                }
            }
            
            if (!found)
            {
                throw new ArgumentException("No executor found for method '" + example + "'");
            }
        }
    
        static bool ContainerExecutor(MethodInfo method)
        {
            if (!CheckSignature(method, typeof(ObjectContainer)))
            {
                return false;
            }       
            
            ObjectContainer container = Db4o.openFile(Util.YapFileName);
            try
            {
                method.Invoke(null, new object[] { container });
            }
            finally
            {
                container.close();
            }
            return true;
        }
        
        static bool LocalServerExecutor(MethodInfo method)
        {
            if (!CheckSignature(method, typeof(ObjectServer)))
            {
                return false;
            }
            
            ObjectServer server = Db4o.openServer(Util.YapFileName, 0);
            try
            {                
                method.Invoke(null, new object[] { server });
            }
            finally
            {
                server.close();
            }
            return true;
        }
        
        static bool RemoteServerExecutor(MethodInfo method)
        {
            if (!CheckSignature(method, typeof(int), typeof(string), typeof(string)))
            {
                return false;
            }
            
            ObjectServer server = Db4o.openServer(Util.YapFileName, Util.ServerPort);
            try
            {   
                server.grantAccess(Util.ServerUser, Util.ServerPassword);
                method.Invoke(null, new object[] { Util.ServerPort, Util.ServerUser, Util.ServerPassword });                
            }
            finally
            {
                server.close();
            }
            return true;
        }
    
        static bool PlainExecutor(MethodInfo method)
        {
            if (0 != method.GetParameters().Length)
            {
                return false;
            }
            method.Invoke(null, new object[0]);
            return true;
        }
        
        static bool CheckSignature(MethodInfo method, params Type[] types)
        {
            ParameterInfo[] parameters = method.GetParameters();
            if (types.Length != parameters.Length)
            {
                return false;
            }
            
            for (int i=0; i<parameters.Length; ++i)
            {
                if (types[i] != parameters[i].ParameterType)
                {
                    return false;
                }
            }
            return true;
        }
    }
}
