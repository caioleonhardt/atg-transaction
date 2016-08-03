package com.leonhardt.transaction;

import java.lang.reflect.Method;

import javax.transaction.TransactionManager;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;

public class ATGLockTransactionInterceptor extends GenericService implements MethodInterceptor {

	private OrderManager orderManager;
	private ClientLockManager clientLockManager;
	private TransactionManager transactionManager;
	
	@Override
	public Object intercept(Object object, Method method, Object[] args,
			MethodProxy methodProxy) throws Throwable {

		boolean acquireLock = false;
		ClientLockManager clm = getClientLockManager();
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation();
		Object result = null;

		// get order to synchronize
		Order order = null;
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof Order) {
				order = (Order) args[i];
				break;
			}
		}
		
		// if didn't find any order on arguments
		// will execute the method without problems
		if (order == null) {
			vlogWarning("Any order was found on parameters, the method will be invoked without lock.");
			return methodProxy.invokeSuper(object, args);
		}
	
		ATGLockTransaction annotation = method.getAnnotation(ATGLockTransaction.class);
		String lockId = LockType.PROFILE.equals(annotation.lockType()) ?  order.getProfileId() : order.getId();

		try {
			acquireLock = !clm.hasWriteLock(lockId, Thread.currentThread());
			if (acquireLock) {
				vlogDebug("acquiring write lock");
				clm.acquireWriteLock(lockId, Thread.currentThread());
			}

			boolean shouldRollback = false;
			try {
				vlogDebug("starting transaction");
				td.begin(tm, TransactionDemarcation.REQUIRED );

				vlogDebug("invoking method");
				result = methodProxy.invokeSuper(object, args);
				vlogDebug("method was executed with success");

				return result;
			} catch (Throwable e) {
				shouldRollback = true;
				vlogError(e, "Exception in execution method");
				throw e;
			} finally {
				try {
					if (tm != null) {
						vlogDebug("ending transaction");
						td.end(shouldRollback);
					}
				} catch (TransactionDemarcationException e) {
					vlogError(e, "Error ending transaction");
					throw e;
				}
			}
		} catch (DeadlockException e) {
			vlogError(e, "Exception in ClientLockManager");
			throw e;
		} finally {
			try {
				if (acquireLock) {
					vlogDebug("releasing write lock");
					clm.releaseWriteLock(lockId, Thread.currentThread(), true);
				}
			} catch (LockManagerException e) {
				vlogError(e, "LockManagerException");
				throw e;
			}
		}
	}

	public OrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public ClientLockManager getClientLockManager() {
		return clientLockManager;
	}

	public void setClientLockManager(ClientLockManager clientLockManager) {
		this.clientLockManager = clientLockManager;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
