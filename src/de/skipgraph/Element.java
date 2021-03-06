package de.skipgraph;

import java.math.BigDecimal;

public class Element implements Comparable<Element>{

	private String capacity;
	private BigDecimal value;
	private int contactIp;
	private int contactPort;

	public Element(String capacity, BigDecimal value, int contactIp, int contactPort) {
		this.capacity = capacity;
		this.value = value;
		this.contactIp = contactIp;
		this.contactPort = contactPort;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public int getContactIp() {
		return contactIp;
	}

	public void setContactIp(int contactIp) {
		this.contactIp = contactIp;
	}

	public int getContactPort() {
		return contactPort;
	}

	public void setContactPort(int contactPort) {
		this.contactPort = contactPort;
	}

	public String toString() {
		return String.format("(%s, %s, %s:%d)", value, capacity, intToIp(contactIp), contactPort);
	}

	/**
	 * Methode erstellt String-IP aus gegebenem int
	 * @param i		beliebiger Integer, der eine IPv4-Adresse repräsentiert
	 * @return		als IP formatierter String
	 */
	public static String intToIp(int i) {
		return ((i >> 24 ) & 0xFF) + "." +
				((i >> 16 ) & 0xFF) + "." +
				((i >>  8 ) & 0xFF) + "." +
				( i        & 0xFF);
	}

	@Override
	public int compareTo(Element t) {
		return this.value.compareTo(t.getValue());
	}
}
