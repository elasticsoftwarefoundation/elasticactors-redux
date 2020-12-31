package org.elasticsoftware.elasticactors.redux.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Message {

    String type();

    String[] legacyTypes() default {};

    boolean durable() default true;

    int timeout() default 0;
}
