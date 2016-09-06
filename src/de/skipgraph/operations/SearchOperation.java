package de.skipgraph.operations;

import de.skipgraph.SkipGraphElement;
import de.skipgraph.SkipGraphNode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SearchOperation extends QueryOperation {

	private String capacity;
	private BigDecimal valueStart;
	private BigDecimal valueEnd;
	private int maxNumberOfVals;

	public SearchOperation(BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		this(null, valueStart, valueEnd, maxNumberOfVals);
	}

	public SearchOperation(String capacity, BigDecimal valueStart, BigDecimal valueEnd, int maxNumberOfVals) {
		this.capacity = capacity;
		this.valueStart = valueStart;
		this.valueEnd = valueEnd;
		this.maxNumberOfVals = maxNumberOfVals;
	}

	public BigDecimal getValueStart() {
		return valueStart;
	}

	public void setValueStart(BigDecimal valueStart) {
		this.valueStart = valueStart;
	}

	public BigDecimal getValueEnd() {
		return valueEnd;
	}

	public void setValueEnd(BigDecimal valueEnd) {
		this.valueEnd = valueEnd;
	}

	public int getMaxNumberOfVals() {
		return maxNumberOfVals;
	}

	public void setMaxNumberOfVals(int maxNumberOfVals) {
		this.maxNumberOfVals = maxNumberOfVals;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}

	@Override
	public List<SkipGraphElement> execute(SkipGraphNode node) {
		String endVal = valueEnd != null ? valueEnd.toString() : "infinity";
		String capStr = capacity != null ? " for " + capacity : "";
		String maxStr = maxNumberOfVals > 0 ? " (maximum " + maxNumberOfVals + " values)" : "";
		System.out.println("searching" + capStr + " from " + valueStart + " to " + endVal + maxStr);
		if (node.isAboveElementTablesMaximum(valueStart)) {
			System.out.println("value too big");
			return null;
		}
		else if (valueEnd != null && node.isBelowElementTablesMinimum(valueEnd)) {
			System.out.println("value too small");
			return null;
		}
		else {
			List<SkipGraphElement> list = new ArrayList<>();
			if (capacity == null) {
				if (valueEnd == null) {
					for (SkipGraphElement element :
							node.getElementTable()) {
						if (element.getValue().compareTo(valueStart) >= 0) {
							list.add(element);
							if (maxNumberOfVals > 0 && list.size() >= maxNumberOfVals) break;
						}
					}
				} else {
					for (SkipGraphElement element :
							node.getElementTable()) {
						if (element.getValue().compareTo(valueStart) >= 0 &&
								element.getValue().compareTo(valueEnd) <= 0) {
							list.add(element);
							if (maxNumberOfVals > 0 && list.size() >= maxNumberOfVals) break;
						}
					}
				}
			}
			else {
				if (valueEnd == null) {
					for (SkipGraphElement element :
							node.getElementTable()) {
						if (capacity.equals(element.getCapacity()) &&
								element.getValue().compareTo(valueStart) >= 0) {
							list.add(element);
							if (maxNumberOfVals > 0 && list.size() >= maxNumberOfVals) break;
						}
					}
				} else {
					for (SkipGraphElement element :
							node.getElementTable()) {
						if (capacity.equals(element.getCapacity()) &&
								element.getValue().compareTo(valueStart) >= 0 &&
								element.getValue().compareTo(valueEnd) <= 0) {
							list.add(element);
							if (maxNumberOfVals > 0 && list.size() >= maxNumberOfVals) break;
						}
					}
				}
			}
			return list;
		}
	}

}
