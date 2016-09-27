package com.library.db.impl;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

import com.library.db.I.IBatchable;
import com.library.bean.Contact;
import com.library.db.MimeType;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chen on 2016/9/22.
 */
public class ContactsBatch implements IBatchable<Contact> {
    private Context mContext;
    private ContentResolver mResolver;

    public ContactsBatch(Context mContext) {
        this.mContext = mContext;
        mResolver = mContext.getContentResolver();
    }


    @Override
    public void batchUpdate(List<Contact> people) {

    }


    @Override
    public void batchInsert(List<Contact> people) throws RemoteException, OperationApplicationException {

        try {

            //插入数据到raw_contact表需要用的uri
            Uri raw_uri = ContactsContract.RawContacts.CONTENT_URI;
            //插入数据到data表需要用的uri
            Uri data_uri = ContactsContract.Data.CONTENT_URI;
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            for (Contact contact : people) {
                //（数据是插入到raw_contact_id表）
                ContentProviderOperation cpoid = ContentProviderOperation.newInsert(raw_uri)
                        //（当前是aop[0]）
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build();
                ops.add(cpoid);
                //包装联系人姓名（数据是插入到data表）
                ContentProviderOperation cponame = ContentProviderOperation.newInsert(data_uri)
                        //这里的raw_contact_id的值取自于上 ops[0] 的返回值（当前是aop[1]）
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(MimeType.MIME_TYPE, MimeType.NAME_TYPE)
                        .withValue(ContactsContract.Data.DATA1, contact.getName())
                        .build();
                ops.add(cponame);
                //包装联系人手机号码（数据是插入到data表）
                ContentProviderOperation cponumber = ContentProviderOperation.newInsert(data_uri)
                        //这里的raw_contact_id的值取自于上 ops[0] 的返回值（当前是aop[2]）
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(MimeType.MIME_TYPE, MimeType.PHONE_TYPE)
                        .withValue(ContactsContract.Data.DATA1, contact.getNumber())
                        .build();
                ops.add(cponumber);
                this.mResolver.applyBatch(ContactsContract.AUTHORITY, ops);
                //循环一次插入一个联系人的相关信息
                //clear清空为下一个联系人数据做准备
                ops.clear();
            }

            //回调通知调用者批量插入数据完成
            if (insertListener != null) {
                insertListener.onInsertSuccess();
            }

        } catch (Exception e) {
            if (insertListener != null) {
                insertListener.onInsertFail(e);
            }
        }


    }


    @Override
    public void clear() {
        try {
            mResolver.delete(ContactsContract.Data.CONTENT_URI, null, null);
            mResolver.delete(ContactsContract.RawContacts.CONTENT_URI, null, null);
            if (clearListener != null) {
                clearListener.onClearSuccess();
            }

        } catch (Exception e) {
            if (clearListener != null) {
                clearListener.onClearFail(e);
            }
        }
    }


    private IBatchable.OnBatchInertListener insertListener;

    @Override
    public void setInsertListener(OnBatchInertListener insertListener) {
        this.insertListener = insertListener;
    }

    private OnBatcClearListener clearListener;

    @Override
    public void setClearListener(OnBatcClearListener clearListener) {
        this.clearListener = clearListener;
    }
}
