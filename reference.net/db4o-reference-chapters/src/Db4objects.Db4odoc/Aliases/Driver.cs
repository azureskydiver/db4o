namespace Db4objects.Db4odoc.Aliases
{
    class Driver
    {

        private string name;
        private int points;

        public Driver(string name, int points)
        {
            this.name = name;
            this.points = points;
        }

        public string Name
        {
            get
            {
                return name;
            }
            set
            {
                name = value;
            }
        }

        public override string ToString()
        {
            return string.Format("{0}/{1}", name, points);
        }
    }
}
