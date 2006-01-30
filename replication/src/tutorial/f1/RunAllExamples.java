package f1;

import f1.collection.array.ArrayExample;
import f1.collection.list.ListExample;
import f1.collection.map.MapExample;
import f1.collection.set.SetExample;
import f1.one_to_one.OneToOneExample;
import f1.singleobject.SingleObjectExample;
import f1.updateevent.UpdateEventExample;
import org.apache.log4j.Logger;

public class RunAllExamples {
	Logger log = Logger.getLogger(RunAllExamples.class);

	public static void main(String[] args) {
		System.out.println("Running all Examples");

		SingleObjectExample.main(null);
		OneToOneExample.main(null);

		ArrayExample.main(null);
		ListExample.main(null);
		MapExample.main(null);
		SetExample.main(null);

		UpdateEventExample.main(null);

		System.out.println("All Examples Done!");
	}
}
