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

package org.eclipse.wst.wsdl.validation.internal.wsdl20;

import java.util.Hashtable;
import java.util.Map;


/**
 * A registry to hold all the WSDL 2.0 validators.
 */
public class ValidatorRegistry
{

  protected static ValidatorRegistry verInstance;

  protected Map validatorReg = new Hashtable();

  /**
   * Constructor.
   */
  protected ValidatorRegistry()
  {
  }

  /**
   * Returns the instance of this registry.
   * 
   * @return The instance of this registry.
   */
  public static ValidatorRegistry getInstance()
  {
    if (verInstance == null)
    {
      verInstance = new ValidatorRegistry();
    }
    return verInstance;
  }

  /**
   * Register this validator delegate with the given namespace.
   * 
   * @param namespace The namespace the validator is associated with.
   * @param valDelegate The validator delegate to register.
   */
  public void registerValidator(String namespace, WSDL20ValidatorDelegate valDelegate)
  {
    // allow the null namespace but make it the empty string
    if (namespace == null)
    {
      namespace = "";
    }

    // add the validator to the hashtable
    validatorReg.put(namespace, valDelegate);
  }

  /**
   * Ask for the validator associated with this namespace. If none is found
   * return null.
   * 
   * @param namespace The namespace of the validator.
   * @return The WSDL 1.1 validator for the given namespace.
   */
  public IWSDL20Validator queryValidatorRegistry(String namespace)
  {
    // if the namespace is null allow it and treat it as the empty string
    if (namespace == null)
    {
      namespace = "";
    }
    WSDL20ValidatorDelegate delegate = (WSDL20ValidatorDelegate)validatorReg.get(namespace);
    if(delegate != null)
    {  
      return delegate.getValidator();
    }
    return null;
  }

  /**
   * Convenience method that tells whether a validator for a given namespace is registered.
   * 
   * @param namespace The namespace to check.
   * @return True if there is a validator registered, false otherwise.
   */
  public boolean hasRegisteredValidator(String namespace)
  {
    if (queryValidatorRegistry(namespace) != null)
    {
      return true;
    }
    return false;
  }
}
