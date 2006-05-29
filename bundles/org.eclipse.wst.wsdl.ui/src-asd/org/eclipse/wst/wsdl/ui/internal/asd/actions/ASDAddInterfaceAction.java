/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.wsdl.ui.internal.asd.actions;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.wst.wsdl.ui.internal.asd.ASDEditorPlugin;
import org.eclipse.wst.wsdl.ui.internal.asd.Messages;
import org.eclipse.wst.wsdl.ui.internal.asd.design.editparts.InterfaceEditPart;
import org.eclipse.wst.wsdl.ui.internal.asd.facade.IDescription;
import org.eclipse.wst.wsdl.ui.internal.asd.outline.ICategoryAdapter;

public class ASDAddInterfaceAction extends BaseSelectionAction {	
	public static String ID = "ASDAddInterfaceAction";  //$NON-NLS-1$
	
	public ASDAddInterfaceAction(IWorkbenchPart part)	{
		super(part);
		setId(ID);
		setText(Messages.getString("_UI_ACTION_ADD_PORTTYPE")); //$NON-NLS-1$
		setImageDescriptor(ASDEditorPlugin.getImageDescriptor("icons/porttype_obj.gif")); //$NON-NLS-1$
	}
	
	public void run() {
		if (getSelectedObjects().size() > 0) {
			Object o = getSelectedObjects().get(0);

			if (o instanceof ICategoryAdapter) {
				o = ((ICategoryAdapter) o).getOwnerDescription();
			}
			
			if (o instanceof IDescription) {
				Command command = ((IDescription) o).getAddInterfaceCommand();
			    CommandStack stack = (CommandStack) ASDEditorPlugin.getActiveEditor().getAdapter(CommandStack.class);
			    stack.execute(command);
			    
			    if (command instanceof IASDAddCommand) {
			    	Object element = ((IASDAddCommand) command).getNewlyAddedComponent();
			    	selectAndDirectEdit(element);
			    }
			}
		}  
	}
	
	protected void doDirectEdit(EditPart ep) {
		if (ep instanceof InterfaceEditPart) {
			((InterfaceEditPart) ep).performDirectEdit(null);
		}
	}
}