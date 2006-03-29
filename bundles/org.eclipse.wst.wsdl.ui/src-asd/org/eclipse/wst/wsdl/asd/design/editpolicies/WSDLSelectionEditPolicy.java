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
package org.eclipse.wst.wsdl.asd.design.editpolicies;

import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.wst.wsdl.asd.design.editparts.IFeedbackHandler;

public class WSDLSelectionEditPolicy extends SelectionEditPolicy {
	protected void hideSelection() {
		if (getHost() instanceof IFeedbackHandler) {
				((IFeedbackHandler) getHost()).removeFeedback();
		}
	}

	protected void showSelection() {
		if (getHost() instanceof IFeedbackHandler) {
			((IFeedbackHandler) getHost()).addFeedback();
		}
	}
}