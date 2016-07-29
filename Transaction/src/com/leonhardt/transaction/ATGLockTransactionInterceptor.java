package com.leonhardt.transaction;

import java.lang.reflect.Method;

import javax.transaction.TransactionManager;

import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.servlet.ServletUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

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
	
		// if request is not null, use the profileId to create the lock
		// otherwise must be used the orderId to create the lock
		String lockId = ServletUtil.getCurrentRequest() == null ? order.getId() : order.getProfileId();

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

				synchronized (order) {
					vlogDebug("invoking method");
					result = methodProxy.invokeSuper(object, args);

					vlogDebug("updationg order");
					getOrderManager().updateOrder(order);
				}
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
