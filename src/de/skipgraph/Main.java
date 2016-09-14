package de.skipgraph;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		// building SkipGraph
		SkipGraph skipGraph = new SkipGraph(3, 10);
		//skipGraph.printGraph();
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
		//skipGraph.printGraph();

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
		elements.add(new Element("MEM", new BigDecimal(10.1234), 5, 987));
		elements.add(new Element("STOR", new BigDecimal(5), 5, 987));
		elements.add(new Element("CPU", new BigDecimal(5000), 6, 987));
		elements.forEach(skipGraph::input);
		skipGraph.printGraph();

		System.out.println(headline("testing delete"));
		skipGraph.delete(elements.get(3));
		skipGraph.delete(elements.get(3));

		System.out.println(headline("testing update"));
		skipGraph.update(elements.get(2), new Element("MEM", new BigDecimal(9), 3, 7890));
		skipGraph.update(elements.get(2), new Element("BW", new BigDecimal(9), 3, 7890));
		skipGraph.update(elements.get(2), new Element("MEM", new BigDecimal(9), 2, 7890));
		skipGraph.update(elements.get(2), new Element("MEM", new BigDecimal(9), 3, 1231));
		skipGraph.printGraph();

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
		skipGraph.printGraph();

	}

	public static String headline(String s) {
		return "\n\n>> " + s.toUpperCase() + " <<";
	}

}
