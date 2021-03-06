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
package org.eclipse.emf.ecp.view.core.vaadin.internal;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.view.core.vaadin.ECPVaadinView;
import org.eclipse.emf.ecp.view.core.vaadin.ECPVaadinViewRenderer;
import org.eclipse.emf.ecp.view.core.vaadin.VaadinRendererFactory;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContextFactory;
import org.eclipse.emf.ecp.view.spi.model.VView;
import org.eclipse.emf.ecp.view.spi.provider.ViewProviderHelper;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * The implementation for the {@link ECPVaadinViewRenderer}.
 *
 * @author Dennis Melzer
 *
 */
public class ECPVaadinViewRendererImpl implements ECPVaadinViewRenderer {

	private final VaadinRendererFactory factory = new VaadinRendererFactoryImpl();

	@Override
	public ECPVaadinView render(EObject domainObject) {
		return render(domainObject, ViewProviderHelper.getView(domainObject, null));
	}

	@Override
	public ECPVaadinView render(EObject domainObject, VView viewModel) {
		final ViewModelContext viewContext = ViewModelContextFactory.INSTANCE.createViewModelContext(viewModel,
			domainObject);
		return render(viewContext);
	}

	@Override
	public ECPVaadinView render(ViewModelContext viewModelContext) {
		final Component resultSet = factory.render(viewModelContext.getViewModel(), viewModelContext);
		if (resultSet == null) {
			final Label label = new Label();
			label.setCaption("Rendering went wrong!"); //$NON-NLS-1$
			final VerticalLayout verticalLayout = new VerticalLayout(new com.vaadin.ui.Label());
			return new ECPVaadinViewImpl(verticalLayout, viewModelContext, factory);
		}
		return new ECPVaadinViewImpl(resultSet, viewModelContext, factory);
	}

}
