/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 * 20070327   172339 kathy@ca.ibm.com - Kathy Chan
 *******************************************************************************/
package org.eclipse.wst.ws.internal.ui.utils;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;

/**
 * The AdapterUtils class provides utility methods to get objects from the Platform's adapter extension.
 *
 */
public class AdapterUtils {	
	
	/**
	 * @param object Object to adapt
	 * @return The adapted object representing the file or String if an adapter is found. 
	 *         Returns null if an adapter is not found.
	 */
	public static Object getAdaptedObject (Object object) { 
		Object adaptedObject = Platform.getAdapterManager().loadAdapter(object, "org.eclipse.core.resources.IFile");
		if (adaptedObject == null) {
			adaptedObject = Platform.getAdapterManager().loadAdapter(object, "java.lang.String");
		}
		return adaptedObject;
	}
	
	/**
	   * @param object Look up an adapter mapping the object to IFile or String.
	   * @return The WSDL uri returned by the adapter or null if no adapter is found.
	   */
	  public static String getAdaptedWSDL (Object object) {
		  String wsdlURI = null;
		  Object adaptedObject = AdapterUtils.getAdaptedObject(object);
		  if ( adaptedObject != null) {
			  if (adaptedObject instanceof IFile)
			  {
				  URI uri = ((IFile)adaptedObject).getLocationURI();
				  if (uri != null) {
					  wsdlURI = uri.toString();
				  }
				  
			  } else if (adaptedObject instanceof String) {
				  wsdlURI = (String) adaptedObject;
			  }
		  }
		  return wsdlURI;
	  }
	
}
