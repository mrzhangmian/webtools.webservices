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

import org.eclipse.wst.command.env.core.common.Environment;
import org.eclipse.wst.command.env.core.fragment.CommandFactoryFragment;
import org.eclipse.wst.ws.internal.provisional.wsrt.IContext;
import org.eclipse.wst.ws.internal.provisional.wsrt.ISelection;
import org.eclipse.wst.ws.internal.provisional.wsrt.IWebServiceClient;

public abstract class AbstractClientFragment extends CommandFactoryFragment
{
  protected IWebServiceClient webServiceClient_;
  protected Environment       environment_;
  protected IContext          context_;
  protected ISelection        selection_;
  protected String            module_;
  protected String            ear_;
    
  public AbstractClientFragment()
  {
  }
  
  protected AbstractClientFragment( AbstractClientFragment fragment )
  {
	super( fragment ); 
	
	webServiceClient_ = fragment.webServiceClient_;
	environment_      = fragment.environment_;
	context_          = fragment.context_;
	selection_        = fragment.selection_;
	module_           = fragment.module_;
	ear_              = fragment.ear_;
  }
    
  public void setWebService( IWebServiceClient webService )
  {
	webServiceClient_ = webService;  
  }

  public void setEnvironment( Environment environment )
  {
	environment_ = environment;
  }
  
  public void setContext( IContext context )
  {	  
	context_ = context;
  }
  
  public void setSelection( ISelection selection )
  {
	selection_ = selection;  
  }
  
  public void setModule( String module )
  {
	module_ = module;
  }
  
  public void setEar( String ear )
  {
	ear_ = ear;  
  }
}
