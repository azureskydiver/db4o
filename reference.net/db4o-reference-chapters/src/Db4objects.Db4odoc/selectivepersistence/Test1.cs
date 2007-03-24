namespace Db4objects.Db4odoc.selectivepersistence
{
    class Test1
    {
        private string name;
        private NotStorable transientClass;

        public Test1(string name, NotStorable transientClass)
        {
            this.name = name;
            this.transientClass = transientClass;
        }

        public override string ToString()
        {
            if (transientClass == null)
            {
                return string.Format("{0}/{1}", name, "null");
            }
            else
            {
                return string.Format("{0}/{1}", name, transientClass);
            }
        }
    }
}
