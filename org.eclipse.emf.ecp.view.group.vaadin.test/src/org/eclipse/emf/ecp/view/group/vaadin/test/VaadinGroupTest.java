/*******************************************************************************
 * Copyright (c) 2014 Dennis Melzer and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dennis - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.ecp.view.group.vaadin.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecp.view.common.vaadin.test.VaadinDatabindingClassRunner;
import org.eclipse.emf.ecp.view.core.vaadin.ECPVaadinViewComponent;
import org.eclipse.emf.ecp.view.core.vaadin.VaadinRendererFactory;
import org.eclipse.emf.ecp.view.group.vaadin.GroupLayoutRendererVaadin;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContextFactory;
import org.eclipse.emf.ecp.view.spi.group.model.VGroup;
import org.eclipse.emf.ecp.view.spi.group.model.VGroupFactory;
import org.eclipse.emf.ecp.view.spi.model.VControl;
import org.eclipse.emf.ecp.view.spi.model.VFeaturePathDomainModelReference;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.emf.ecp.view.spi.model.VViewFactory;
import org.eclipse.emf.ecp.view.spi.renderer.NoPropertyDescriptorFoundExeption;
import org.eclipse.emf.ecp.view.spi.renderer.NoRendererFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

@RunWith(VaadinDatabindingClassRunner.class)
public class VaadinGroupTest {

	private static final String GROUP_NAME = "group";
	private static final String GROUP_NAME2 = "group2";

	/**
	 * @return a View with one Group in it
	 */
	private static VView createViewWithOneGroup() {
		final VView view = VViewFactory.eINSTANCE.createView();
		final VGroup group = createGroup();
		view.getChildren().add(group);
		return view;
	}

	/**
	 * @return A view with two groups as children
	 */
	private static VView createViewWithTwoGroups() {
		final VView view = VViewFactory.eINSTANCE.createView();
		final VGroup group = createGroup();
		view.getChildren().add(group);
		final VGroup group2 = createGroup();
		view.getChildren().add(group2);
		return view;
	}

	/**
	 * @return A View with two groups, one is the subgroup of the first one
	 */
	private static VView createViewWithTwoHierachicalGroups() {
		final VView view = VViewFactory.eINSTANCE.createView();
		final VGroup group = createGroup();
		view.getChildren().add(group);
		final VGroup subGroup = createGroup();
		group.getChildren().add(subGroup);
		return view;
	}

	private static VView createViewWithTwoGroupsWithTwoControls() {
		final VView view = VViewFactory.eINSTANCE.createView();
		final VGroup group1 = createGroup();
		view.getChildren().add(group1);
		group1.getChildren().add(createControl());
		group1.getChildren().add(createControl());
		final VGroup group2 = createGroup();
		view.getChildren().add(group2);
		group2.getChildren().add(createControl());
		group2.getChildren().add(createControl());
		return view;
	}

	/**
	 * @return
	 */
	private static VGroup createGroup() {
		return VGroupFactory.eINSTANCE.createGroup();
	}

	private static VControl createControl() {
		final VControl control = VViewFactory.eINSTANCE.createControl();
		final VFeaturePathDomainModelReference modelReference = VViewFactory.eINSTANCE
				.createFeaturePathDomainModelReference();
		modelReference.setDomainModelEFeature(EcorePackage.eINSTANCE.getEClassifier_InstanceClassName());
		control.setDomainModelReference(modelReference);
		return control;
	}

	@Test
	public void testOneGroupinView() throws NoRendererFoundException, NoPropertyDescriptorFoundExeption {
		final VView view = createViewWithOneGroup();
		final org.eclipse.emf.ecp.view.spi.group.model.VGroup group = (org.eclipse.emf.ecp.view.spi.group.model.VGroup) view
				.getChildren().get(0);
		group.setName(GROUP_NAME);

		// setup ui
		AbstractOrderedLayout viewLayout = getContentLyout(view);
		final Component renderedControl = viewLayout.getComponent(0);
		assertGroupControl(renderedControl, GROUP_NAME);
		// assertEquals("Rendered Control and control returned by renderer are not the same", control, renderedControl);
	}

	private void assertGroupControl(final Component renderedControl, String groupName) {
		assertEquals("Rendered Control is not a Group", GroupLayoutRendererVaadin.GROUP_STYLE_NAME,
				renderedControl.getStyleName());
		assertEquals("Rendered Group does not have correct name", groupName, renderedControl.getCaption());
	}

	private AbstractOrderedLayout getContentLyout(VView view) {
		final ViewModelContext viewContext = ViewModelContextFactory.INSTANCE.createViewModelContext(view,
				VViewFactory.eINSTANCE.createView());
		final ECPVaadinViewComponent control = (ECPVaadinViewComponent) VaadinRendererFactory.INSTANCE.render(view,
				viewContext);
		return (AbstractOrderedLayout) control.getContent();
	}

	private AbstractOrderedLayout getContentLayout(VView view, EObject eObject) {
		final ViewModelContext viewContext = ViewModelContextFactory.INSTANCE.createViewModelContext(view, eObject);
		final ECPVaadinViewComponent control = (ECPVaadinViewComponent) VaadinRendererFactory.INSTANCE.render(view,
				viewContext);
		return (AbstractOrderedLayout) control.getContent();
	}

	@Test
	public void testTwoGroupsinView() throws NoRendererFoundException, NoPropertyDescriptorFoundExeption {

		// setup model
		final VView view = createViewWithTwoGroups();
		final org.eclipse.emf.ecp.view.spi.group.model.VGroup group = (org.eclipse.emf.ecp.view.spi.group.model.VGroup) view
				.getChildren().get(0);
		group.setName(GROUP_NAME);

		final org.eclipse.emf.ecp.view.spi.group.model.VGroup group2 = (org.eclipse.emf.ecp.view.spi.group.model.VGroup) view
				.getChildren().get(1);
		group2.setName(GROUP_NAME2);

		// setup ui
		AbstractOrderedLayout viewLayout = getContentLyout(view);
		final Component renderedControl1 = viewLayout.getComponent(0);
		final Component renderedControl2 = viewLayout.getComponent(1);
		assertGroupControl(renderedControl1, GROUP_NAME);
		// assertEquals("Rendered Control and control returned by renderer are not the same", control, viewComposite);

		assertGroupControl(renderedControl2, GROUP_NAME2);
		// assertEquals("Rendered Control and control returned by renderer are not the same", control, viewComposite);
	}

	@Test
	public void testTwoGroupsHierachicalinView() throws NoRendererFoundException, NoPropertyDescriptorFoundExeption {

		final VView view = createViewWithTwoHierachicalGroups();
		final org.eclipse.emf.ecp.view.spi.group.model.VGroup group = (org.eclipse.emf.ecp.view.spi.group.model.VGroup) view
				.getChildren().get(0);
		final org.eclipse.emf.ecp.view.spi.group.model.VGroup subGroup = (org.eclipse.emf.ecp.view.spi.group.model.VGroup) group
				.getChildren().get(0);
		group.setName(GROUP_NAME);

		subGroup.setName(GROUP_NAME2);

		// setup ui
		AbstractOrderedLayout viewLayout = getContentLyout(view);
		final AbstractOrderedLayout renderedControl1 = (AbstractOrderedLayout) viewLayout.getComponent(0);
		final Component renderedControl2 = renderedControl1.getComponent(0);
		assertGroupControl(renderedControl1, GROUP_NAME);
		// assertEquals("Rendered Control and control returned by renderer are not the same", control, viewComposite);
		assertGroupControl(renderedControl2, GROUP_NAME2);
	}

	@Test
	public void testTwoGroupsWithTwoControlsInView() throws NoRendererFoundException, NoPropertyDescriptorFoundExeption {
		final VView view = createViewWithTwoGroupsWithTwoControls();
		final org.eclipse.emf.ecp.view.spi.group.model.VGroup group1 = (org.eclipse.emf.ecp.view.spi.group.model.VGroup) view
				.getChildren().get(0);
		final org.eclipse.emf.ecp.view.spi.group.model.VGroup group2 = (org.eclipse.emf.ecp.view.spi.group.model.VGroup) view
				.getChildren().get(1);
		group1.setName(GROUP_NAME);

		group2.setName(GROUP_NAME2);

		// setup ui
		AbstractOrderedLayout viewLayout = getContentLayout(view, EcoreFactory.eINSTANCE.createEClass());
		final AbstractOrderedLayout renderedControl1 = (AbstractOrderedLayout) viewLayout.getComponent(0);
		final AbstractOrderedLayout renderedControl2 = (AbstractOrderedLayout) viewLayout.getComponent(1);
		assertGroupControl(renderedControl1, GROUP_NAME);
		// assertEquals("Rendered Control and control returned by renderer are not the same", control, viewComposite);

		// assertEquals("Rendered Control and control returned by renderer are not the same", control, viewComposite);
		assertTrue(renderedControl1.getComponent(0) instanceof TextField);
		assertTrue(renderedControl1.getComponent(1) instanceof TextField);

		assertGroupControl(renderedControl2, GROUP_NAME2);

		assertTrue(renderedControl2.getComponent(0) instanceof TextField);
		assertTrue(renderedControl2.getComponent(1) instanceof TextField);

	}

	@Test
	public void testEmptyGroup() throws NoRendererFoundException, NoPropertyDescriptorFoundExeption {

		// setup model
		final VView view = createViewWithOneGroup();

		// setup ui
		try {
			AbstractOrderedLayout viewLayout = getContentLayout(view, EcoreFactory.eINSTANCE.createEClass());
			final Component renderedControl = viewLayout.getComponent(0);
			assertEquals("", renderedControl.getCaption());
		} catch (final IllegalArgumentException e) {
			fail("Renderer throws IlleaglArgument on empty group name" + e.getStackTrace());
		}
	}
}
