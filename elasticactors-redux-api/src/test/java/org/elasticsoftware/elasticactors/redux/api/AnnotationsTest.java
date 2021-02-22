package org.elasticsoftware.elasticactors.redux.api;

import org.elasticsoftware.elasticactors.redux.api.actors.TestActor;
import org.elasticsoftware.elasticactors.redux.api.actors.TestCachedActor;
import org.elasticsoftware.elasticactors.redux.api.actors.TestManagedActor;
import org.elasticsoftware.elasticactors.redux.api.actors.TestServiceActor;
import org.elasticsoftware.elasticactors.redux.api.annotation.Actor;
import org.elasticsoftware.elasticactors.redux.api.annotation.CachedActor;
import org.elasticsoftware.elasticactors.redux.api.annotation.ManagedActor;
import org.elasticsoftware.elasticactors.redux.api.annotation.ServiceActor;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.core.annotation.MergedAnnotations.from;

class AnnotationsTest {

    @Test
    void actor_shouldFindAnnotationAndRetrieveValues() {
        Actor actor = findAnnotation(TestActor.class, Actor.class);
        assertNotNull(actor);
        assertEquals("testActorType", actor.type());
        assertArrayEquals(
                new String[]{"legacyTestActorType1", "legacyTestActorType2"},
                actor.legacyTypes());
    }

    @Test
    void actor_typeShouldBeComponentName() {
        Component component = from(TestActor.class).get(Component.class).synthesize();
        assertNotNull(component);
        assertEquals("testActorType", component.value());
    }

    @Test
    void serviceActor_shouldFindAnnotationAndRetrieveValues() {
        ServiceActor actor = findAnnotation(TestServiceActor.class, ServiceActor.class);
        assertNotNull(actor);
        assertEquals("testServiceActorId", actor.id());
        assertArrayEquals(
                new String[]{"testServiceActorLegacyId0", "testServiceActorLegacyId1"},
                actor.legacyIds());
    }

    @Test
    void serviceActor_idShouldBeComponentName() {
        Component component = from(TestServiceActor.class).get(Component.class).synthesize();
        assertNotNull(component);
        assertEquals("testServiceActorId", component.value());
    }

    @Test
    void managedActor_shouldFindAnnotationAndRetrieveValues() {
        ManagedActor actor = findAnnotation(TestManagedActor.class, ManagedActor.class);
        assertNotNull(actor);
        assertArrayEquals(new String[]{"testManagedActorId"}, actor.id());
        assertEquals("testManagedActorType", actor.type());
        assertFalse(actor.exclusive());
        assertArrayEquals(
                new String[]{"legacyTestManagedActorType1", "legacyTestManagedActorType2"},
                actor.legacyTypes());
    }

    @Test
    void managedActor_typeShouldBeComponentNameShouldBeComponentName() {
        Component component = from(TestManagedActor.class).get(Component.class).synthesize();
        assertNotNull(component);
        assertEquals("testManagedActorType", component.value());
    }

    @Test
    void cachedActor_shouldFindAnnotationAndRetrieveValues() {
        CachedActor actor = findAnnotation(TestCachedActor.class, CachedActor.class);
        assertNotNull(actor);
        assertArrayEquals(new String[]{"testCachedActorId"}, actor.id());
        assertEquals("testCachedActorType", actor.type());
        assertArrayEquals(
                new String[]{"legacyTestCachedActorType1", "legacyTestCachedActorType2"},
                actor.legacyTypes());
    }

    @Test
    void cachedActor_idShouldBeComponentName() {
        Component component = from(TestCachedActor.class).get(Component.class).synthesize();
        assertNotNull(component);
        assertEquals("testCachedActorId", component.value());
    }

}
