ATG Transaction
----------
Transactions in ATG 10.0.3 are ugly and use repetitive code. The solution was create a proxy with cglib, it make the method annotable and transparent, see bellow.

Add the factory for your component:

	$class=foo.bar.YourComponent
	$instanceFactory=/com/leonhardt/transaction/ATGTransactionFactory


Now, if you want use transactions or lock:

```java
@ATGTransaction
public void executeTransaction() {
	System.out.println("executeTransaction");
}

@ATGLock
public void executeLockTransaction(@Lock RepositoryItem item, String id) {
	System.out.println("executeLockTransaction");
}

@ATGLock
public void execute(@Lock String string) {
    System.out.println(string);
}

@ATGLockOrder
public void executeLockOrder(@Lock Order order, String id) {
	synchronized (order) {
	    vlogInfo("the order will be locked ");
	    // TODO all the order updates
	    getOrderManager().updateOrder(order);
    }
}
```
To test, you can start the server in debug mode and see the lock table in ClientLockManager:

ATTENTION: Tested only in development enviroment!!
