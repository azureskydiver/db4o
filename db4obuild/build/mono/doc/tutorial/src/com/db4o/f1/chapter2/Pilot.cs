namespace com.db4o.f1.chapter2
{
    public class Pilot
    {
        string _name;
        int _points;
        
        public Pilot(string name, int points)
        {
            _name = name;
            _points = points;
        }
        
        public int Points
        {
            get
            {
                return _points;
            }
        }
        
        public void AddPoints(int points)
        {
            _points += points;
        }
        
        public string Name
        {
            get
            {
                return _name;
            }
        }
        
        override public string ToString()
        {
            return _name + "/" + _points;
        }
    }
}
