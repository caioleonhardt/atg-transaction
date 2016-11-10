package com.leonhardt.transaction;

import java.lang.reflect.Method;

import javax.transaction.TransactionManager;

import com.leonhardt.transaction.names.LockUtils;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import atg.commerce.order.OrderManager;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.nucleus.GenericService;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;

public class ATGLockOrderTransactionInterceptor extends GenericService implements MethodInterceptor {

	private OrderManager orderManager;
	private ClientLockManager clientLockManager;
	private TransactionManager transactionManager;
	
	@Override
	public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
	    ATGLockOrder annotation = method.getAnnotation(ATGLockOrder.class);
	    
	    String lockId = LockUtils.lockPath(method, args);

		boolean isWriteLockDefault = LockType.WRITE.equals(annotation.type());
		
		boolean acquireLock = false;
		ClientLockManager clm = getClientLockManager();
		TransactionManager tm = getTransactionManager();
		TransactionDemarcation td = new TransactionDemarcation();
		Object result = null;

		try {
			if (isWriteLockDefault) {
				acquireLock = !clm.hasWriteLock(lockId, Thread.currentThread());
				if (acquireLock) {
					vlogDebug("acquiring write lock");
					clm.acquireWriteLock(lockId, Thread.currentThread());
				}				
			} else {
				acquireLock = !clm.hasReadLock(lockId, Thread.currentThread());
				if (acquireLock) {
					vlogDebug("acquiring read lock");
					clm.acquireReadLock(lockId, Thread.currentThread());
				}								
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
				}
			}
		} catch (DeadlockException e) {
			vlogError(e, "Exception in ClientLockManager");
		} finally {
			try {
				if (isWriteLockDefault) {
					if (acquireLock) {
						vlogDebug("releasing write lock");
						clm.releaseWriteLock(lockId, Thread.currentThread(), true);
					}					
				} else {
					if (acquireLock) {
						vlogDebug("releasing read lock");
						clm.releaseReadLock(lockId, Thread.currentThread(), true);
					}										
				}
				
			} catch (LockManagerException e) {
				vlogError(e, "LockManagerException");
			}
		}
		
		return result;
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
