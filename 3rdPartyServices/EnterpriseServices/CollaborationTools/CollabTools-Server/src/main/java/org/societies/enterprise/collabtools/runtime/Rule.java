/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.enterprise.collabtools.runtime;



/**
 * Rule schema. The rules are applied to the context information with the {@link Operators}
 *
 * @author Chris Lima
 *
 */
public class Rule implements Comparable<Rule> {

	private final String name;
	private final Operators operator;
	private final String ctxAttribute;
	private final String value;
	private final Integer priority;
	private final double weight;
	private final String ctxType;

	/**
	 * @param operator available in {@link Operators}
	 * @param ctxAttribute
	 * @param value Can be a string or a numeric value. For same or similar leave in blank.
	 * @param priority integer representing a the a priority. Lower values have more priority
	 * @param ctxType LongTermCtxTypes or ShortTermCtxTypes
	 */
	public Rule(String name, Operators operator, String ctxAttribute, String value, int priority, double weight, String ctxType) {
		this.name = name;
		this.operator = operator;
		this.ctxAttribute = ctxAttribute;
		this.value = value;
		this.priority = priority;
		this.weight = weight;
		this.ctxType = ctxType;
	}

	/**
	 * @return the rule name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the operator
	 */
	public Operators getOperator() {
		return operator;
	}

	/**
	 * @return the ctxAttribute
	 */
	public String getCtxAttribute() {
		return ctxAttribute;
	}

	/**
	 * @return Can be a string or a numeric value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return LongTermCtxTypes or ShortTermCtxTypes
	 */
	public String getCtxType() {
		return ctxType;
	}

	/**
	 * @return The priority of the rule.  Used to determine which order the rules should be run.
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * @return The priority of the rule.  Used to determine which order the rules should be run.
	 */
	public double getWeight() {
		return weight;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Rule o) {
		return o.priority.compareTo(this.priority);
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ctxAttribute == null) ? 0 : ctxAttribute.hashCode());
		result = prime * result + ((ctxType == null) ? 0 : ctxType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result
				+ ((priority == null) ? 0 : priority.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		long temp;
		temp = Double.doubleToLongBits(weight);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rule other = (Rule) obj;
		if (ctxAttribute == null) {
			if (other.ctxAttribute != null)
				return false;
		} else if (!ctxAttribute.equals(other.ctxAttribute))
			return false;
		if (ctxType == null) {
			if (other.ctxType != null)
				return false;
		} else if (!ctxType.equals(other.ctxType))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (operator != other.operator)
			return false;
		if (priority == null) {
			if (other.priority != null)
				return false;
		} else if (!priority.equals(other.priority))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (Double.doubleToLongBits(weight) != Double
				.doubleToLongBits(other.weight))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Rule [name=" + name + ", operator=" + operator
				+ ", ctxAttribute=" + ctxAttribute + ", value=" + value
				+ ", priority=" + priority + ", weight=" + weight
				+ ", ctxType=" + ctxType + "]";
	}

}
