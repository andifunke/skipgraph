package de.skipgraph;

import java.math.BigDecimal;

public class Contact {

	private Node node;
	private BigDecimal rangeStart;
	private BigDecimal rangeEnd;

	public Contact(Node node) {
		this.node = node;
		this.rangeStart = node.getElementTable().getRangeStart();
		this.rangeEnd = node.getElementTable().getRangeEnd();
	}

	public Contact(Node node, BigDecimal rangeStart, BigDecimal rangeEnd) {
		this.node = node;
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public BigDecimal getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(BigDecimal rangeStart) {
		this.rangeStart = rangeStart;
	}

	public BigDecimal getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(BigDecimal rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

}
