package org.elasticsoftware.elasticactors.redux.cluster;

import lombok.RequiredArgsConstructor;
import org.elasticsoftware.elasticactors.redux.configuration.KubernetesProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KubernetesMonitor {

    private final KubernetesProperties kubernetesProperties;
    private final Node node;



}
