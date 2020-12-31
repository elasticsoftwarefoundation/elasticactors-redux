package org.elasticsoftware.elasticactors.redux.configuration;

import org.springframework.lang.Nullable;

public interface ActorSystemProperties {

    String getName();

    int getShards();

    @Nullable
    String getEnvironment();

    @Nullable
    String getDomain();

    default String getClusterName() {
        String fullName = getName().trim();
        String environment = getEnvironment();
        if (environment != null) {
            environment = environment.trim();
        }
        String domainName = getDomain();
        if (domainName != null) {
            domainName = domainName.trim();
        }
        if (environment != null && !environment.isEmpty()) {
            fullName += "." + environment;
        }
        if (domainName != null && !domainName.isEmpty()) {
            fullName += "." + domainName;
        }
        return fullName;
    }

}
