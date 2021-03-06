/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jst.ws.internal.consumption.datamodel.beanmodel;


/**
* objects of this class represent a recognized return type
* 
*/
public class StateLessBeanType extends RecognizedReturnType
{

  // Copyright
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";


  /**
  *Constructor
  *
  */
  
  public StateLessBeanType(String name)
  {
    super(TypeFactory.STATELESS_BEAN);
  }

  
}

