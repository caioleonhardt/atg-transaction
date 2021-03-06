package com.leonhardt.transaction;

import java.lang.reflect.Method;

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

	private ATGTransactionInterceptor transactionCallback;
	private ATGLockTransactionInterceptor lockTransactionCallback;
	private ATGLockOrderTransactionInterceptor lockOrderTransactionCallback;
	
	private static CallbackFilter filter = new CallbackFilter() {
		
		/**
		 * @return the index of {@link Callback} array to use
		 */
		@Override
		public int accept(Method method) {
			if (method.isAnnotationPresent(ATGTransaction.class)) {
				return 1;
			}

			if (method.isAnnotationPresent(ATGLock.class)) {
				return 2;
			}

			if (method.isAnnotationPresent(ATGLockOrder.class)) {
				return 3;
			}
			
			return 0;
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
		
		Callback callbacks[] = new Callback[4];
		callbacks[0] = NoOp.INSTANCE;
		callbacks[1] = getTransactionCallback();
		callbacks[2] = getLockTransactionCallback();
		callbacks[3] = getLockOrderTransactionCallback();
		
		e.setCallbacks(callbacks);
		
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

	public ATGLockTransactionInterceptor getLockTransactionCallback() {
	    return lockTransactionCallback;
    }

	public void setLockTransactionCallback(ATGLockTransactionInterceptor lockTransactionCallback) {
	    this.lockTransactionCallback = lockTransactionCallback;
    }

	public ATGLockOrderTransactionInterceptor getLockOrderTransactionCallback() {
	    return lockOrderTransactionCallback;
    }

	public void setLockOrderTransactionCallback(
            ATGLockOrderTransactionInterceptor lockOrderTransactionCallback) {
	    this.lockOrderTransactionCallback = lockOrderTransactionCallback;
    }

	public ATGTransactionInterceptor getTransactionCallback() {
	    return transactionCallback;
    }

	public void setTransactionCallback(ATGTransactionInterceptor transactionCallback) {
	    this.transactionCallback = transactionCallback;
    }

}
