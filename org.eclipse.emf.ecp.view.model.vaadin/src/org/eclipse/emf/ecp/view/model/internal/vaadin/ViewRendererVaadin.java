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

package org.eclipse.emf.ecp.view.model.internal.vaadin;

import org.eclipse.emf.ecp.view.model.vaadin.ECPVaadinComponent;
import org.eclipse.emf.ecp.view.model.vaadin.AbstractVaadinRenderer;
import org.eclipse.emf.ecp.view.model.vaadin.VaadinRendererFactory;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.model.VContainedElement;
import org.eclipse.emf.ecp.view.spi.model.VView;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

public class ViewRendererVaadin extends AbstractVaadinRenderer<VView> {

	@Override
	public ECPVaadinComponent renderComponent(VView renderable, ViewModelContext viewModelContext) {
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.setSizeFull();
		for (VContainedElement composite : renderable.getChildren()) {
			Component renderResult = VaadinRendererFactory.INSTANCE.render(composite, viewModelContext);
			verticalLayout.addComponent(renderResult);

		}

		return new ECPVaadinComponent(verticalLayout);
	}

}
