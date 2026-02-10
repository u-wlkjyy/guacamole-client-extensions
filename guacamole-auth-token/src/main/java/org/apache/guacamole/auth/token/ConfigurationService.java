package org.apache.guacamole.auth.token;

import com.google.inject.Inject;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.properties.StringGuacamoleProperty;

public class ConfigurationService {

    @Inject
    private Environment environment;

    private static final StringGuacamoleProperty AUTH_URL = new StringGuacamoleProperty() {
        @Override
        public String getName() { return "cvmlab-auth-url"; }
    };

    private static final StringGuacamoleProperty AUTH_TOKEN = new StringGuacamoleProperty() {
        @Override
        public String getName() { return "cvmlab-auth-token"; }
    };

    public String getAuthUrl() throws GuacamoleException {
        return environment.getRequiredProperty(AUTH_URL);
    }

    public String getAuthToken() throws GuacamoleException {
        return environment.getRequiredProperty(AUTH_TOKEN);
    }
}
