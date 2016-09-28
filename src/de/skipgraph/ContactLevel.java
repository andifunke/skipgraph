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
		return String.format("(next: %s, prev: %s)",
				getNextContact().getNode(), getPrevContact().getNode());
	}

}
