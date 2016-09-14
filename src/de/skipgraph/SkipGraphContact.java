package de.skipgraph;

import java.math.BigDecimal;

public class SkipGraphContact {

	private SkipGraphNode node;
	private BigDecimal rangeStart;
	private BigDecimal rangeEnd;

	public SkipGraphContact(SkipGraphNode node) {
		this.node = node;
		this.rangeStart = null;
		this.rangeEnd = null;
	}

	public SkipGraphContact(SkipGraphNode node, BigDecimal rangeStart, BigDecimal rangeEnd) {
		this.node = node;
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
	}

	public SkipGraphNode getNode() {
		return node;
	}

	public void setNode(SkipGraphNode node) {
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
