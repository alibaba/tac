package com.alibaba.tac.sdk.tangram4tac;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by longerian on 2017/11/18.
 *
 * 字段序列化后，默认输出到json里的字段名与成员变量名相同，通过该注解可提供一个别名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldNameMapper {

    String key();

}
