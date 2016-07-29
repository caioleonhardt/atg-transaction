package com.leonhardt.transaction;

import java.lang.reflect.Method;

import javax.transaction.TransactionManager;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;

public class ATGTransactionInterceptor extends GenericService implements MethodInterceptor {
	
	private TransactionManager transactionManager;

	@Override
	public Object intercept(Object object, Method method, Object[] args,
			MethodProxy methodProxy) throws Throwable {

		vlogDebug("before transaction");

		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation();
		boolean shouldRollback = false;
		Object response = null;
		
		try {
			if (tm != null) {
				vlogDebug("starting transaction");
				td.begin(tm, TransactionDemarcation.REQUIRED);
				
				vlogDebug("invoking method");
				response = methodProxy.invokeSuper(object, args);
			}
			
			return response;
		} catch (Throwable e) {
			shouldRollback = true;
			vlogDebug("rollback transaction");
			throw e;
		} finally {
			try {
				if (tm != null) {
					vlogDebug("fishing transaction");
					td.end(shouldRollback);
				}
			} catch (TransactionDemarcationException tde) {
				if (isLoggingError()) {
					logError(tde);
				}
				throw tde;
			}
		}
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
