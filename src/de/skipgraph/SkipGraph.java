package de.skipgraph;

import de.skipgraph.operations.*;
import java.math.BigDecimal;
import java.util.List;


public class SkipGraph {

	private SkipGraphNode skipGraphHead;
	private int minTableSize = 10;
	private int maxTableSize = 100;


	public SkipGraph() {
		this.skipGraphHead = new SkipGraphNode(minTableSize, maxTableSize);
	}

	public SkipGraph(int minTableSize, int maxTableSize) {
		this.minTableSize = minTableSize;
		this.maxTableSize = maxTableSize;
		this.skipGraphHead = new SkipGraphNode(minTableSize, maxTableSize);
	}


	/*
	 local administration methodes - only for distributed skip graph
	  */
	public void initialize() {	}
	public void join() {	}


	/*
	 global query methods
	  */
	// ToDo: use return codes (boolean or int) for all methods

	public SkipGraphElement get(int index) {
		GetOperation getOperation = new GetOperation(index);
		return sendQuery(getOperation).get(0);
	}

	public void input(SkipGraphElement element) {
		InputOperation inputOperation = new InputOperation(element);
		sendQuery(inputOperation);
	}

	public void delete(SkipGraphElement element) {
		DeleteOperation deleteOperation = new DeleteOperation(element);
		sendQuery(deleteOperation);
	}

	public void update(SkipGraphElement oldElement, SkipGraphElement newElement) {
		if (oldElement.getCapacity().equals(newElement.getCapacity()) &&
				oldElement.getContactIp() == newElement.getContactIp() &&
				oldElement.getContactPort() == newElement.getContactPort()) {
			delete(oldElement);
			input(newElement);
		} else {
			System.out.println("source and capacity must be identical");
		}
	}

	// general search
	public List<SkipGraphElement> search(BigDecimal valueStart) {
		return search(valueStart, null, 0);
	}

	public List<SkipGraphElement> search(BigDecimal valueStart, int maxNumberOfVals) {
		return search(valueStart, null, maxNumberOfVals);
	}

	public List<SkipGraphElement> search(BigDecimal valueStart, BigDecimal valueEnd) {
		return search(valueStart, valueEnd, 0);
	}

	public List<SkipGraphElement> search(BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		SearchOperation searchOperation = new SearchOperation(valueStart, valueEnd, maxNumberOfVals);
		return sendQuery(searchOperation);
	}

	// search for capacity
	public List<SkipGraphElement> search(String capacity, BigDecimal valueStart) {
		return search(capacity, valueStart, null, 0);
	}

	public List<SkipGraphElement> search(String capacity, BigDecimal valueStart, int maxNumberOfVals) {
		return search(capacity, valueStart, null, maxNumberOfVals);
	}

	public List<SkipGraphElement> search(String capacity, BigDecimal valueStart, BigDecimal valueEnd) {
		return search(capacity, valueStart, valueEnd, 0);
	}

	public List<SkipGraphElement> search(String capacity, BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
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
	public List<SkipGraphElement> sendQuery (QueryOperation queryOperation) {
		return skipGraphHead.execute(queryOperation);
	}



}
