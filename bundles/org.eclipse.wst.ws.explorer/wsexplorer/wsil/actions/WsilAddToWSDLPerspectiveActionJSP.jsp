<%
/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
%>
<%@ page contentType="text/html; charset=UTF-8" import="org.eclipse.wst.ws.internal.explorer.platform.constants.*,
                                                        org.eclipse.wst.ws.internal.explorer.platform.actions.ShowPerspectiveAction,
                                                        org.eclipse.wst.ws.internal.explorer.platform.wsil.constants.WsilFrameNames,
                                                        org.eclipse.wst.ws.internal.explorer.platform.wsil.actions.WsilAddToWSDLPerspectiveAction"%>

<jsp:include page="/wsil/scripts/wsilPanes.jsp" flush="true"/>
<jsp:useBean id="controller" class="org.eclipse.wst.ws.internal.explorer.platform.perspective.Controller" scope="session"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">
<html lang="<%=response.getLocale().getLanguage()%>">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body dir="<%=org.eclipse.wst.ws.internal.explorer.platform.util.DirUtils.getDir()%>">
<%
// Prepare the action.
WsilAddToWSDLPerspectiveAction action = new WsilAddToWSDLPerspectiveAction(controller);

// Load the parameters for the action from the servlet request.
action.populatePropertyTable(request);

// Run the action and obtain the return code (fail/success).
boolean actionResult = action.execute();

if (actionResult) {
%>
  <script language="javascript">
    perspectiveWorkArea.location = "<%=response.encodeURL(controller.getPathWithContext(ShowPerspectiveAction.getActionLink(ActionInputs.PERSPECTIVE_WSDL,false)))%>";
  </script>
<%
}
else {
%>
  <script language="javascript">
    wsilPropertiesContainer.location = "<%=response.encodeURL(controller.getPathWithContext("wsil/wsil_properties_container.jsp"))%>";
    wsilStatusContent.location = "<%=response.encodeURL(controller.getPathWithContext("wsil/wsil_status_content.jsp"))%>";
  </script>
<%
}
%>
</body>
</html>
