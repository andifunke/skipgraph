package de.skipgraph;

import de.skipgraph.operations.*;
import java.math.BigDecimal;
import java.util.List;


public class SkipGraph {

	private SkipGraphNode skipGraphHead;

	public SkipGraph() {
		this.skipGraphHead = new SkipGraphNode();
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

	public SkipGraphElement get (int index) {
		GetOperation getOperation = new GetOperation(index);
		return sendQuery(getOperation).get(0);
	}

	public void input (SkipGraphElement element) {
		InputOperation inputOperation = new InputOperation(element);
		sendQuery(inputOperation);
	}

	public void delete (SkipGraphElement element) {
		DeleteOperation deleteOperation = new DeleteOperation(element);
		sendQuery(deleteOperation);
	}

	public void update (SkipGraphElement oldElement, SkipGraphElement newElement) {
		delete(oldElement);
		input(newElement);
	}

	public List<SkipGraphElement> search (BigDecimal valueStart) {
		return search(valueStart, null, 0);
	}

	public List<SkipGraphElement> search (BigDecimal valueStart, int maxNumberOfVals) {
		return search(valueStart, null, maxNumberOfVals);
	}

	public List<SkipGraphElement> search (BigDecimal valueStart, BigDecimal valueEnd) {
		return search(valueStart, valueEnd, 0);
	}

	public List<SkipGraphElement> search (BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		SearchOperation searchOperation = new SearchOperation(valueStart, valueEnd, maxNumberOfVals);
		return sendQuery(searchOperation);
	}


	/*
		message methods
		 */
	public List<SkipGraphElement> sendQuery (QueryOperation queryOperation) {
		return skipGraphHead.execute(queryOperation);
	}

}
