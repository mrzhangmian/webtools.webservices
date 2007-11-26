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
 * 20071106 196997   ericdp@ca.ibm.com - Eric Peters
 * 20071120 209858 ericdp@ca.ibm.com - Eric Peters, Enhancing service policy framework and UI 
 ********************************************************************************/
package org.eclipse.wst.ws.internal.service.policy.ui;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.ws.service.policy.IDescriptor;
import org.eclipse.wst.ws.service.policy.IPolicyEnumerationList;
import org.eclipse.wst.ws.service.policy.IPolicyRelationship;
import org.eclipse.wst.ws.service.policy.IPolicyState;
import org.eclipse.wst.ws.service.policy.IPolicyStateEnum;
import org.eclipse.wst.ws.service.policy.IServicePolicy;
import org.eclipse.wst.ws.service.policy.IStateEnumerationItem;
import org.eclipse.wst.ws.service.policy.ServicePolicyPlatform;
import org.eclipse.wst.ws.service.policy.ui.IPolicyOperation;
import org.eclipse.wst.ws.service.policy.ui.ServicePolicyActivatorUI;
import org.eclipse.wst.ws.service.policy.ui.ServicePolicyPlatformUI;
import org.eclipse.wst.ws.service.policy.ui.IPolicyOperation.OperationKind;
import org.osgi.framework.Bundle;

public class ServicePoliciesComposite extends Composite implements
		SelectionListener {

	private ScrolledComposite operationsScrolledComposite;
	//a scrollable composite containing operations available for the selected policies
	private Composite operationsComposite;
	private IWorkbenchHelpSystem helpSystem;
	private Composite masterComposite;
	private Composite detailsComposite;
	private Tree masterPolicyTree;
	//tertiary and higher level policy nodes in a separate tree
	private Tree detailsPolicyTree;
	private Text text_DetailsPanel_description;
	private Text text_DetailsPanel_dependencies;
	private Label label_DetailsPanel_description;
	private Label label_detailsPanel_dependancies;
	private Hashtable<String, IStatus> allErrors;
	private IStatus error;
	private IConManager iconManager = new IConManager();
	private IProject project = null;
	private SelectionListener listener;
	private ExpandableComposite excomposite;
	private ServicePolicyPlatform platform = ServicePolicyPlatform
			.getInstance();
	private ServicePolicyPlatformUI platformUI = ServicePolicyPlatformUI
			.getInstance();;

	/**
	 * Creates an expandable composite
	 * @param parent
	 * @param nColumns
	 * @return
	 */
	private ExpandableComposite createExpandableComposite(Composite parent,
			int nColumns) {
		ExpandableComposite excomposite = new ExpandableComposite(parent,
				SWT.NONE, ExpandableComposite.TWISTIE
						| ExpandableComposite.CLIENT_INDENT);
		excomposite.setExpanded(false);
		excomposite.setVisible(false);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(
				JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				true, false, nColumns, 1));
		excomposite.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				expandedStateChanged((ExpandableComposite) e.getSource());
			}
		});
		makeScrollableCompositeAware(excomposite);
		return excomposite;
	}
	
	/**
	 * @author ericdp
	 * Helper class that encapsulates data required when an action control changes state
	 *
	 */
	private class ActionControlData {
		private List<IServicePolicy> spList;
		private IPolicyOperation po;

		public ActionControlData(List<IServicePolicy> spList, IPolicyOperation po) {
			this.spList = spList;
			this.po = po;
		}

		public List<IServicePolicy> getSpList() {
			return spList;
		}

		public void setSpList(List<IServicePolicy> spList) {
			this.spList = spList;
		}

		public IPolicyOperation getPo() {
			return po;
		}

		public void setPo(IPolicyOperation po) {
			this.po = po;
		}
	}

	/**
	 * Makes the scrollable composite aware of this control, so expand/collapse
	 * of the scrollable composite will move this control down/up accordingly
	 * @param control the control to make the scrollable composite aware of
	 */
	private void makeScrollableCompositeAware(Control control) {
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(control);
		if (parentScrolledComposite != null) {
			parentScrolledComposite.adaptChild(control);
		}
	}

	private ScrolledPageContent getParentScrolledComposite(Control control) {
		Control parent = control.getParent();
		while (!(parent instanceof ScrolledPageContent) && parent != null) {
			parent = parent.getParent();
		}
		if (parent instanceof ScrolledPageContent) {
			return (ScrolledPageContent) parent;
		}
		return null;
	}

	private final void expandedStateChanged(ExpandableComposite expandable) {
		ScrolledPageContent parentScrolledComposite = getParentScrolledComposite(expandable);
		if (parentScrolledComposite != null) {
			parentScrolledComposite.reflow(true);
		}
	}

	/**
	 * @param parent
	 * @return a scrollable composite containing an expandable content
	 *
	 */
	private Composite createDetailsScrollPageContent(Composite parent) {

		int nColumns = 1;

		final ScrolledPageContent pageContent = new ScrolledPageContent(parent);

		GridLayout pageContLayout = new GridLayout();
		pageContLayout.numColumns = nColumns;
		pageContLayout.marginHeight = 0;
		pageContLayout.marginWidth = 0;

		Composite composite = pageContent.getBody();
		composite.setLayout(pageContLayout);

		excomposite = createExpandableComposite(composite, nColumns);

		Composite inner = new Composite(excomposite, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		excomposite.setClient(inner);
		//details policy tree for tertiary and higher level service policies
		detailsPolicyTree = new Tree(inner, SWT.BORDER | SWT.MULTI);
		detailsPolicyTree.addSelectionListener(this);
		GridData detailsPrefTreeGD = new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		detailsPrefTreeGD.heightHint = 50;
		detailsPolicyTree.setLayoutData(detailsPrefTreeGD);
		//make the scrollable composite aware of the tree so expand/collapse works properly
		makeScrollableCompositeAware(detailsPolicyTree);
		
		createPolicyOperationsComposite(composite);

		label_DetailsPanel_description = new Label(composite, SWT.NONE);

		label_DetailsPanel_description
				.setText(WstSPUIPluginMessages.LABEL_SERVICEPOLICIES_DESCRIPTION);
		label_DetailsPanel_description
				.setToolTipText(WstSPUIPluginMessages.TOOLTIP_PSP_DESCRIPTION);
		makeScrollableCompositeAware(label_DetailsPanel_description);
		text_DetailsPanel_description = new Text(composite, SWT.WRAP
				| SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
		GridData detailsGD = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		detailsGD.heightHint = 50;
		detailsGD.widthHint = 400;
		text_DetailsPanel_description.setLayoutData(detailsGD);
		makeScrollableCompositeAware(text_DetailsPanel_description);
		label_detailsPanel_dependancies = new Label(composite, SWT.NONE);
		makeScrollableCompositeAware(label_detailsPanel_dependancies);
		label_detailsPanel_dependancies
				.setText(WstSPUIPluginMessages.LABEL_SERVICEPOLICIES_DEPENDENCIES);
		label_detailsPanel_dependancies
				.setToolTipText(WstSPUIPluginMessages.TOOLTIP_PSP_DESCRIPTION);

		text_DetailsPanel_dependencies = new Text(composite, SWT.WRAP
				| SWT.BORDER | SWT.V_SCROLL | SWT.READ_ONLY);
		makeScrollableCompositeAware(text_DetailsPanel_dependencies);
		GridData dependenciesGD = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		dependenciesGD.heightHint = 100;
		dependenciesGD.widthHint = 400;
		text_DetailsPanel_dependencies.setLayoutData(dependenciesGD);

		return pageContent;
	}

	/**
	 * Creates the scrollable composite that will contain widgets associated with a policy operation
	 * @param parent the parent composite
	 */
	private void createPolicyOperationsComposite(Composite parent) {
		operationsScrolledComposite = new ScrolledComposite(parent,
				SWT.H_SCROLL | SWT.V_SCROLL);
		operationsScrolledComposite.setExpandHorizontal(true);
		operationsScrolledComposite.setExpandVertical(true);

		operationsScrolledComposite.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		operationsComposite = new Composite(operationsScrolledComposite,
				SWT.NONE);
		operationsScrolledComposite.setContent(operationsComposite);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		operationsComposite.setLayout(layout);
		operationsScrolledComposite.setMinSize(operationsComposite.computeSize(
				400, 100));
		makeScrollableCompositeAware(operationsScrolledComposite);
		makeScrollableCompositeAware(operationsComposite);
	}

	public ServicePoliciesComposite(Composite parent, IProject project,
			SelectionListener listener) {

		super(parent, SWT.NONE);
		this.project = project;
		this.listener = listener;
		allErrors = new Hashtable<String, IStatus>();
		helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		GridLayout parentLayout = new GridLayout();
		parentLayout.numColumns = 2;
		parentLayout.horizontalSpacing = 0;
		this.setLayout(parentLayout);
		this.setLayoutData(new GridData(GridData.FILL_BOTH));
		masterComposite = new Composite(this, SWT.NONE);

		GridLayout masterLayout = new GridLayout();
		masterLayout.numColumns = 1;
		masterLayout.horizontalSpacing = 0;
		masterComposite.setLayout(masterLayout);
		masterComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		detailsComposite = new Composite(this, SWT.NONE);
		GridLayout detailsLayout = new GridLayout();
		detailsLayout.numColumns = 1;
		detailsLayout.horizontalSpacing = 0;
		detailsComposite.setLayout(detailsLayout);
		detailsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		masterPolicyTree = new Tree(masterComposite, SWT.BORDER | SWT.MULTI);
		masterPolicyTree.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		Composite othersComposite = createDetailsScrollPageContent(detailsComposite);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
				true);
		othersComposite.setLayoutData(gridData);

		masterPolicyTree.addSelectionListener(this);

		List<IServicePolicy> policyList = platform.getRootServicePolicies(null);

		for (IServicePolicy policy : policyList) {
			addPolicy(policy, masterPolicyTree, true);
		}

	}

	/**
	 * Sets the image on ti, if associated service policy defines an icon
	 * it will will be used, otherwise default Folder/Leaf icons will be used
	 * @param ti the TreeItem to set the image for
	 * @param sp the service policy associated with ti
	 * @param iconOverlays description of which overlays should be applied 
	 */
	private void setImage(TreeItem ti, IServicePolicy sp, String[] iconOverlays) {
		Image i = (sp.getChildren().size() == 0) ? iconManager.getIconOverlay(
				iconManager.getLeafBaseUrl(), iconOverlays) : iconManager
				.getIconOverlay(iconManager.getFolderBaseUrl(), iconOverlays);
		if (sp.getDescriptor() != null) {
			String iconPathBundleID = null;
			String iconPath = null;
			iconPathBundleID = sp.getDescriptor().getIconBundleId();
			iconPath = sp.getDescriptor().getIconPath();
			if (iconPathBundleID != null && iconPath != null
					&& iconPathBundleID.length() > 0 && iconPath.length() > 0) {
				Bundle b = Platform.getBundle(iconPathBundleID);
				i = iconManager.getIconOverlay(FileLocator.find(b,
						new Path(iconPath), null).toString(), iconOverlays);
			}
		}
		ti.setImage(i);
		// image has changed, notify change listeners so tree gets updated
		ti.notifyListeners(SWT.Modify, new Event());
	}

	/**
	 * Add service policy sp and children of sp to the parent
	 * @param sp the service policy to add
	 * @param parent either a Tree (masterPolicyTree or DetailsPolicyTree), or a TreeItem
	 * to add service policy children to
	 * @param addLevel2Only false if should add tertiary and higher level
	 * child policies
	 */
	private void addPolicy(IServicePolicy sp, Widget parent,
			boolean addLevel2Only) {
		if (addLevel2Only && sp.getParentPolicy() != null
				&& sp.getParentPolicy().getParentPolicy() != null)
			// don't add tertiary and higher branches, these are added on demand
			// to a different tree
			return;
		TreeItem ti;
		if (parent instanceof TreeItem)
			ti = new TreeItem((TreeItem) parent, SWT.NONE);
		else
			ti = new TreeItem((Tree) parent, SWT.NONE);
		ti.setText(sp.getDescriptor().getLongName());
		ti.setData(sp);

		setImage(ti, sp, getIconOverlayInfo(sp, false));
		List<IServicePolicy> childrenPolicyList = sp.getChildren();
		for (IServicePolicy policy : childrenPolicyList) {
			addPolicy(policy, ti, addLevel2Only);
		}

	}

	/**
	 * Returns icon overlay information
	 * @param sp the service policy that should have some overlay images associated with it
	 * @param invalid true if the service policy is valid
	 * @return String[] containing information about what overlays to apply to the icon that
	 * is associated with sp
	 */
	private String[] getIconOverlayInfo(IServicePolicy sp, boolean invalid) {
		String[] overLays = new String[4];
			if (sp.getId().equals("id_boolean1")) { 
			}
				
		IPolicyState polState = (project == null) ? sp.getPolicyState() : sp
				.getPolicyState(project);
		IPolicyStateEnum polEnum = (project == null) ? sp.getPolicyStateEnum()
				: sp.getPolicyStateEnum(project);
		if (!polState.isMutable())
			overLays[0] = iconManager.lock;
		if ((sp.getStatus() != null && sp.getStatus().getSeverity() == IStatus.ERROR) || invalid)
			overLays[1] = iconManager.invalid;
		if ((sp.getStatus() != null && sp.getStatus().getSeverity() == IStatus.WARNING))
			overLays[2] = iconManager.warning;
		if (polEnum != null) {
			if (polEnum.getEnumId().equals(
					"org.eclipse.wst.service.policy.booleanEnum")
					&& polEnum.getCurrentItem().getId().equals(
							"org.eclipse.wst.true"))
				overLays[3] = iconManager.favorite;
		}
		return overLays;
	}

	/**
	 * Does anything necessary because the default button has been pressed.
	 */
	public void performDefaults() {
		initializeDefaults();
		TreeItem selected = null;
		IServicePolicy focusSP = null;
		// fire selection event to tree that is associated with operation UI so
		// operation UI gets updated
		if (detailsPolicyTree.isVisible()
				&& detailsPolicyTree.getSelection().length > 0) {
			detailsPolicyTree.notifyListeners(SWT.Selection, new Event());
			selected = detailsPolicyTree.getSelection()[0];
			focusSP = (IServicePolicy) selected.getData();
		} else if (masterPolicyTree.getSelection().length > 0) {
			masterPolicyTree.notifyListeners(SWT.Selection, new Event());
			selected = masterPolicyTree.getSelection()[0];
			focusSP = (IServicePolicy) selected.getData();
		}

		error = validateAllPolicies(focusSP);

	}

	/**
	 * Initializes states of the controls using default values in the preference
	 * store.
	 */
	private void initializeDefaults() {
		if (project == null)
			platform.restoreDefaults();
		else
			platform.restoreDefaults(project);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == masterPolicyTree || e.getSource() == detailsPolicyTree) {
			TreeItem[] selectedItems;
			if (e.getSource() == masterPolicyTree)
				selectedItems = masterPolicyTree.getSelection();
			else
				selectedItems = detailsPolicyTree.getSelection();
			//possible that an event is fired and no nodes selected
			if (!(selectedItems.length > 0))
				return;
			List<IServicePolicy> sp = new Vector<IServicePolicy>();
			for (int i = 0; i < selectedItems.length; i++) {
				sp.add((IServicePolicy) selectedItems[i].getData());
			}
			//update the context sensitive help
			updateCSH(sp);
			updateDetailsPanels(sp);

			if (e.getSource() == masterPolicyTree) {
				// if selected node in master tree is 2nd level & has
				// children, populate details tree
				if (sp.get(0).getParentPolicy() != null
						&& sp.get(0).getChildren().size() > 0) {
					populateDetailsPolicyTree(sp);
				} else {
					//if expandable composite was visible, collapse and set invisible
					if (excomposite.getVisible()) {
						excomposite.setVisible(false);
						excomposite.setExpanded(false);
						expandedStateChanged(excomposite);
					}
				}
			}
			addActionButtons(sp);

		} else {
			// an action control fired a change event
			Control actionControl = (Control) e.getSource();
			updatePolicy(actionControl);
			IServicePolicy changedSP = ((ActionControlData) actionControl.getData())
					.getSpList().get(0);
			error = validateAllPolicies(changedSP);
			//inform listeners that a control has changed, as composite may be in error now
			listener.widgetSelected(e);

		}

	}

	/**
	 * Populate the details policy tree with sp and their children
	 * @param sp a list of top level details tree service policies
	 */
	private void populateDetailsPolicyTree(List<IServicePolicy> sp) {
		List<IServicePolicy> childpolicyList = sp.get(0)
				.getChildren();
		detailsPolicyTree.removeAll();
		for (IServicePolicy policy : childpolicyList) {
			addPolicy(policy, detailsPolicyTree, false);
		}
		// update details preference tree
		TreeItem[] treeItems = detailsPolicyTree.getItems();
		for (int i = 0; i < treeItems.length; i++) {
			updateValidStates(treeItems[i], true);
		}

		excomposite.setVisible(true);
		excomposite.setText(sp.get(0).getDescriptor().getLongName()
				+ " (" + WstSPUIPluginMessages.TEXT_DETAILS + ")");
		// text has changed, so need to force layout so updated
		excomposite.layout();
	}

	/**
	 * Updates the details and dependencies panels for the first service
	 * policy in spList
	 * @param spList a list of selected service policies
	 */
	private void updateDetailsPanels(List<IServicePolicy> spList) {
		String desc = (spList.get(0) == null
				|| spList.get(0).getDescriptor() == null || spList.get(0)
				.getDescriptor().getDescription() == null) ? "" : spList.get(0)
				.getDescriptor().getDescription();
		text_DetailsPanel_description.setText(desc);
		text_DetailsPanel_dependencies.setText(getDependanciesText(spList
				.get(0)));
	}

	/**
	 * Updates the context sensitive help for the composite with 
	 * that defined for the first policy in spList (or a parent of 
	 * first policy in spList policy does not define any CSH)
	 * @param spList the list of selected service policies
	 */
	private void updateCSH(List<IServicePolicy> spList) {
		String CSH_ID = (spList.get(0) == null || spList.get(0).getDescriptor() == null) ? null
				: spList.get(0).getDescriptor().getContextHelpId();
		IServicePolicy parentSP = spList.get(0);
		while ((CSH_ID == null || CSH_ID.length() == 0)) {
			parentSP = parentSP.getParentPolicy();
			if (parentSP == null)
				break;
			CSH_ID = (parentSP.getDescriptor() == null) ? null : parentSP
					.getDescriptor().getContextHelpId();
		}
		helpSystem.setHelp(this, CSH_ID);
	}

	/**
	 * Validates all Service Policies in the model
	 * 
	 * @param IServicePolicy
	 *            the focus policy, i.e. one that changed state
	 * @return IStatus null if no Service policies in error, otherwise a Service
	 *         Policy error (the focucPolicy error if it is in error).
	 *         Postcondition- the error property is updated.
	 */
	private IStatus validateAllPolicies(IServicePolicy focusPolicy) {
		// remove all errors as going to re-validate
		allErrors.clear();
		// re-validate all policies in model
		List<IServicePolicy> servicePolicyList = platform
				.getRootServicePolicies(null);
		for (IServicePolicy servicePolicyItem : servicePolicyList) {
			validatePolicy(servicePolicyItem);
		}
		// define error message
		IStatus error = (focusPolicy == null) ? null : allErrors
				.get(focusPolicy.getId());
		if (error == null)
			if (!allErrors.isEmpty())
				error = allErrors.get(allErrors.keys().nextElement());

		// update master policy tree
		TreeItem[] treeItems = masterPolicyTree.getItems();
		for (int i = 0; i < treeItems.length; i++) {
			updateValidStates(treeItems[i], false);
		}
		if (detailsPolicyTree.isVisible()) {
			// update details preference tree
			treeItems = detailsPolicyTree.getItems();
			for (int i = 0; i < treeItems.length; i++) {
				updateValidStates(treeItems[i], false);
			}

		}
		return error;
	}

	/**
	 * Updates the policy trees to reflect valid or invalid states, invalid states are 
	 * propagated up through the tree, and up to master tree if updateDetailsOnly is false
	 * @param ti the TreeItem to update, updates ti and all it's children
	 * @param updateDetailsOnly if false then propagate invalid states into
	 * master tree 
	 */
	private void updateValidStates(TreeItem ti, boolean updateDetailsOnly) {
		// assume tree item is valid for now
		IServicePolicy sp = (IServicePolicy) ti.getData();
		setValidPolicyState(ti);

		if (allErrors.containsKey(sp.getId()))
			setInvalidPolicyState(ti, updateDetailsOnly);
		TreeItem[] treeItems = ti.getItems();
		if (ti.getItems().length == 0) {
			if (sp.getChildren().size() != 0 && policyChildrenInvalid(sp))
				setInvalidPolicyState(ti, updateDetailsOnly);
		} else {
			for (int i = 0; i < treeItems.length; i++) {

				updateValidStates(treeItems[i], updateDetailsOnly);
			}
		}

	}

	/**
	 * Checks whether the sp has invalid children
	 * @param sp the service policy whose children may be invalid
	 * @return true if sp has an invalid child
	 */
	private boolean policyChildrenInvalid(IServicePolicy sp) {
		boolean toReturn = false;
		List<IServicePolicy> servicePolicyList = sp.getChildren();
		for (IServicePolicy servicePolicyItem : servicePolicyList) {
			if (allErrors.containsKey(servicePolicyItem.getId()))
				return true;
			else
				toReturn = policyChildrenInvalid(servicePolicyItem);
		}
		return toReturn;
	}

	/**
	 * Validates the Service Policies for the sp and all it's children, creating 
	 * error status objects for any unsatisfied relationships
	 * 
	 * @param sp
	 *            the service policy to validate
	 */
	private void validatePolicy(IServicePolicy sp) {
		String operationLongName;
		String operationSelectionLongName;
		String dependantPolicyShortName;
		List<IPolicyRelationship> relationShipList = sp.getRelationships();
		List<IPolicyOperation> spOperationsUsingEnumList;
		String currentValueID = null;
		if (sp.getPolicyStateEnum() != null) {
			currentValueID = (project == null) ? sp.getPolicyStateEnum()
					.getCurrentItem().getId() : sp.getPolicyStateEnum(project)
					.getCurrentItem().getId();
			spOperationsUsingEnumList = getEnumerationOperations(sp);
			for (IPolicyOperation operationItem : spOperationsUsingEnumList) {
				operationLongName = operationItem.getDescriptor().getLongName();
				for (IPolicyRelationship relationShipItem : relationShipList) {
					// policies associated with the relationship item
					List<IPolicyEnumerationList> relatedPolicies = relationShipItem
							.getRelatedPolicies();
					List<IStateEnumerationItem> spStateEnumerationList = relationShipItem
							.getPolicyEnumerationList().getEnumerationList();
					for (IStateEnumerationItem stateEnumerationItem : spStateEnumerationList) {
						if (!stateEnumerationItem.getId()
								.equals(currentValueID))
							continue;
						operationSelectionLongName = stateEnumerationItem
								.getLongName();
						for (IPolicyEnumerationList relatedPolicyEnumerationItem : relatedPolicies) {
							dependantPolicyShortName = relatedPolicyEnumerationItem
									.getPolicy().getDescriptor().getShortName();
							List<IPolicyOperation> relatedSPOperationsUsingEnumList = getEnumerationOperations(relatedPolicyEnumerationItem
									.getPolicy());
							for (IPolicyOperation relatedSPOperationsUsingEnumItem : relatedSPOperationsUsingEnumList) {
								List<IStateEnumerationItem> relatedSPStateEnumerationList = relatedPolicyEnumerationItem
										.getEnumerationList();
								List<String> validShortNames = new Vector<String>();
								for (int i = 0; i < relatedSPStateEnumerationList
										.size(); i++) {
									validShortNames
											.add(relatedSPStateEnumerationList
													.get(i).getShortName());

								}
								String currentItemShortName = (project == null) ? relatedPolicyEnumerationItem
										.getPolicy().getPolicyStateEnum()
										.getCurrentItem().getShortName()
										: relatedPolicyEnumerationItem
												.getPolicy()
												.getPolicyStateEnum(project)
												.getCurrentItem()
												.getShortName();
								if (!validShortNames
										.contains(currentItemShortName)) {
									// policy state is invalid
									IStatus error = createUnsatisfiedRelationshipError(sp,
											operationLongName,
											operationSelectionLongName,
											dependantPolicyShortName,
											relatedSPOperationsUsingEnumItem,
											relatedSPStateEnumerationList);
									allErrors.put(sp.getId(), error);

								}

							}

						}

					}
				}
			}
		}
		List<IServicePolicy> servicePolicyChildrenList = sp.getChildren();
		for (IServicePolicy servicePolicyChildrenItem : servicePolicyChildrenList) {
			validatePolicy(servicePolicyChildrenItem);

		}

	}

	/**
	 * Sets the icon for the ti denoting it as valid
	 * @param ti a valid tree item
	 */
	private void setValidPolicyState(TreeItem ti) {
		IServicePolicy sp = (IServicePolicy) ti.getData();
		setImage(ti, sp, getIconOverlayInfo(sp, false));
	}

	/**
	 * Sets the icon for ti denoting it as invalid, as well as ti's parent tree items
	 * @param ti an invalid tree item
	 * @param updateDetailsOnly true indicates do not update parent tree items only in master tree
	 */
	private void setInvalidPolicyState(TreeItem ti, boolean updateDetailsOnly) {
		IServicePolicy SP = (IServicePolicy) ti.getData();
		setImage(ti, SP, getIconOverlayInfo(SP, true));
		TreeItem parent;
		if (ti.getParent() == masterPolicyTree || updateDetailsOnly)
			parent = ti.getParentItem();
		else
			parent = (ti.getParentItem() == null && SP.getParentPolicy() != null) ? getParentInMasterTree(SP
					.getId())
					: ti.getParentItem();
		while (parent != null) {
			setImage(parent, (IServicePolicy) parent.getData(),
					getIconOverlayInfo((IServicePolicy) parent.getData(),
							true));
			if (updateDetailsOnly)
				parent = parent.getParentItem();
			else {
				parent = (parent.getParentItem() == null && ((IServicePolicy) parent
						.getData()).getParentPolicy() != null) ? getParentInMasterTree(((IServicePolicy) parent
						.getData()).getParentPolicy().getId())
						: parent.getParentItem();
			}

		}
	}

	/**
	 * Creates an error status object for the sp. The error message contains information about 
	 * an unsatisfied relationship
	 * @param sp
	 * @param operationLongName
	 * @param operationSelectionLongName
	 * @param dependantPolicyShortName
	 * @param relatedOperation
	 * @param relatedOperationAcceptableValues
	 * @return an Error status
	 */
	private IStatus createUnsatisfiedRelationshipError(IServicePolicy sp,
			String operationLongName, String operationSelectionLongName,
			String dependantPolicyShortName,
			IPolicyOperation relatedOperation,
			List<IStateEnumerationItem> relatedOperationAcceptableValues) {
		String dependantOperationShortName;
		String dependantOperationSelectionShortNameList;
		dependantOperationShortName = relatedOperation
				.getDescriptor().getShortName();
		dependantOperationSelectionShortNameList = new String();
		for (int i = 0; i < relatedOperationAcceptableValues.size(); i++) {
			IStateEnumerationItem item = relatedOperationAcceptableValues.get(i);
			if (i != 0)
				dependantOperationSelectionShortNameList += " | ";
			dependantOperationSelectionShortNameList += item.getShortName();

		}
		String[] args = { sp.getDescriptor().getLongName(), operationLongName,
				operationSelectionLongName, dependantPolicyShortName,
				dependantOperationShortName,
				dependantOperationSelectionShortNameList};
		IStatus error = new Status(Status.ERROR,
				ServicePolicyActivatorUI.PLUGIN_ID, NLS.bind(
						WstSPUIPluginMessages.SERVICEPOLICIES_DEPENDENCY_ERROR,
						args));
		return error;
	}

	/**
	 * @param parentID the id to find in the master tree
	 * @return a TreeItem with the id in the master tree
	 */
	private TreeItem getParentInMasterTree(String parentID) {
		TreeItem parent = null;
		TreeItem[] rootNodes = masterPolicyTree.getItems();
		for (int i = 0; i < rootNodes.length; i++) {
			parent = findChildNode(rootNodes[i], parentID);
			if (parent != null)
				break;
		}
		return parent;

	}

	/**
	 * Returns a TreeItem associated with a child policy of ti
	 * @param ti
	 * @param id the policy id of a child node of ti
	 * @return null if there is no child policy matching id
	 */
	private TreeItem findChildNode(TreeItem ti, String id) {
		TreeItem toReturn = null;
		if (((IServicePolicy) ti.getData()).getId().equals(id)) {
			toReturn = ti;
		} else {
			TreeItem[] childItems = ti.getItems();
			for (int i = 0; i < childItems.length; i++) {
				toReturn = findChildNode(childItems[i], id);
			}

		}
		return toReturn;

	}

	/**
	 * Returns dependancy information about sp, that is information about
	 * it's relationships (if any) with other service policies
	 * @param sp the service policy to get dependency info about
	 * @return
	 */
	private String getDependanciesText(IServicePolicy sp) {
		// the relationships defined for the policy
		List<IPolicyRelationship> relationShipList = sp.getRelationships();
		if (relationShipList == null || relationShipList.size() == 0)
			return WstSPUIPluginMessages.SERVICEPOLICIES_DEPENDENCIES_NONE;
		String toReturn = new String();

		String operationLongName;
		String operationSelectionLongName;
		String dependantPolicyShortName;
		String dependantOperationShortName;
		String dependantOperationSelectionShortNameList;
		// the operations using the same enumeration as the policy
		List<IPolicyOperation> spOperationsUsingEnumList = getEnumerationOperations(sp);
		for (IPolicyOperation operationItem : spOperationsUsingEnumList) {
			operationLongName = operationItem.getDescriptor().getLongName();
			for (IPolicyRelationship relationShipItem : relationShipList) {
				// policies associated with the relationship item
				List<IPolicyEnumerationList> relatedPolicies = relationShipItem
						.getRelatedPolicies();
				List<IStateEnumerationItem> spStateEnumerationList = relationShipItem
						.getPolicyEnumerationList().getEnumerationList();
				for (IStateEnumerationItem stateEnumerationItem : spStateEnumerationList) {
					operationSelectionLongName = stateEnumerationItem
							.getLongName();
					for (IPolicyEnumerationList relatedPolicyEnumerationItem : relatedPolicies) {
						dependantPolicyShortName = relatedPolicyEnumerationItem
								.getPolicy().getDescriptor().getShortName();
						List<IPolicyOperation> relatedSPOperationsUsingEnumList = getEnumerationOperations(relatedPolicyEnumerationItem
								.getPolicy());
						for (IPolicyOperation relatedSPOperationsUsingEnumItem : relatedSPOperationsUsingEnumList) {
							List<IStateEnumerationItem> relatedSPStateEnumerationList = relatedPolicyEnumerationItem
									.getEnumerationList();
							dependantOperationShortName = relatedSPOperationsUsingEnumItem
									.getDescriptor().getShortName();
							dependantOperationSelectionShortNameList = new String();
							for (int i = 0; i < relatedSPStateEnumerationList
									.size(); i++) {
								IStateEnumerationItem item = relatedSPStateEnumerationList
										.get(i);
								if (i != 0)
									dependantOperationSelectionShortNameList += " | ";
								dependantOperationSelectionShortNameList += item
										.getShortName();

							}
							String[] args = {
									operationLongName,
									operationSelectionLongName,
									dependantPolicyShortName,
									dependantOperationShortName,
									dependantOperationSelectionShortNameList};
							toReturn += NLS
									.bind(
											WstSPUIPluginMessages.SERVICEPOLICIES_DEPENDENCIES,
											args)
									+ "\r\n";

						}

					}

				}
			}
		}
		return toReturn;
	}

	/** Returns a list of policy operations for this service policy (note: framework is currently
	 * limited to 1 policy operation of type enumeration per policy, UI is not limited to accomodate future
	 * changes to framework if required)
	 * @param sp a service policy that has enumeration type policy operations
	 * @return a list of 0 or more enumeration type operations for the service policy
	 */
	private List<IPolicyOperation> getEnumerationOperations(IServicePolicy sp) {
		List<IPolicyOperation> toReturn = null;
		toReturn = new Vector<IPolicyOperation>();
		List<IPolicyOperation> operationList = platformUI.getAllOperations();

		for (IPolicyOperation policyOperation : operationList) {
			if (Pattern.matches(policyOperation.getPolicyIdPattern(), sp
					.getId())) {
				IPolicyStateEnum polEnum = (project == null) ? sp
						.getPolicyStateEnum() : sp.getPolicyStateEnum(project);
				if ((polEnum.getEnumId().equals(
						"org.eclipse.wst.service.policy.booleanEnum") && (policyOperation
						.getOperationKind().equals(OperationKind.selection) || policyOperation
						.getOperationKind().equals(OperationKind.iconSelection)))
						|| (policyOperation.getOperationKind().equals(
								OperationKind.enumeration) && policyOperation
								.getEnumerationId().equals(polEnum.getEnumId()))) {

					toReturn.add(policyOperation);
				}
			}

		}

		return toReturn;

	}

	/**
	 * Saves to the preference store the widget data (widget data contains associated service policy 
	 * and service policy operation)
	 * @param actionControl a widget who's value needs to be saved to the preference store
	 * 
	 */
	private void updatePolicy(Control actionControl) {

		IPolicyOperation po = ((ActionControlData) actionControl.getData()).getPo();
		List<IServicePolicy> servicePolicyList = ((ActionControlData) actionControl
				.getData()).getSpList();
		if (actionControl instanceof Button
				&& po.getOperationKind().equals(OperationKind.complex))
			updateComplexOperationPreference(po, servicePolicyList);
		else
			for (IServicePolicy servicePolicyItem : servicePolicyList) {

				updateSelectionOperationPreference(actionControl, po,
						servicePolicyItem);
			}

	}

	public boolean okToLeave() {
		return allErrors.size() == 0;

	}

	/**
	 * Saves value of enumeration, selection, and iconselection type operations
	 * to the preference store
	 * @param actionControl the action control containing the value to set for the preference
	 * @param po 
	 * @param sp
	 */
	private void updateSelectionOperationPreference(Control actionControl,
			IPolicyOperation po, IServicePolicy sp) {
		String selectedValue;
		IPolicyStateEnum polEnum = (project == null) ? sp.getPolicyStateEnum()
				: sp.getPolicyStateEnum(project);
		if (actionControl instanceof Combo) {
			selectedValue = ((Combo) actionControl).getText();
			List<IStateEnumerationItem> enumItemList = ServicePolicyPlatform
					.getInstance().getStateEnumeration(po.getEnumerationId());
			for (IStateEnumerationItem enumItem : enumItemList) {
				if (enumItem.getLongName().equals(selectedValue)) {
					polEnum.setCurrentItem(enumItem.getId());
					break;
				}
			}

		} else {
			if (((Button) actionControl).getSelection()) {
				polEnum.setCurrentItem("org.eclipse.wst.true");
			} else
				polEnum.setCurrentItem("org.eclipse.wst.false");

		}

	}

	/**
	 * Launches the complex operation for the list of service policies
	 * @param po a "complex" policy operation 
	 * @param sps a list of service policies to apply the operation to
	 */
	private void  updateComplexOperationPreference(IPolicyOperation po,
			List<IServicePolicy> sps) {
		po.launchOperation(sps);
	}

	public void dispose() {
		super.dispose();
		iconManager.dispose();
	}

	/**
	 * Create UI widgets for operations applicable to the list of service policies sp
	 * @param spList the list of service policies to create UI widgets, the UI widgets
	 * will be used to define operations/preferences to apply to the service policies in the list
	 */
	private void addActionButtons(List<IServicePolicy> spList) {
		// remove existing action controls
		Control[] toRemove = operationsComposite.getChildren();
		for (int i = 0; i < toRemove.length; i++) {
			toRemove[i].dispose();
		}
		Set<IPolicyOperation> operationList = platformUI.getSelectedOperations(
				spList, (project == null));

		for (IPolicyOperation policyOperation : operationList) {
			if (policyOperation.getOperationKind() == IPolicyOperation.OperationKind.complex) {
				addComplexOperationUI(policyOperation, spList);
			} else
				addSelectionOperationUI(policyOperation, spList);

		}
		// just removed and added some controls so force composite
		// to re-layout it's controls
		operationsScrolledComposite.setMinSize(operationsComposite.computeSize(
				SWT.DEFAULT, SWT.DEFAULT));
		operationsComposite.layout();
	}
	/**
	 * Creates UI widgets for policy operations of enumeration, selection, and 
	 * iconselection types
	 * @param po the policy operation to create a UI widget for
	 * @param sp the service policies this operation will apply to
	 */
	private void addSelectionOperationUI(IPolicyOperation po,
			List<IServicePolicy> sp) {
		IDescriptor d = po.getDescriptor();
		Control selectionControl;
		if (po.getOperationKind() == IPolicyOperation.OperationKind.enumeration) {
			Label l = new Label(operationsComposite, SWT.NONE);
			l.setText(d.getLongName() + ":");
			Combo cb = new Combo(operationsComposite, SWT.DROP_DOWN
					| SWT.READ_ONLY);
			selectionControl = cb;
			cb.addSelectionListener(this);
			List<IStateEnumerationItem> enumItemList = ServicePolicyPlatform
					.getInstance().getStateEnumeration(po.getEnumerationId());
			for (IStateEnumerationItem enumItem : enumItemList) {
				cb.add(enumItem.getLongName());
			}
			l.setEnabled(po.isEnabled(sp));
			cb.setEnabled(po.isEnabled(sp));
			if (cb.isEnabled())
				cb.setText(getEnumerationOperationCurrentSelection(sp.get(0)));
		} else {
			// a selection or icon
			Button checkBox = new Button(operationsComposite, SWT.CHECK);
			selectionControl = checkBox;
			checkBox.addSelectionListener(this);
			GridData checkBoxGD = new GridData();
			checkBoxGD.horizontalSpan = 2;
			checkBox.setLayoutData(checkBoxGD);
			checkBox.setText(d.getLongName());
			checkBox.setEnabled(po.isEnabled(sp));
			if (checkBox.isEnabled())
				checkBox
						.setSelection(getSelectionOperationCurrentSelection(sp.get(0)));

		}

		selectionControl.setData(new ActionControlData(sp, po));

	}
	/**
	 * @param sp a service policy with a selection or iconselection operation defined
	 * @return true if the operation is currently selected
	 */
	private boolean getSelectionOperationCurrentSelection(IServicePolicy sp) {
		IPolicyStateEnum polEnum = (project == null) ? sp.getPolicyStateEnum()
				: sp.getPolicyStateEnum(project);
		return (polEnum.getCurrentItem().getId().equals("org.eclipse.wst.true")) ? true
				: false;
	}

	/**
	 * @param sp a service policy with an enumeration operation defined
	 * @return the currently selected enumeration item
	 */
	private String getEnumerationOperationCurrentSelection(IServicePolicy sp) {
		IPolicyStateEnum polEnum = (project == null) ? sp.getPolicyStateEnum()
				: sp.getPolicyStateEnum(project);
		return polEnum.getCurrentItem().getLongName();
	}

	/**
	 * Creates UI widgets for policy operations of complex type
	 * @param po the policy operation to create a UI widget for
	 * @param sp the service policies this operation will apply to
	 */
	private void addComplexOperationUI(IPolicyOperation po,
			List<IServicePolicy> sp) {
		IDescriptor d = po.getDescriptor();
		Button pushButton = new Button(operationsComposite, SWT.PUSH);
		GridData pushButtonGD = new GridData();
		pushButtonGD.horizontalSpan = 2;
		pushButton.setLayoutData(pushButtonGD);
		pushButton.setText(d.getLongName());
		pushButton.addSelectionListener(this);
		pushButton.setData(new ActionControlData(sp, po));
		pushButton.setEnabled(po.isEnabled(sp));
  
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */  
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	/**
	 * @return the error that should be shown, if there are multiple validation errors,
	 * return the validation error for a service policy that just changes state (often the
	 * selected service policy)
	 */
	public IStatus getError() {
		return error;
	}

}