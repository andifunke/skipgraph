package de.skipgraph;

import de.skipgraph.operations.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


public class SkipGraph {

	private Node skipGraphHead;
	private int minTableSize = 10;
	private int maxTableSize = 100;
	private int nodeCounter = 1;
	private final int logFactor = 2;


	public SkipGraph() {
	}

	public SkipGraph(int minTableSize, int maxTableSize) {
		this.minTableSize = minTableSize;
		this.maxTableSize = maxTableSize;
	}

	public int generatePrefix() {
		return Main.random.nextInt(logFactor);
	}

	public int getNodeCounter() {
		return nodeCounter++;
	}

	/*
	 local administration methodes - only for distributed skip graph
	  */
	public void init() {
		this.skipGraphHead = new Node(minTableSize, maxTableSize);
	}

	public void join() { }

	/*
	 global query methods
	  */
	// ToDo: use return codes (boolean or int) for all methods

	public Element get(int index) {
		GetOperation getOperation = new GetOperation(index);
		return sendQuery(getOperation).get(0);
	}

	public void input(Element element) {
		InputOperation inputOperation = new InputOperation(element);
		sendQuery(inputOperation);
	}

	public void delete(Element element) {
		DeleteOperation deleteOperation = new DeleteOperation(element);
		sendQuery(deleteOperation);
	}

	public void update(Element oldElement, Element newElement) {
		if (oldElement.getCapacity().equals(newElement.getCapacity()) &&
				oldElement.getContactIp() == newElement.getContactIp() &&
				oldElement.getContactPort() == newElement.getContactPort()) {
			delete(oldElement);
			input(newElement);
		}
		else {
			System.out.println("! " + oldElement + "->" + newElement + " <= source and capacity must be identical");
		}
	}

	// general search
	public List<Element> search(BigDecimal valueStart) {
		return search(valueStart, null, 0);
	}

	public List<Element> search(BigDecimal valueStart, int maxNumberOfVals) {
		return search(valueStart, null, maxNumberOfVals);
	}

	public List<Element> search(BigDecimal valueStart, BigDecimal valueEnd) {
		return search(valueStart, valueEnd, 0);
	}

	public List<Element> search(BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		SearchOperation searchOperation = new SearchOperation(valueStart, valueEnd, maxNumberOfVals);
		return sendQuery(searchOperation);
	}

	// search for capacity
	public List<Element> search(String capacity, BigDecimal valueStart) {
		return search(capacity, valueStart, null, 0);
	}

	public List<Element> search(String capacity, BigDecimal valueStart, int maxNumberOfVals) {
		return search(capacity, valueStart, null, maxNumberOfVals);
	}

	public List<Element> search(String capacity, BigDecimal valueStart, BigDecimal valueEnd) {
		return search(capacity, valueStart, valueEnd, 0);
	}

	public List<Element> search(String capacity, BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		if (valueEnd != null && valueStart != null && valueStart.compareTo(valueEnd) > 0) {
			BigDecimal tmp = valueEnd;
			valueEnd = valueStart;
			valueStart = tmp;
		}
		SearchOperation searchOperation = new SearchOperation(capacity, valueStart, valueEnd, maxNumberOfVals);
		return sendQuery(searchOperation);
	}

	/*
		message methods
		 */
	public List<Element> sendQuery(QueryOperation queryOperation) {
		return skipGraphHead.execute(queryOperation);
	}


	public void printResult(Element element) {
		System.out.println(" " + element + "\n");
	}

	public void printResult(List<Element> elements) {
		Collections.sort(elements);
		System.out.println(" " + elements + "\n");
	}

	/*
		debugging
	 */
	public void print() {
		System.out.println(Main.headline("printing graph"));
		if (skipGraphHead != null) {
			int i = 0;
			Node next = skipGraphHead;
			do {
				i++;
				System.out.println(String.format("> node #%d (id %s)", i, next));
				next.printElementTable();
				next.printContactTable();
				next = next.getContactTable().getNextNode();
				if (next == null) {
					System.out.println("link to next node missing - aborting...");
					break;
				}
				System.out.println();
			} while (next != skipGraphHead);
		}
		else {
			System.out.println("SkipGraph is empty");
		}
	}

	public void buildDotFile(String filename) {
		buildDotFile(filename, false, false);
	}

	public void buildDotFile(String filename, Boolean plotAutomatically) {
		buildDotFile(filename, plotAutomatically, false);
	}

	public void buildDotFile(String filename, Boolean plotAutomatically, Boolean useCluster) {
		if (skipGraphHead != null) {
			DotFileBuilder dfb = new DotFileBuilder(skipGraphHead, plotAutomatically, useCluster);
			//dfb.print();
			dfb.writeFile(filename);
		}
		else {
			System.out.println("SkipGraph is empty");
		}

	}
}
