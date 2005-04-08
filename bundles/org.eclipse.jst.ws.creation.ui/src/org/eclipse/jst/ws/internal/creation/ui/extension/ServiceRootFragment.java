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

package org.eclipse.jst.ws.internal.creation.ui.extension;

import org.eclipse.wst.command.env.core.data.DataMappingRegistry;
import org.eclipse.wst.command.env.core.fragment.SequenceFragment;
import org.eclipse.wst.command.env.core.fragment.SimpleFragment;
import org.eclipse.wst.ws.internal.extensions.AssembleServiceFragment;
import org.eclipse.wst.ws.internal.extensions.DeployServiceFragment;
import org.eclipse.wst.ws.internal.extensions.DevelopServiceFragment;
import org.eclipse.wst.ws.internal.extensions.InstallServiceFragment;
import org.eclipse.wst.ws.internal.extensions.RunServiceFragment;

public class ServiceRootFragment extends SequenceFragment 
{
  public ServiceRootFragment()
  {
    add( new SimpleFragment( new PreServiceDevelopCommand(), "" ) );
    add( new DevelopServiceFragment() );
    add( new SimpleFragment( new PreServiceAssembleCommand(), "" ) );
    add( new AssembleServiceFragment() );
    add( new SimpleFragment( new PreServiceDeployCommand(), "" ) );
    add( new DeployServiceFragment() );
    add( new SimpleFragment( new PreServiceInstallCommand(), "" ) );
    add( new InstallServiceFragment() );
    add( new SimpleFragment( new PreServiceRunCommand(), "" ) );
    add( new RunServiceFragment() );
  }

  public void registerDataMappings(DataMappingRegistry registry) 
  {
  	registry.addMapping( PreServiceDevelopCommand.class, "WebService", DevelopServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Environment", DevelopServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Context", DevelopServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Selection", DevelopServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Module", DevelopServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Ear", DevelopServiceFragment.class );
	
  	registry.addMapping( PreServiceDevelopCommand.class, "WebService", AssembleServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Environment", AssembleServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Context", AssembleServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Selection", AssembleServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Module", AssembleServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Ear", AssembleServiceFragment.class );
	
  	registry.addMapping( PreServiceDevelopCommand.class, "WebService", DeployServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Environment", DeployServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Context", DeployServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Selection", DeployServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Module", DeployServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Ear", DeployServiceFragment.class );
	
  	registry.addMapping( PreServiceDevelopCommand.class, "WebService", InstallServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Environment", InstallServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Context", InstallServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Selection", InstallServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Module", InstallServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Ear", InstallServiceFragment.class );
	
  	registry.addMapping( PreServiceDevelopCommand.class, "WebService", RunServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Environment", RunServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Context", RunServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Selection", RunServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Module", RunServiceFragment.class );  
  	registry.addMapping( PreServiceDevelopCommand.class, "Ear", RunServiceFragment.class );
	
  }
}
