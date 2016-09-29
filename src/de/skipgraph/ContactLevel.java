package de.skipgraph;

public class ContactLevel {

	private int prefix;
	private Contact prevContact;
	private Contact nextContact;

	/*
	public ContactLevel(Contact prevContact, Contact nextContact) {
		this.prevContact = prevContact;
		this.nextContact = nextContact;
	}
	*/

	public ContactLevel(Contact prevContact, Contact nextContact, int prefix) {
		this.prefix = prefix;
		this.prevContact = prevContact;
		this.nextContact = nextContact;
	}

	public Contact getPrevContact() {
		return prevContact;
	}

	public void setPrevContact(Contact prevContact) {
		this.prevContact = prevContact;
	}

	public void setPrevContact(Node node) {
		this.prevContact = new Contact(node);
	}

	public Contact getNextContact() {
		return nextContact;
	}

	public void setNextContact(Contact nextContact) {
		this.nextContact = nextContact;
	}

	public void setNextContact(Node node) {
		this.nextContact = new Contact(node);
	}

	public int getPrefix() {
		return prefix;
	}

	public String toString() {
		String prevNode = getPrevContact().getNode().toString();
		String prevStart = getPrevContact().getRangeStart() == null ? "-inf" : getPrevContact().getRangeStart().toString();
		String prevEnd = getPrevContact().getRangeEnd() == null ? "inf" : getPrevContact().getRangeEnd().toString();
		String nextNode = getNextContact().getNode().toString();
		String nextStart = getNextContact().getRangeStart() == null ? "-inf" : getNextContact().getRangeStart().toString();
		String nextEnd = getNextContact().getRangeEnd() == null ? "inf" : getNextContact().getRangeEnd().toString();
		return String.format("prev: %s [%s, %s), next: %s [%s, %s)",
				prevNode, prevStart, prevEnd, nextNode, nextStart, nextEnd);
	}

}
