/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.ws.internal.context;

public interface UDDIPreferenceContext 
{
  /*
   * This constant String is used to lookup the column delimiter for user-defined UDDI
   * Category data.
   */
  public static final String PREFERENCE_UDDI_CAT_DATA_COLUMN_DELIMITER = "uddiCatDataColumnDelimiter";
  
  /*
   * This constant String is used to lookup the string delimiter for user-defined UDDI
   * Category data.
   */
  public static final String PREFERENCE_UDDI_CAT_DATA_STRING_DELIMITER = "uddiCatDataStringDelimiter";
  
  public void setUddiCatDataColumnDelimiter(String delimiter);
  public String getUddiCatDataColumnDelimiter();
  public void setUddiCatDataStringDelimiter(String delimiter);
  public String getUddiCatDataStringDelimiter();
}