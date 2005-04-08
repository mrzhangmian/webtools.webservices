/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.ws.internal.extensions;

import java.util.Vector;

import org.eclipse.wst.command.internal.provisional.ICommandFactory;
import org.eclipse.wst.command.internal.provisional.SimpleCommandFactory;

public class InstallClientFragment extends AbstractClientFragment 
{
  public InstallClientFragment()
  {
  }
  
  protected InstallClientFragment( InstallClientFragment fragment )
  {
	super( fragment );  
  }
  
  public Object clone() 
  {
	return new InstallClientFragment();
  }

  public ICommandFactory getICommandFactory() 
  {
	ICommandFactory factory = null;
	
	if( webServiceClient_ == null )
	{
	  factory = new SimpleCommandFactory( new Vector() );
	}
	else
	{
	  factory = webServiceClient_.install( environment_, context_, selection_, module_, ear_ );	
	}
	
	return factory;
  }
}
