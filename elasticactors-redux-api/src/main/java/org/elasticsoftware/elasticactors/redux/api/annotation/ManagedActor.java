package org.elasticsoftware.elasticactors.redux.api.annotation;

import org.elasticsoftware.elasticactors.redux.api.state.InitialStateProvider;
import org.elasticsoftware.elasticactors.redux.api.state.InitialStateProvider.Default;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
@Component
public @interface ManagedActor {

    String[] id();

    @AliasFor(attribute = "value", annotation = Component.class)
    String type();

    String[] legacyTypes() default {};

    Class<?> stateClass();

    Class<? extends InitialStateProvider> initialStateProvider() default Default.class;

    boolean exclusive() default true;
}
