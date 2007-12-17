using System;

namespace Spikes {
    public class CustomCowEvent : CowEvent {
        string _id;

        public CustomCowEvent(Cow cow, DateTime date, string id)
            : base(cow, date) {
            _id = id;
        }

        public string Id {
            get { return _id; }
        }
    }
}
