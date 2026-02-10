package org.apache.guacamole.auth.filetoken;

import com.google.inject.Inject;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.environment.Environment;
import org.apache.guacamole.properties.StringGuacamoleProperty;
import java.io.File;

public class ConfigurationService {

    @Inject
    private Environment environment;

    private static final StringGuacamoleProperty AUTH_DIR = new StringGuacamoleProperty() {
        @Override
        public String getName() { return "cvmlab.auth.dir"; }
    };

    public File getAuthDir() throws GuacamoleException {
        String dir = environment.getRequiredProperty(AUTH_DIR);
        return new File(dir);
    }
}
