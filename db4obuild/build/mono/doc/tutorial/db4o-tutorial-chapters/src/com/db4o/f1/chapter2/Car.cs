namespace com.db4o.f1.chapter2
{
    public class Car
    {
        string _model;
        Pilot _pilot;
        
        public Car(string model)
        {
            _model = model;
            _pilot = null;
        }
      
        public Pilot Pilot
        {
            get
            {
                return _pilot;
            }
            
            set
            {
                _pilot = value;
            }
        }
        
        public string Model         
        {
            get
            {
                return _model;
            }
        }
        
        override public string ToString()
        {
            return _model + "[" + _pilot + "]";
        }
    }
}
