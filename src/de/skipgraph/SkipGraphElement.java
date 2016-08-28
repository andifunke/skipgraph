package de.skipgraph;

import java.math.BigDecimal;

public class SkipGraphElement {

	private String capacity;
	private BigDecimal value;
	private int contactIp;
	private int contactPort;

	public SkipGraphElement(String capacity, BigDecimal value, int contactIp, int contactPort) {
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
}
