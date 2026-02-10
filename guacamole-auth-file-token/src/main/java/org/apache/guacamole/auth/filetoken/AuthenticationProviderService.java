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

package org.apache.guacamole.auth.filetoken;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.guacamole.GuacamoleException;
import org.apache.guacamole.auth.filetoken.user.AuthenticatedUser;
import org.apache.guacamole.auth.filetoken.user.UserContext;
import org.apache.guacamole.auth.filetoken.user.UserData;
import org.apache.guacamole.auth.filetoken.user.UserDataService;
import org.apache.guacamole.net.auth.Credentials;
import org.apache.guacamole.net.auth.credentials.CredentialsInfo;
import org.apache.guacamole.net.auth.credentials.GuacamoleInvalidCredentialsException;

/**
 * Service providing convenience functions for the FileTokenAuthenticationProvider.
 */
public class AuthenticationProviderService {

    @Inject
    private UserDataService userDataService;

    @Inject
    private Provider<AuthenticatedUser> authenticatedUserProvider;

    @Inject
    private Provider<UserContext> userContextProvider;

    public AuthenticatedUser authenticateUser(Credentials credentials)
            throws GuacamoleException {

        UserData userData = userDataService.fromCredentials(credentials);
        if (userData == null)
            throw new GuacamoleInvalidCredentialsException("Permission denied.", CredentialsInfo.EMPTY);

        credentials.setUsername(userData.getUsername());

        AuthenticatedUser authenticatedUser = authenticatedUserProvider.get();
        authenticatedUser.init(credentials, userData);
        return authenticatedUser;

    }

    public UserContext getUserContext(org.apache.guacamole.net.auth.AuthenticatedUser authenticatedUser)
            throws GuacamoleException {

        if (!(authenticatedUser instanceof AuthenticatedUser))
            return null;

        UserContext userContext = userContextProvider.get();
        userContext.init(((AuthenticatedUser) authenticatedUser).getUserData());
        return userContext;

    }

}
