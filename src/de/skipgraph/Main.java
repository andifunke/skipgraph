package de.skipgraph;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		// building SkipGraph
		SkipGraph skipGraph = new SkipGraph(5,10);
		List<SkipGraphElement> elements = new LinkedList<>();
		elements.add(new SkipGraphElement("CPU", new BigDecimal(2), 1, 1230));
		elements.add(new SkipGraphElement("BW", new BigDecimal(40), 1, 1230));
		elements.add(new SkipGraphElement("MEM", new BigDecimal(8), 1, 1230));
		elements.add(new SkipGraphElement("STOR", new BigDecimal(160), 1, 1230));
		elements.add(new SkipGraphElement("STOR", new BigDecimal(160), 1, 1230));
		elements.add(new SkipGraphElement("CPU", new BigDecimal(20), 2, 4560));
		elements.add(new SkipGraphElement("BW", new BigDecimal(4), 2, 4560));
		elements.add(new SkipGraphElement("MEM", new BigDecimal(80), 2, 4560));
		elements.add(new SkipGraphElement("STOR", new BigDecimal(16), 2, 4560));
		elements.add(new SkipGraphElement("CPU", new BigDecimal(1), 3, 7890));
		elements.add(new SkipGraphElement("BW", new BigDecimal(10), 3, 7890));
		elements.add(new SkipGraphElement("MEM", new BigDecimal(100), 3, 7890));
		elements.add(new SkipGraphElement("STOR", new BigDecimal(1000), 3, 7890));
		elements.forEach(skipGraph::input);
		skipGraph.delete(elements.get(3));
		skipGraph.delete(elements.get(3));
		skipGraph.update(elements.get(2), new SkipGraphElement("MEM", new BigDecimal(9), 1, 1230));
		skipGraph.update(elements.get(2), new SkipGraphElement("BW", new BigDecimal(9), 1, 1230));
		skipGraph.update(elements.get(2), new SkipGraphElement("MEM", new BigDecimal(9), 2, 1230));
		skipGraph.update(elements.get(2), new SkipGraphElement("MEM", new BigDecimal(9), 1, 1231));

		// testing get
		try {
			System.out.println(skipGraph.get(3).toString());
		} catch (NullPointerException e) {
		}
		try {
			System.out.println(skipGraph.get(13).toString());
		} catch (NullPointerException e) {
		}

		// testing search
		List<SkipGraphElement> list = skipGraph.search("CPU", new BigDecimal(2), new BigDecimal(20));
		for (SkipGraphElement element:
				list) {
			System.out.println(element.toString());
		}
		list = skipGraph.search(new BigDecimal(2), new BigDecimal(20));
		for (SkipGraphElement element:
			  list) {
			System.out.println(element.toString());
		}
		list = skipGraph.search("CPU", new BigDecimal(2));
		for (SkipGraphElement element:
				list) {
			System.out.println(element.toString());
		}
		list = skipGraph.search(new BigDecimal(20));
		for (SkipGraphElement element:
				list) {
			System.out.println(element.toString());
		}
		list = skipGraph.search(new BigDecimal(20), 3);
		for (SkipGraphElement element:
				list) {
			System.out.println(element.toString());
		}

	}
}
