package se.liu.ida.tdp024.account.data.impl.db.util;

import se.liu.ida.tdp024.account.data.api.util.StorageFacade;

public class StorageFacadeDB implements StorageFacade{

    @Override
    public void emptyStorage() {
        EMF.close();
    }
    
}
