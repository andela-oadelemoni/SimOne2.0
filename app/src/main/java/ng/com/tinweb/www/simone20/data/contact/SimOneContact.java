package ng.com.tinweb.www.simone20.data.contact;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.util.LongSparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by kamiye on 28/09/2016.
 */

public class SimOneContact implements Serializable {
    protected int id;
    protected long contactId;
    protected String name;
    protected List<String> phones;

    private Context context;
    private DataStore dataStore;

    public SimOneContact(Context context) {
        this.context = context;
    }

    public int getId() {
        return id;
    }

    public long getContactId() {
        return contactId;
    }

    public String getName() {
        return name;
    }

    public String getPhonesAsString() {
        return phones.toString();
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addPhone(String number) {
        if (phones == null) {
            phones = new ArrayList<>();
        }
        phones.add(number);
    }

    public List<SimOneContact> getList(String searchQuery) {
        initialiseDataStore();
        return dataStore.search(searchQuery);
    }

    public void syncAll(SyncCallback callback) {
        initialiseDataStore();
        Cursor cursor = getContactListCursor();

        if (cursor != null) {
            LongSparseArray<SimOneContact> contacts = generateContactsMap(cursor);
            if (dataStore.save(contacts)) {
                callback.onSuccess();
            } else {
                callback.onError("Contacts not successfully synchronised");
            }
        }
    }

    public void syncOne(String name, SyncCallback callback) {
        initialiseDataStore();

        if (dataStore.saveOne(name)) {
            callback.onSuccess();
        } else {
            callback.onError("Error adding new contact");
        }
    }

    public void delete(String name, SyncCallback callback) {
        initialiseDataStore();
        if (dataStore.deleteOne(name)) {
            callback.onSuccess();
        }
        else {
            callback.onError("Error deleting " + name);
        }
    }

    private LongSparseArray<SimOneContact> generateContactsMap(Cursor cursor) {
        LongSparseArray<SimOneContact> contacts = new LongSparseArray<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID)
            );
            String contactName = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)
            );
            SimOneContact contact = contacts.get(id);
            if (contact == null) {
                contact = new SimOneContact(context);
                contact.setContactId(id);
                contact.setName(contactName);
                contacts.put(id, contact);
            }
            String phoneNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Contactables.DATA)
            );
            String mimeType = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE)
            );
            if (mimeType.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                contact.addPhone(phoneNumber);
            }
        }
        cursor.close();
        return contacts;
    }

    private Cursor getContactListCursor() {

        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Contactables.DATA,
                ContactsContract.CommonDataKinds.Contactables.TYPE,
        };
        String selection = ContactsContract.Data.MIMETYPE + " in (?)";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
        };
        String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;

        Uri uri = ContactsContract.Data.CONTENT_URI;

        return context.getContentResolver().query(uri, projection,
                selection, selectionArgs, sortOrder);
    }

    private void initialiseDataStore() {
        if (dataStore == null) {
            dataStore = new ContactDbHelper(context);
        }
    }

    public interface SyncCallback {

        void onSuccess();

        void onError(String errorMessage);
    }
}
