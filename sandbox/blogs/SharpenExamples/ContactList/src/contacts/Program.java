package contacts;

public class Program {

	public static void main(String[] args) {
		boolean running = true;
		while (running) {
			String option = Console.prompt("(a)dd new entry, (l)ist entries, (q)uit");
			if (option.isEmpty()) continue;
			switch (option.charAt(0)) {
			case 'q':
				running = false;
				break;
			default:
				System.out.println("'" + option + "' DOES NOT COMPUTE!");
			}
		}
	}
}
