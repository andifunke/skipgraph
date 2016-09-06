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
		// debug infos:
		String endVal = valueEnd != null ? valueEnd.toString() : "infinity";
		String capStr = capacity != null ? " for " + capacity : "";
		String maxStr = maxNumberOfVals > 0 ? " (maximum " + maxNumberOfVals + " values)" : "";
		System.out.println("searching" + capStr + " from " + valueStart + " to " + endVal + maxStr);

		// search range is above the node's table range ?
		if (node.isAboveElementTablesMaximum(valueStart)) {
			//System.out.println("  ! value too big");
			return node.getContacts().getNext().execute(this);
		}
		// search range is below the node's table range ?
		else if (valueEnd != null && node.isBelowElementTablesMinimum(valueEnd)) {
			//System.out.println("  ! value too small");
			return node.getContacts().getPrev().execute(this);
		}
		// search starts below the node's table range ?
		else if (valueStart != null && node.isBelowElementTablesMinimum(valueStart)) {
			//System.out.println("  ! value too small");
			return node.getContacts().getPrev().execute(this);
		}
		// search starts inside the node's table range.
		else {
			List<SkipGraphElement> retList = new ArrayList<>();

			// search without capacity
			if (capacity == null) {
				// open end
				if (valueEnd == null) {
					for (SkipGraphElement element :
							node.getElementTable()) {
						if (element.getValue().compareTo(valueStart) >= 0) {
							retList.add(element);
							if (maxNumberOfVals > 0 && retList.size() >= maxNumberOfVals) return retList;
						}
					}
				// closed end
				} else {
					for (SkipGraphElement element :
							node.getElementTable()) {
						if (element.getValue().compareTo(valueStart) >= 0 &&
								element.getValue().compareTo(valueEnd) <= 0) {
							retList.add(element);
							if (maxNumberOfVals > 0 && retList.size() >= maxNumberOfVals) return retList;
						}
					}
				}
			}

			// search with capacity
			else {
				// open end
				if (valueEnd == null) {
					for (SkipGraphElement element :
							node.getElementTable()) {
						if (capacity.equals(element.getCapacity()) &&
								element.getValue().compareTo(valueStart) >= 0) {
							retList.add(element);
							if (maxNumberOfVals > 0 && retList.size() >= maxNumberOfVals) return retList;
						}
					}
				// closed end
				} else {
					for (SkipGraphElement element :
							node.getElementTable()) {
						if (capacity.equals(element.getCapacity()) &&
								element.getValue().compareTo(valueStart) >= 0 &&
								element.getValue().compareTo(valueEnd) <= 0) {
							retList.add(element);
							if (maxNumberOfVals > 0 && retList.size() >= maxNumberOfVals) return retList;
						}
					}
				}
			}

			// give search to next node if required
			if (node.getTableRangeEnd() != null) {
				//System.out.println("end: " + node.getTableRangeEnd());
				if (valueEnd == null || node.isAboveElementTablesMaximum(valueEnd)) {
					if (maxNumberOfVals > 0) {
						maxNumberOfVals -= retList.size();
					}
					// System.out.println("maximum " + maxNumberOfVals + " values");
					valueStart = node.getTableRangeEnd();
					retList.addAll(node.getContacts().getNext().execute(this));
				}
			}
			return retList;
		}
	}

}
