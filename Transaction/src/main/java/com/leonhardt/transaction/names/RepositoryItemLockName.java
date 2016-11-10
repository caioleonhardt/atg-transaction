package com.leonhardt.transaction.names;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryUtils;

class RepositoryItemLockName implements Lockable {

	RepositoryItem objLock;
	
	RepositoryItemLockName(RepositoryItem objLock) {
		this.objLock = objLock;
	}
	
    @Override
    public String name() throws RepositoryException {
        return RepositoryUtils.getUriForRepositoryItem(objLock);
    }

}
