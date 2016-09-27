package com.library.provide;

import android.provider.ContactsContract;

/**
 * Created by chen on 2016/9/22.
 */
public class MimeType {

    public static final String MIME_TYPE = ContactsContract.Contacts.Data.MIMETYPE;

    public static final String NAME_TYPE = "vnd.android.cursor.item/name";
    public static final String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
    public static final String EMAIL_TYPE = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
    public static final String PHOTO_TYPE = ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE;
    public static final String ORGANIZATION_TYPE = ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE;
    public static final String NICKNAME_TYPE = ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE;
    public static final String IM_TYPE = ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE;
}
