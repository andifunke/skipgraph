package de.skipgraph.operations;

import de.skipgraph.SkipGraphNode;

import java.math.BigDecimal;

public class SearchOperation extends QueryOperation {

	private String capacity;
	private BigDecimal valueStart;
	private BigDecimal valueEnd;
	private int maxNumberOfVals;

	public SearchOperation(BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		this("any", valueStart, valueEnd, maxNumberOfVals);
	}

	public SearchOperation(String capacity, BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		//super(QueryType.SEARCH);
		this.capacity = capacity;
		this.valueStart = valueStart;
		this.valueEnd = valueEnd;
		this.maxNumberOfVals = maxNumberOfVals;
	}

	public BigDecimal getValueStart() {
		return valueStart;
	}

	public void setValueStart(BigDecimal valueStart) {
		this.valueStart = valueStart;
	}

	public BigDecimal getValueEnd() {
		return valueEnd;
	}

	public void setValueEnd(BigDecimal valueEnd) {
		this.valueEnd = valueEnd;
	}

	public int getMaxNumberOfVals() {
		return maxNumberOfVals;
	}

	public void setMaxNumberOfVals(int maxNumberOfVals) {
		this.maxNumberOfVals = maxNumberOfVals;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public void execute(SkipGraphNode node) {
	}
	}
