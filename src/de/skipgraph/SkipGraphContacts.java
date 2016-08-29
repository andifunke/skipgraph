package de.skipgraph;

import java.math.BigDecimal;

public class SkipGraphContacts {

	private int level;
	private SkipGraphNode prev = null;
	private BigDecimal prevRangeStart = null;
	private BigDecimal prevRangeEnd = null;
	private SkipGraphNode next = null;
	private BigDecimal nextRangeStart = null;
	private BigDecimal nextRangeEnd = null;

	public SkipGraphContacts(SkipGraphNode prev, SkipGraphNode next) {
		this.prev = prev;
		this.next = next;
	}

	public SkipGraphContacts(SkipGraphNode prev, SkipGraphNode next, int level) {
		this.level = level;
		this.prev = prev;
		this.next = next;
	}

}
