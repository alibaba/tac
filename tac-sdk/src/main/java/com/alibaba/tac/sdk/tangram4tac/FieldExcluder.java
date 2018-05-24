package com.alibaba.tac.sdk.tangram4tac;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by longerian on 2017/11/18.
 *
 * 序列化的时候将排除该注解标记的字段
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldExcluder {
}
