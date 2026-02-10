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

import com.google.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.GuacamoleSecurityException;
import org.apache.guacamole.auth.token.connection.ConnectionService;
import org.apache.guacamole.net.GuacamoleTunnel;
import org.apache.guacamole.net.auth.Connection;
import org.apache.guacamole.protocol.GuacamoleClientInformation;
import org.apache.guacamole.protocol.GuacamoleConfiguration;

/**
 * Connection implementation which automatically manages related UserData.
 */
public class UserDataConnection implements Connection {

    @Inject
    private ConnectionService connectionService;

    private String identifier;
    private UserData data;
    private UserData.Connection connection;

    public UserDataConnection init(UserData data, String identifier,
            UserData.Connection connection) {
        this.identifier = identifier;
        this.data = data;
        this.connection = connection;
        return this;
    }

    @Override
    public String getIdentifier() { return identifier; }

    @Override
    public void setIdentifier(String identifier) {
        throw new UnsupportedOperationException("UserDataConnection is immutable.");
    }

    @Override
    public String getName() { return identifier; }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("UserDataConnection is immutable.");
    }

    @Override
    public String getParentIdentifier() { return UserContext.ROOT_CONNECTION_GROUP; }

    @Override
    public void setParentIdentifier(String parentIdentifier) {
        throw new UnsupportedOperationException("UserDataConnection is immutable.");
    }

    @Override
    public GuacamoleConfiguration getConfiguration() {
        GuacamoleConfiguration config = connectionService.getConfiguration(connection);
        if (config == null) config = new GuacamoleConfiguration();
        return config;
    }

    @Override
    public void setConfiguration(GuacamoleConfiguration config) {
        throw new UnsupportedOperationException("UserDataConnection is immutable.");
    }

    @Override
    public Map<String, String> getAttributes() { return Collections.emptyMap(); }

    @Override
    public void setAttributes(Map<String, String> attributes) {
        throw new UnsupportedOperationException("UserDataConnection is immutable.");
    }

    @Override
    public Date getLastActive() { return null; }

    @Override
    public Set<String> getSharingProfileIdentifiers() throws GuacamoleException {
        return Collections.emptySet();
    }

    @Override
    public int getActiveConnections() { return 0; }

    @Override
    public GuacamoleTunnel connect(GuacamoleClientInformation info,
            Map<String, String> tokens) throws GuacamoleException {

        if (connection.isSingleUse()) {
            if (data.removeConnection(getIdentifier()) == null)
                throw new GuacamoleSecurityException("Permission denied");
        }

        return connectionService.connect(connection, info, tokens);

    }

}
