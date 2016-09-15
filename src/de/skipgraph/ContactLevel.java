package de.skipgraph;

public class ContactLevel {

	private byte prefix;
	private Contact prevContact;
	private Contact nextContact;

	/*
	public ContactLevel(Contact prevContact, Contact nextContact) {
		this.prevContact = prevContact;
		this.nextContact = nextContact;
	}
	*/

	public ContactLevel(Contact prevContact, Contact nextContact, byte prefix) {
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

	public void setPrevContact(Node prevNode) {
		this.nextContact = new Contact(prevNode);
	}

	public Contact getNextContact() {
		return nextContact;
	}

	public void setNextContact(Contact nextContact) {
		this.nextContact = nextContact;
	}

	public void setNextContact(Node nextNode) {
		this.nextContact = new Contact(nextNode);
	}

	public byte getPrefix() {
		return prefix;
	}

	/*
	public void setPrefix(int prefix) {
		this.prefix = prefix;
	}
	*/

}