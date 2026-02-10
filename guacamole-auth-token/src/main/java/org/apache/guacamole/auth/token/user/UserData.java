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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * All data associated with a particular user.
 */
public class UserData {

    /**
     * The username of the user associated with this data.
     */
    private String username;

    /**
     * The time after which this data is no longer valid.
     */
    private Long expires;

    /**
     * All connections accessible by this user.
     */
    private ConcurrentMap<String, Connection> connections;

    /**
     * The data associated with a Guacamole connection.
     */
    public static class Connection {

        private String id;
        private String protocol;
        private String primaryConnection;
        private Map<String, String> parameters;
        private boolean singleUse = false;
        private String guacdHostname;
        private Integer guacdPort;
        private String guacdEncryption;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }

        @JsonProperty("join")
        public String getPrimaryConnection() { return primaryConnection; }

        @JsonProperty("join")
        public void setPrimaryConnection(String primaryConnection) {
            this.primaryConnection = primaryConnection;
        }

        public Map<String, String> getParameters() { return parameters; }
        public void setParameters(Map<String, String> parameters) {
            this.parameters = parameters;
        }


        public boolean isSingleUse() { return singleUse; }
        public void setSingleUse(boolean singleUse) { this.singleUse = singleUse; }

        public String getGuacdHostname() { return guacdHostname; }
        public void setGuacdHostname(String guacdHostname) { this.guacdHostname = guacdHostname; }

        public Integer getGuacdPort() { return guacdPort; }
        public void setGuacdPort(Integer guacdPort) { this.guacdPort = guacdPort; }

        public String getGuacdEncryption() { return guacdEncryption; }
        public void setGuacdEncryption(String guacdEncryption) { this.guacdEncryption = guacdEncryption; }


    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getExpires() { return expires; }
    public void setExpires(Long expires) { this.expires = expires; }

    public Map<String, Connection> getConnections() {
        return connections == null ? null : Collections.unmodifiableMap(connections);
    }

    public void setConnections(Map<String, Connection> connections) {
        this.connections = new ConcurrentHashMap<>(connections);
    }

    public Connection removeConnection(String identifier) {
        return connections.remove(identifier);
    }

    @JsonIgnore
    public boolean isExpired() {
        Long expirationTimestamp = getExpires();
        if (expirationTimestamp == null)
            return false;
        return System.currentTimeMillis() > expirationTimestamp;
    }

}
