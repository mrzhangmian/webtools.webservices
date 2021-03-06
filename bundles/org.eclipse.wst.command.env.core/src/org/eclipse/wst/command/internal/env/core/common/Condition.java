/*******************************************************************************
 * Copyright (c) 2001, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.command.internal.env.core.common;

/**
 * This interface defines a boolean condition that can be evaluated for any object.
 */
public interface Condition
{
  /**
   * This returns whether the given object passes this condition.
   */
  public boolean evaluate();
}
