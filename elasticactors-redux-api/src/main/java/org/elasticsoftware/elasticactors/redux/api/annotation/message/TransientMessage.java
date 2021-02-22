package org.elasticsoftware.elasticactors.redux.api.annotation.message;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Message(type = "", durable = false)
public @interface TransientMessage {

    @AliasFor(attribute = "type", annotation = Message.class)
    String type();

    @AliasFor(attribute = "legacyTypes", annotation = Message.class)
    String[] legacyTypes() default {};

    @AliasFor(attribute = "timeout", annotation = Message.class)
    int timeout() default 0;
}
