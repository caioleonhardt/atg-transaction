package com.leonhardt.transaction;

import atg.nucleus.InstanceFactory;
import atg.nucleus.InstanceFactoryException;
import atg.nucleus.Nucleus;
import atg.nucleus.PropertyConfiguration;
import atg.nucleus.SubClasser;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * This class is used to create the instance from some component
 * that has the property $intanceFactory
 */
public class ATGTransactionFactory implements InstanceFactory {

	private MethodInterceptor interceptor;

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
		e.setCallback(getInterceptor());
		
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

	public MethodInterceptor getInterceptor() {
		return interceptor;
	}

	public void setInterceptor(MethodInterceptor interceptor) {
		this.interceptor = interceptor;
	}
}
