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

package org.eclipse.wst.wsdl.validation.internal;

import java.util.ResourceBundle;

import org.eclipse.wst.wsdl.validation.internal.exception.ValidateWSDLException;
import org.w3c.dom.Document;

/**
 * An interface for a WSDL validator. This is the interface for a top level validator 
 * component such as a WSDL 1.1 validator, WSDL 1.2 validator or WS-I Basic Profile validator.
 */
public interface IWSDLValidator
{
  /**
   * Validate the file with the given name.
   * 
   * @param domModel A DOM model of the file to be validated.
   * @param valInfo The information for the validation that is being performed.
   * @throws ValidateWSDLException
   */
  public void validate(Document domModel, ValidationInfo valInfo) throws ValidateWSDLException;

  /**
   * setResourceBundle
   * Set the ResourceBundle for this validator. Allows the use of difference 
   * ResourceBundles for different validators.
   * 
   * @param rb The resource bundle to set.
   */
  public void setResourceBundle(ResourceBundle rb);

}
