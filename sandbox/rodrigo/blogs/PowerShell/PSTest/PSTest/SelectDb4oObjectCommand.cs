using System.Collections;
using System.Management.Automation;
using Db4objects.Db4o;

namespace CmdLets.Db4objects
{
    [Cmdlet(VerbsCommon.Select, "db4o-object")]
    public class SelectDb4oObjectCommand : Db4oObjectCommandBase
    {
        protected override void ProcessRecord()
        {
            using (var container = Db4oEmbedded.OpenFile(Configure(), DatabasePath))
            {
                DumpResults(container.Query().Execute());
            }
        }

        private void DumpResults(IEnumerable results)
        {
			//foreach (var result in results)
			//{
			//    if (Match((PSObject) result))
			//    { 
			//        WriteObject(result); 
			//    }
			//}
			IEnumerator enumerator = results.GetEnumerator();
			if (enumerator.MoveNext())
			{
				WriteObject(enumerator.Current);
			}
        }

        private bool Match(PSObject o)
        {
            foreach (var property in o.Properties)
            {
                //if (property.Value )
            }
            return false;
        }
        
		//[Parameter(Mandatory = true)]
		//public string Value
		//{
		//    set
		//    {
		//        _value = value;
		//    }
		//}

        private string _value;
    }
}