/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.guacamole.auth.token.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.token.ConfigurationService;
import org.apache.guacamole.auth.token.user.UserData;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.Directory;
import org.apache.guacamole.net.auth.User;
import org.apache.guacamole.net.auth.permission.ObjectPermissionSet;
import org.apache.guacamole.net.auth.simple.SimpleDirectory;
import org.apache.guacamole.net.auth.simple.SimpleObjectPermissionSet;
import org.apache.guacamole.net.auth.simple.SimpleUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UserDataService {

    private static final Logger logger = LoggerFactory.getLogger(UserDataService.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String TOKEN_PARAMETER = "token";

    @Inject
    private ConfigurationService confService;

    @Inject
    private Provider<UserDataConnection> userDataConnectionProvider;

    /**
     * Derives a new UserData object from the data contained within the given
     * Credentials.
     */
    public UserData fromCredentials(Credentials credentials) {

        String token = credentials.getParameter(TOKEN_PARAMETER);
        if (token == null || token.isEmpty())
            return null;

        try {
            String authUrl = confService.getAuthUrl();
            String authToken = confService.getAuthToken();

            // Append token to URL
            String urlStr = authUrl + (authUrl.contains("?") ? "&" : "?") + "token=" + URLEncoder.encode(token, "UTF-8");
            
            URL url = java.net.URI.create(urlStr).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            // Allow for token to be passed as a header if provided
            if (authToken != null && !authToken.isEmpty()) {
                conn.setRequestProperty("Authorization", authToken);
            }
            
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            int status = conn.getResponseCode();
            if (status != 200) {
                logger.warn("Token validation failed with status: " + status);
                return null;
            }

            try (InputStream in = conn.getInputStream()) {
                UserData userData = mapper.readValue(in, UserData.class);
                if (userData.isExpired())
                    return null;
                return userData;
            }

        } catch (Exception e) {
            logger.error("Error validating token: {}", e.getMessage());
            logger.debug("Validation error details", e);
            return null;
        }

    }

    public Set<String> getUserIdentifiers(UserData userData) {
        return Collections.singleton(userData.getUsername());
    }

    public User getUser(UserData userData) {
        return new SimpleUser(userData.getUsername()) {
            @Override
            public ObjectPermissionSet getUserPermissions() throws GuacamoleException {
                return new SimpleObjectPermissionSet(getUserIdentifiers(userData));
            }
            @Override
            public ObjectPermissionSet getConnectionPermissions() throws GuacamoleException {
                return new SimpleObjectPermissionSet(getConnectionIdentifiers(userData));
            }
            @Override
            public ObjectPermissionSet getConnectionGroupPermissions() throws GuacamoleException {
                return new SimpleObjectPermissionSet(getConnectionGroupIdentifiers(userData));
            }
        };
    }

    public Set<String> getConnectionIdentifiers(UserData userData) {
        Map<String, UserData.Connection> connections = userData.getConnections();
        if (connections == null || userData.isExpired())
            return Collections.emptySet();
        return connections.keySet();
    }

    public Set<String> getConnectionGroupIdentifiers(UserData userData) {
        return Collections.singleton(UserContext.ROOT_CONNECTION_GROUP);
    }

    public Directory<Connection> getConnectionDirectory(UserData userData) {
        Map<String, UserData.Connection> connections = userData.getConnections();
        if (connections == null || userData.isExpired())
            return new SimpleDirectory<>();

        Map<String, Connection> directoryContents = new HashMap<>();
        for (Map.Entry<String, UserData.Connection> entry : connections.entrySet()) {
            String identifier = entry.getKey();
            UserData.Connection connection = entry.getValue();

            Connection guacConnection = userDataConnectionProvider.get().init(
                userData,
                identifier,
                connection
            );
            directoryContents.put(identifier, guacConnection);
        }

        return new SimpleDirectory<>(directoryContents);
    }

}
