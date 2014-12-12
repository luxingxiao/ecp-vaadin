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
package org.eclipse.emf.ecp.view.table.vaadin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecp.view.core.vaadin.AbstractControlRendererVaadin;
import org.eclipse.emf.ecp.view.core.vaadin.TableListDiffVisitor;
import org.eclipse.emf.ecp.view.core.vaadin.VaadinRendererUtil;
import org.eclipse.emf.ecp.view.core.vaadin.VaadinWidgetFactory;
import org.eclipse.emf.ecp.view.core.vaadin.converter.SelectionConverter;
import org.eclipse.emf.ecp.view.spi.context.ViewModelContext;
import org.eclipse.emf.ecp.view.spi.table.model.VTableControl;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.lunifera.runtime.web.vaadin.databinding.VaadinObservables;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Vaadin Renderer for {@link VTableControl}.
 *
 * @author Dennis Melzer
 *
 */
public class TableRendererVaadin extends AbstractControlRendererVaadin<VTableControl> {

	private static final String TABLE_BUTTON_TOOLBAR = "table-button-toolbar"; //$NON-NLS-1$
	private Setting setting;
	private Table table;

	private InternalEObject getInstanceOf(EClass clazz) {
		return InternalEObject.class.cast(clazz.getEPackage().getEFactoryInstance().create(clazz));
	}

	@Override
	protected Component render() {
		final VTableControl control = getVElement();
		final ViewModelContext viewContext = getViewModelContext();
		final Iterator<Setting> iterator = control.getDomainModelReference().getIterator();
		if (!iterator.hasNext()) {
			return null;
		}
		setting = iterator.next();

		final VerticalLayout layout = new VerticalLayout();
		table = createTable();
		layout.setData(table);

		setVisibleColumns(control, viewContext);

		bindTable(setting, table);

		if (control.isReadonly()) {
			layout.addComponent(table);
			return layout;
		}

		final HorizontalLayout horizontalLayout = createDetailEditing(control);

		layout.addComponent(horizontalLayout);
		layout.setComponentAlignment(horizontalLayout, Alignment.TOP_RIGHT);
		layout.addComponent(table);
		return layout;
	}

	private HorizontalLayout createDetailEditing(VTableControl control) {
		final HorizontalLayout horizontalLayout = new HorizontalLayout();
		addTableToolbarStyle(control, horizontalLayout);

		if (!control.isAddRemoveDisabled()) {
			createAddRemoveButton(horizontalLayout);
		}

		switch (control.getDetailEditing()) {
		case WITH_DIALOG:
			createEditButton(control, horizontalLayout);
			break;
		case WITH_PANEL:
			// TODO Master/Detail Panel
			throw new UnsupportedOperationException("WITH_PANEL is not implmented yet"); //$NON-NLS-1$
		default:
			break;
		}
		return horizontalLayout;
	}

	private void createEditButton(VTableControl control, HorizontalLayout horizontalLayout) {
		final EMFUpdateValueStrategy emfUpdateValueStrategy = new EMFUpdateValueStrategy();
		emfUpdateValueStrategy.setConverter(new SelectionConverter());
		final Button edit = VaadinWidgetFactory.createTableEditButton(table, control.getDetailView());
		edit.setEnabled(control.getDetailView() != null);
		horizontalLayout.addComponent(edit);
		bindButtonEnable(edit);
	}

	private void createAddRemoveButton(HorizontalLayout horizontalLayout) {
		final Button add = VaadinWidgetFactory.createTableAddButton(setting, table);
		horizontalLayout.addComponent(add);
		final Button remove = VaadinWidgetFactory.createTableRemoveButton(setting, table);
		horizontalLayout.addComponent(remove);
		bindButtonEnable(remove);
	}

	private void bindButtonEnable(Button button) {
		final EMFUpdateValueStrategy strategy = new EMFUpdateValueStrategy();
		strategy.setConverter(new SelectionConverter());
		final IObservableValue observeSingleSelection = VaadinObservables.observeSingleSelection(table,
			getReferenceType().getInstanceClass());
		getBindingContext().bindValue(VaadinObservables.observeEnabled(button), observeSingleSelection, null, strategy);
	}

	private void addTableToolbarStyle(VTableControl control, HorizontalLayout horizontalLayout) {
		if (hasCaption()) {
			horizontalLayout.addStyleName(TABLE_BUTTON_TOOLBAR);
		}
	}

	private void bindTable(final Setting setting, final Table table) {
		final IObservableList targetValue = VaadinObservables.observeContainerItemSetContents(table, setting
			.getEObject()
			.getClass());
		targetValue.addListChangeListener(new IListChangeListener() {

			@Override
			public void handleListChange(ListChangeEvent event) {
				event.diff.accept(new TableListDiffVisitor(table));
			}
		});

		final IObservableList modelValue = EMFProperties.list(setting.getEStructuralFeature()).observe(
			setting.getEObject());
		getBindingContext().bindList(targetValue, modelValue);
	}

	private Table createTable() {
		final Table table = new Table();
		table.setSelectable(true);
		table.setSizeFull();

		final EClass clazz = getReferenceType();
		final BeanItemContainer<Object> indexedContainer = new BeanItemContainer(clazz.getInstanceClass());
		table.setContainerDataSource(indexedContainer);
		return table;
	}

	private EClass getReferenceType() {
		return ((EReference) setting.getEStructuralFeature()).getEReferenceType();
	}

	private void setVisibleColumns(VTableControl control, ViewModelContext viewContext) {
		final List<EStructuralFeature> listFeatures = VaadinRendererUtil.getColumnFeatures(control);
		final InternalEObject tempInstance = getInstanceOf(getReferenceType());
		final List<String> visibleColumnsNames = new ArrayList<String>();
		final List<String> visibleColumnsId = new ArrayList<String>();
		for (final EStructuralFeature eStructuralFeature : listFeatures) {
			final IItemPropertyDescriptor itemPropertyDescriptor = VaadinRendererUtil.getItemPropertyDescriptor(
				tempInstance,
				eStructuralFeature);
			String displayName = eStructuralFeature.getName();
			// String tooltipText = eStructuralFeature.getName();

			if (itemPropertyDescriptor != null) {
				displayName = itemPropertyDescriptor.getDisplayName(null);
			}
			visibleColumnsNames.add(displayName);
			visibleColumnsId.add(eStructuralFeature.getName());
			final TextField converter = new TextField();
			VaadinRendererUtil.setConverterToTextField(eStructuralFeature, converter, control, viewContext);
			if (converter.getConverter() != null) {
				table.addGeneratedColumn(eStructuralFeature.getName(), new ColumnGenerator() {
					@Override
					public Object generateCell(Table source, Object itemId, Object columnId) {
						final EObject eObject = (EObject) itemId;
						return converter.getConverter().convertToPresentation(eObject.eGet(eStructuralFeature),
							String.class, Locale.getDefault());
					}
				});
			}

		}
		// TODO: FIXME 2 gleiche coloumns?
		table.setVisibleColumns(visibleColumnsId.toArray(new Object[visibleColumnsId.size()]));
		table.setColumnHeaders(visibleColumnsNames.toArray(new String[visibleColumnsNames.size()]));
	}

	private List<Object> getItems(final Setting setting) {
		return (List<Object>) setting.getEObject().eGet(setting.getEStructuralFeature());
	}

}