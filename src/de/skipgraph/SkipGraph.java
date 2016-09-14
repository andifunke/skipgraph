package de.skipgraph;

import de.skipgraph.operations.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


public class SkipGraph {

	private Node skipGraphHead;
	private int minTableSize = 10;
	private int maxTableSize = 100;


	public SkipGraph() {
		this.skipGraphHead = new Node(minTableSize, maxTableSize);
	}

	public SkipGraph(int minTableSize, int maxTableSize) {
		this.minTableSize = minTableSize;
		this.maxTableSize = maxTableSize;
		this.skipGraphHead = new Node(minTableSize, maxTableSize);
	}


	/*
	 local administration methodes - only for distributed skip graph
	  */
	public void initialize() { }

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

	public void printGraph() {
		System.out.println(Main.headline("printing graph"));
		int i = 0;
		System.out.print(String.format("### %02d ", i));
		skipGraphHead.printElementTable();
		Node next = skipGraphHead.getContactTable().getNextNode();
		while (next != null && next != skipGraphHead) {
			i++;
			System.out.print(String.format("### %02d ", i));
			next.printElementTable();
			next = next.getContactTable().getNextNode();
		}

	}

}
