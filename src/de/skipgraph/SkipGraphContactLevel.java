package de.skipgraph;

import java.math.BigDecimal;

public class SkipGraphContactLevel {

	private int level;
	private SkipGraphContact prev;
	private SkipGraphContact next;

	public SkipGraphContactLevel(SkipGraphContact prev, SkipGraphContact next) {
		this.prev = prev;
		this.next = next;
	}

	public SkipGraphContactLevel(SkipGraphContact prev, SkipGraphContact next, int level) {
		this.level = level;
		this.prev = prev;
		this.next = next;
	}

	public SkipGraphContact getPrev() {
		return prev;
	}

	public void setPrev(SkipGraphContact prev) {
		this.prev = prev;
	}

	public SkipGraphContact getNext() {
		return next;
	}

	public void setNext(SkipGraphContact next) {
		this.next = next;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
