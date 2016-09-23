package de.skipgraph;

public class ContactTupel {

	private String id;
	private int index;
	private ContactLevel level;

	public ContactTupel(String id, int index, ContactLevel level) {
		this.id = id;
		this.index = index;
		this.level = level;
	}

	public String getId() {
		return id;
	}

	public int getIndex() {
		return index;
	}

	public ContactLevel getLevel() {
		return level;
	}
}
