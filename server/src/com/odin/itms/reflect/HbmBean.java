package com.odin.itms.reflect;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解类能帮助HbmTransfer类进行实体映射
 * @author Leo
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD, java.lang.annotation.ElementType.TYPE})
public @interface HbmBean {
	
	/**
	 * 排除映射字段
	 * @return
	 */
	String[] exposeFields() default {};

}
