package com.library.db.factory;

import android.content.Context;

import com.library.db.I.IBatchable;
import com.library.db.impl.ContactsBatch;


/**
 * Created by chen on 2016/9/22.
 */
public class BatchFactory {
    public static final int CONTACTS = 1;
    public static final int SMS = 2;

    public static IBatchable newInstance(Context context, int type) {
        IBatchable batchable = null;
        switch (type) {
            case CONTACTS:
                batchable = new ContactsBatch(context);
                break;
            case SMS:
                // TODO: 2016/9/22
                break;
        }
        return batchable;
    }


}
