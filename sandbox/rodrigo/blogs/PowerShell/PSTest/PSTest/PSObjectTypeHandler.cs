using System.Collections.Generic;
using System.Management.Automation;
using Db4objects.Db4o.Foundation;
using Db4objects.Db4o.Internal;
using Db4objects.Db4o.Internal.Delete;
using Db4objects.Db4o.Internal.Handlers;
using Db4objects.Db4o.Marshall;
using Db4objects.Db4o.Typehandlers;

namespace CmdLets.Db4objects
{
    class PSObjectTypeHandler : ITypeHandler4
    {
        public IPreparedComparison PrepareComparison(IContext context, object obj)
        {
            throw new System.NotImplementedException();
        }

        public void Delete(IDeleteContext context)
        {
            context.Delete(TypeHandler());
        }

        public void Defragment(IDefragmentContext context)
        {
            context.Defragment(TypeHandler());
        }

        public object Read(IReadContext context)
        {
            var wrapper = (PSObjectWrapper) context.ReadObject(TypeHandler());
            return (PSObject) wrapper;
        }

        public void Write(IWriteContext context, object obj)
        {
            context.WriteObject(TypeHandler(), new PSObjectWrapper((PSObject) obj));
        }

        private static FirstClassObjectHandler TypeHandler()
        {
            return new FirstClassObjectHandler();
        }
    }

    internal class PSObjectWrapper
    {
        public PSObjectWrapper(PSObject obj)
        {
            foreach (PSPropertyInfo member in obj.Properties)
            {
                members[member.Name] = member.Value;
            }

            foreach (PSMethodInfo method in obj.Methods)
            {
                var scriptMethod = method.Value as PSScriptMethod;
                if (null != scriptMethod)
                {
                    methods[method.Name] = scriptMethod.Script.ToString();
                }
            }
        }

        public static implicit operator PSObject(PSObjectWrapper wrapper)
        {
            var obj = new PSObject();
            foreach (var pair in wrapper.members)
            {
                obj.Properties.Add(new PSNoteProperty(pair.Key, pair.Value));
            }

            foreach (var pair in wrapper.methods)
            {
                obj.Methods.Add(new PSScriptMethod(pair.Key, Db4oObjectCommandBase.Instance.InvokeCommand.NewScriptBlock(pair.Value)));
            }

            return obj;
        }

        private readonly IDictionary<string, object> members = new Dictionary<string, object>();
        private readonly IDictionary<string, string> methods = new Dictionary<string, string>();
    }
}
