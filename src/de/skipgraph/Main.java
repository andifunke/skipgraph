package de.skipgraph;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Main {

	public static SkipGraph skipGraph = new SkipGraph(10, 30);
	private static long seed = 0L;
	public static Random random = new Random(seed);

	public static void main(String[] args) {

		skipGraph.init();

		// building SkipGraph
		//SkipGraph skipGraph = new SkipGraph(2, 6);
		//skipGraph.print();
		List<Element> elements = new LinkedList<>();
		elements.add(new Element("CPU", new BigDecimal(2), 1, 1230));
		elements.add(new Element("BW", new BigDecimal(40), 1, 1230));
		//System.out.println(elements.get(0).compareTo(elements.get(1)));
		elements.add(new Element("MEM", new BigDecimal(8), 1, 1230));
		//System.out.println(elements.get(1).compareTo(elements.get(2)));
		elements.add(new Element("STOR", new BigDecimal(160), 1, 1230));
		elements.add(new Element("STOR", new BigDecimal(160), 1, 1230));
		elements.add(new Element("CPU", new BigDecimal(20), 2, 4560));
		elements.add(new Element("BW", new BigDecimal(4), 2, 4560));
		elements.add(new Element("MEM", new BigDecimal(80), 2, 4560));
		elements.add(new Element("STOR", new BigDecimal(16), 2, 4560));

		System.out.println(headline("testing input"));
		elements.forEach(skipGraph::input);
		//skipGraph.print();

		elements.clear();
		elements.add(new Element("CPU", new BigDecimal(1), 3, 7890));
		elements.add(new Element("BW", new BigDecimal(10), 3, 7890));
		elements.add(new Element("MEM", new BigDecimal(100), 3, 7890));
		elements.add(new Element("STOR", new BigDecimal(1000), 3, 7890));
		elements.add(new Element("CPU", new BigDecimal(2), 4, 987));
		elements.add(new Element("BW", new BigDecimal(20), 4, 987));
		elements.add(new Element("MEM", new BigDecimal(200), 4, 987));
		elements.add(new Element("STOR", new BigDecimal(2000), 4, 987));
		elements.add(new Element("CPU", new BigDecimal("0.5"), 5, 987));
		elements.add(new Element("BW", new BigDecimal("2.7"), 5, 987));
		elements.add(new Element("MEM", new BigDecimal("10.1234"), 5, 987));
		elements.add(new Element("STOR", new BigDecimal(5), 5, 987));
		elements.add(new Element("CPU", new BigDecimal(5000), 6, 987));
		for (int i=0; i<1000; i++) {
			int rand = Main.random.nextInt(4);
			String capacity = "";
			switch (rand) {
				case 0:
					capacity = "CPU";
					break;
				case 1:
					capacity = "BW";
					break;
				case 2:
					capacity = "STOR";
					break;
				case 3:
					capacity = "MEM";
					break;
			}
			BigDecimal value = new BigDecimal(Main.random.nextInt(10000));
			int ip = Main.random.nextInt(10000);
			int port = Main.random.nextInt(10000);
			elements.add(new Element(capacity, value, ip, port));
		}
		elements.forEach(skipGraph::input);
		//skipGraph.print();
		//skipGraph.buildDotFile("final_cluster.dot", true, true);
		//skipGraph.buildDotFile("final.dot", true);
		//System.exit(0);

		skipGraph.delete(elements.get(3));
		skipGraph.delete(elements.get(3));

		System.out.println(headline("testing update"));
		skipGraph.update(elements.get(2), new Element("MEM", new BigDecimal(9), 3, 7890));
		skipGraph.update(elements.get(2), new Element("BW", new BigDecimal(9), 3, 7890));
		skipGraph.update(elements.get(2), new Element("MEM", new BigDecimal(9), 2, 7890));
		skipGraph.update(elements.get(2), new Element("MEM", new BigDecimal(9), 3, 1231));
		skipGraph.print();

		System.out.println(headline("testing get"));
		try {
			skipGraph.printResult(skipGraph.get(3));
		} catch (NullPointerException e) {
		}
		try {
			skipGraph.printResult(skipGraph.get(13));
		} catch (NullPointerException e) {
		}
		try {
			skipGraph.printResult(skipGraph.get(130));
		} catch (NullPointerException e) {
		}

		System.out.println(headline("testing search"));
		skipGraph.printResult(skipGraph.search("CPU", new BigDecimal(2), new BigDecimal(20)));
		skipGraph.printResult(skipGraph.search(new BigDecimal(2), new BigDecimal(20)));
		skipGraph.printResult(skipGraph.search("CPU", new BigDecimal(2)));
		skipGraph.printResult(skipGraph.search(new BigDecimal(20)));
		skipGraph.printResult(skipGraph.search(new BigDecimal(20), 3));
		skipGraph.printResult(skipGraph.search(new BigDecimal(10), 10));


		System.out.println(headline("testing leave"));
		skipGraph.delete(new Element("BW", new BigDecimal(20), 4, 987));
		skipGraph.delete(new Element("BW", new BigDecimal(40), 1, 1230));
		skipGraph.delete(new Element("MEM", new BigDecimal(80), 2, 4560));
		// deleting many elements
		int numberOfDeletedElements = elements.size()/2;
		for (int i=0; i<numberOfDeletedElements; i++) {
			int index = random.nextInt(elements.size());
			skipGraph.delete(elements.get(index));
		}

		//skipGraph.print();
		//skipGraph.buildDotFile("afterdelete.dot");

		skipGraph.buildDotFile("final_cluster.dot", true, true);
		skipGraph.buildDotFile("final.dot", true);

	}

	public static String headline(String s) {
		return "\n\n>> " + s.toUpperCase() + " <<";
	}

}
