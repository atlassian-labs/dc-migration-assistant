/*
 * Copyright 2020 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.atlassian.migration.datacenter;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

/**
 * Creates the servlet which renders the soy template containing the frontend SPA bundle.
 */
public class TemplateServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(TemplateServlet.class);

    private final SoyTemplateRenderer templateRenderer;
    private final PermissionEnforcer permissionEnforcer;
    private final LoginUriProvider loginUriProvider;

    public TemplateServlet(final SoyTemplateRenderer templateRenderer,
                           final PermissionEnforcer permissionEnforcer,
                           final LoginUriProvider loginUriProvider) {
        this.templateRenderer = templateRenderer;
        this.permissionEnforcer = permissionEnforcer;
        this.loginUriProvider = loginUriProvider;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (permissionEnforcer.isSystemAdmin()) {
                render(response);
            } else if (permissionEnforcer.isAuthenticated()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } else {
                response.sendRedirect(loginUriProvider.getLoginUri(URI.create(request.getRequestURL().toString())).toASCIIString());
            }
        } catch (IOException exception) {
            logger.error("Unable to render template", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void render(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML);

        templateRenderer.render(
                response.getWriter(),
                "com.atlassian.migration.datacenter.dc-migration-assistant:dc-migration-assistant-templates",
                "dcmigration.init",
                Collections.emptyMap()
        );
    }
}
