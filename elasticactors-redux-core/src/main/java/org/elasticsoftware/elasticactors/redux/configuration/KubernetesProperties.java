package org.elasticsoftware.elasticactors.redux.configuration;

public interface KubernetesProperties {

    String getNamespace();

    String getStatefulsetName();
}
