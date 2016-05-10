package com.leonhardt.transaction;

import java.lang.reflect.Method;

import javax.transaction.TransactionManager;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ATGTransactionInterceptor extends GenericService implements MethodInterceptor {

	private TransactionManager transactionManager;
	
	public ATGTransactionInterceptor() {
		super();
	}
	
	@Override
	public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		
		Object response = null;
		
		if (method.isAnnotationPresent(ATGTransaction.class)) {
			vlogDebug("inside transaction");
			
			TransactionManager tm = getTransactionManager();
			TransactionDemarcation td = new TransactionDemarcation();
			boolean shouldRollback = false;
			
			try {
				if (tm != null) {
					td.begin(tm, TransactionDemarcation.REQUIRED);
					vlogDebug("init transaction");
					response = methodProxy.invokeSuper(object, args);				
				}
			} catch (Throwable e) {
				shouldRollback = true;
				vlogDebug("rollback transaction");
				throw e;
			} finally {
				try {
					if (tm != null) {
						td.end(shouldRollback);
						vlogDebug("fishing transaction");
					}
				} catch (TransactionDemarcationException tde) {
					if (isLoggingError()) {
						logError(tde);
					}
				}
			}
			vlogDebug("outside transaction");
		}
		else {
			response = methodProxy.invokeSuper(object, args);
		}
		
		return response;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
