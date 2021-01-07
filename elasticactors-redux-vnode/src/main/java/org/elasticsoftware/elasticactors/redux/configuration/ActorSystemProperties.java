package org.elasticsoftware.elasticactors.redux.configuration;

import org.springframework.lang.Nullable;

public interface ActorSystemProperties {

    String getName();

    @Nullable
    String getEnvironment();

    @Nullable
    String getDomain();

    default String getClusterName() {
        StringBuilder sb = new StringBuilder(getName());
        String environment = getEnvironment();
        String domainName = getDomain();
        if (environment != null && !environment.isEmpty()) {
            sb.append(".");
            sb.append(environment);
        }
        if (domainName != null && !domainName.isEmpty()) {
            sb.append(".");
            sb.append(domainName);
        }
        return sb.toString();
    }

}
