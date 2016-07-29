package com.leonhardt.transaction;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import atg.nucleus.GenericService;
import atg.nucleus.InstanceFactory;
import atg.nucleus.InstanceFactoryException;
import atg.nucleus.Nucleus;
import atg.nucleus.PropertyConfiguration;
import atg.nucleus.SubClasser;

/**
 * This class is used to create the instance from some component
 * that has the property $intanceFactory
 */
public class ATGTransactionFactory extends GenericService implements InstanceFactory {

	private List<Callback> callbacks;
	
	private static CallbackFilter filter = new CallbackFilter() {
		
		/**
		 * @return the index of {@link Callback} array to use
		 */
		@Override
		public int accept(Method method) {
			if (method.isAnnotationPresent(ATGTransaction.class)) {
				return 1;
			}

			if (method.isAnnotationPresent(ATGLockTransaction.class)) {
				return 2;
			}
			
			return 0;
		}
	};

	@Override
	public void doStartService() throws atg.nucleus.ServiceException {
		if (callbacks != null && callbacks.get(0) instanceof NoOp) {
			callbacks.add(0, NoOp.INSTANCE);			
		}
	};

	@Override
	public boolean copyState(Nucleus arg0, PropertyConfiguration arg1,
			Object arg2, Object arg3) throws InstanceFactoryException {
		return false;
	}

	@Override
	public Object createInstance(Nucleus nucleus, PropertyConfiguration prop,
			SubClasser sub, Object obj) throws InstanceFactoryException {
		
		Class<?> loaded = prop.getServiceClass();
		
		Enhancer e = new Enhancer();		
		e.setSuperclass(loaded);
		e.setCallbackFilter(filter);
		e.setCallbacks((Callback[]) getCallbacks().toArray());
		
		return e.create();
	}

	@Override
	public boolean isReloadable() {
		return false;
	}

	@Override
	public boolean isReloadable(String arg0) {
		return false;
	}

	public List<Callback> getCallbacks() {
		return callbacks;
	}

	public void setCallbacks(List<Callback> callbacks) {
		this.callbacks = callbacks;
	}
}
