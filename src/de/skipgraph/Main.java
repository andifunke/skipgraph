package de.skipgraph;

import java.math.BigDecimal;

public class Main {

	public static void main(String[] args) {

		SkipGraph skipGraph = new SkipGraph(5,20);
		SkipGraphElement element1 = new SkipGraphElement("CPU", new BigDecimal(20), 1, 2);
		skipGraph.input(element1);
		skipGraph.search(new BigDecimal(20));

	}
}
