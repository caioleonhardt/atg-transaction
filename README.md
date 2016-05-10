ATG Transaction
----------
Transactions in ATG 10.0.3 are ugly and use repetitive code. The solution was create a proxy with cglib, it make the method annotable and transparent, see bellow.

In your class:

    @ATGTransaction
	public void executeWithTransaction() {
		vlogInfo("code wrapped with transaction here!");
	}

In your component:

	$class=foo.bar.YourComponent
	$instanceFactory=/com/leonhardt/transaction/ATGTransactionFactory


To test, you can enable the loggingDebug from component:

	/com/leonhardt/transaction/ATGTransactionInterceptor

ATTENTION: Tested only in development enviroment!!