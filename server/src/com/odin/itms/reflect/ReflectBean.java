package com.odin.itms.reflect;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

@HbmBean
public abstract class ReflectBean  implements java.io.Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7161828347828803800L;

	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
