/*
 * Copyright 2017 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package com.adobe.workspace;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(service = Servlet.class)
@SlingServletPaths("/bin/aio/workspace")
public class WorkspaceConfigHC extends SlingSafeMethodsServlet {

  private static final long serialVersionUID = 1L;

  @Reference
  private WorkspaceConfig workspaceConfig;

  @Override
  protected void doGet(final SlingHttpServletRequest req,
      final SlingHttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("text/html");
    StringBuffer sb = new StringBuffer("<html><body><pre>");
    infoBold(sb, "Adobe I/O Workspace Checks");

    checkWorkspaceConfig(sb);

    sb.append("</pre></body></html>");

    resp.getWriter().write(sb.toString());
    resp.flushBuffer();
  }

  private void checkWorkspaceConfig(StringBuffer sb) {
    try {
      infoBold(sb, "Adobe I/O Events' Workspace Configuration checks");
      info(sb, "OSGI Config: " + workspaceConfig.getWorkspaceConfig());
      info(sb, "I/O Events Workspace: " + workspaceConfig.getWorkspace());
    } catch (Exception e) {
      error(sb, e);
    }
  }


  private static void info(StringBuffer sb, String line) {
    sb.append(String.format("%n " + line));
  }

  private static void error(StringBuffer sb, Throwable error) {
    String messsage =
        StringUtils.isEmpty(error.getMessage()) ? error.toString() : error.getMessage();
    sb.append(String.format("%n <b style=\"color:red;\">ERROR: " + messsage + "</b>"));
  }

  private static void error(StringBuffer sb, String error) {
    sb.append(String.format("%n <b style=\"color:red;\">ERROR: " + error + "</b>"));
  }

  private static void warn(StringBuffer sb, String line) {
    sb.append(String.format("%n <b style=\"color:orange;\">WARN: " + line + "</b>"));
  }

  private static void assertion(StringBuffer sb, Boolean assertion) {
    if (assertion != null && assertion) {
      sb.append(String.format("<b style=\"color:green;\">" + assertion + "</b>"));
    } else {
      sb.append(String.format("<b style=\"color:orange;\">" + assertion + "</b>"));
    }
  }

  private static void infoBold(StringBuffer sb, String title) {
    info(sb, "");
    info(sb, "<b>" + title + "</b>");
  }

}

