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
public @interface HbmType {
	
	/**
	 * 是否排除
	 * @return
	 */
	boolean expose() default false;
	/**
	 * 自定义的名字（默认为注解字段的字段名）
	 * 例如字段"areacode"，添加一个自定义名字：@HbmType(alias="areaCode")
	 * @return
	 */
	String alias() default "";
	/**
	 * 可映射的key
	 * 例如字段"areacode"，添加两个可映射的key：@HbmType(mapNames={"area_code","AREA_CODE"})
	 * @return
	 */
	String[] mapNames() default {};
	/**
	 * 类型转换器的className
	 * 默认会根据字段类型自动适配
	 * 必须是org.apache.commons.beanutils.Converter的子类
	 * 例如"org.apache.commons.beanutils.converters.IntegerConverter"
	 * @return
	 */
	String converter() default "";
	
	

}
