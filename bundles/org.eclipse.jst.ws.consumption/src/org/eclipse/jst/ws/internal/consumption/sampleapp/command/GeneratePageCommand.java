/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jst.ws.internal.consumption.sampleapp.command;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.jst.ws.internal.consumption.codegen.Generator;
import org.eclipse.wst.command.env.common.FileResourceUtils;
import org.eclipse.wst.command.env.core.SimpleCommand;
import org.eclipse.wst.command.env.core.common.Environment;
import org.eclipse.wst.command.env.core.common.MessageUtils;
import org.eclipse.wst.command.env.core.common.SimpleStatus;
import org.eclipse.wst.command.env.core.common.Status;
import org.eclipse.wst.command.env.core.context.ResourceContext;
import org.eclipse.wst.ws.internal.datamodel.Element;
import org.eclipse.wst.ws.internal.datamodel.Model;


/**
 * MofToBeanModelCommand
 * Creation date: (4/10/2001 12:41:48 PM)
 * @author: Gilbert Andrews
 */
public class GeneratePageCommand extends SimpleCommand {
		
private String LABEL = "GeneratePageCommand";
private String DESCRIPTION = "Generate code based on the model";
private MessageUtils msgUtils_;

private Model model_;
private Generator fGenerator;
private IFile fIFile;
private ResourceContext resourceContext_;
private StringBuffer fStringBuffer;

private Element rootElement_;
/**
 * Build constructor comment.
 */
public GeneratePageCommand()
{
	String pluginId = "org.eclipse.jst.ws.consumption";
	msgUtils_ = new MessageUtils(pluginId + ".plugin", this);
	setDescription(DESCRIPTION);
	setName(LABEL);  		
}

/**
* Constructor
* This command will generate code from a Model
* @param model The model to be traversed
* @param generator The code generator to be used
* @param resource the resource to place the finished product
*/
public GeneratePageCommand(ResourceContext context, Model model, Generator generator, IFile file)
{
  String pluginId = "org.eclipse.jst.ws.consumption";
  msgUtils_ = new MessageUtils(pluginId + ".plugin", this);
  setDescription(DESCRIPTION);
  setName(LABEL);	
  
  model_ = model;
  fGenerator = generator;
  fIFile = file;
  resourceContext_ = context;
}

public Model getDataModel()
{
  return model_;
}

/**
 *
 */
public Status execute(Environment env)
{
  Status status = new SimpleStatus( "" );
  try {
    fGenerator.visit(model_.getRootElement());
    fStringBuffer = fGenerator.getStringBuffer();
    String tempString = fStringBuffer.toString();
    OutputStream fileResource = FileResourceUtils.newFileOutputStream(resourceContext_, fIFile.getFullPath(), env.getProgressMonitor(), env.getStatusHandler());
    //PrintStream ps = new PrintStream(fileResource);
    //ps.print(tempString);
    OutputStreamWriter osw = new OutputStreamWriter(fileResource,"UTF-8");
    osw.write(tempString,0,fStringBuffer.length());  
    osw.close();
    fileResource.close();
    return status;
  } catch (IOException ioexc) {
  	status = new SimpleStatus("", ioexc.getMessage(), Status.ERROR);
  	return status;
  }
}

public void setRootElement(Element rootElement)
{
  rootElement_ = rootElement;
}

}

