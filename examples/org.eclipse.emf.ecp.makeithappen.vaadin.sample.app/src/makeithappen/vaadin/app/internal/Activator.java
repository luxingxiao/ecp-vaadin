package makeithappen.vaadin.app.internal;

import makeithappen.vaadin.app.internal.servlet.VaadinResourceProvider;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator
 * 
 * @author Dennis Melzer
 *
 */
public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(VaadinResourceProvider.class, new VaadinResourceProvider(), null);

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
