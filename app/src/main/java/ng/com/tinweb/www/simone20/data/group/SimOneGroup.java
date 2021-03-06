package ng.com.tinweb.www.simone20.data.group;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

/**
 * SimOneGroup - Group model
 */

public class SimOneGroup implements Serializable {

    public static final int DB_INSERT_ERROR = 99;
    public static final int GROUP_EXISTS_ERROR = 98;
    public static final int UNKNOWN_ERROR = 100;
    public static final int DB_DELETE_ERROR = 101;

    private DataStore dataStore;

    private String name;
    private String oldName;
    private int interval;
    private int members;

    /**
     * TODO this class to be further implemented in the group story
     */

    public SimOneGroup(Context context) {
        initialiseDateStore(context);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOldname(String name) {
        this.oldName = name;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getName() {
        return name;
    }

    public String getOldName() {
        return oldName;
    }

    public int getInterval() {
        return interval;
    }

    public void save(ActionCallback callback) {
        dataStore.save(name, interval, callback);
    }

    public void update(ActionCallback callback) {
        dataStore.update(oldName, name, interval, callback);
    }

    public void getAll(GetAllCallback callback) {
        dataStore.getMultiple(callback);
    }

    public void delete(String groupName, ActionCallback callback) {
        dataStore.delete(groupName, callback);
    }

    private void initialiseDateStore(Context context) {
        this.dataStore = new GroupDbHelper(context);
    }

    public interface ActionCallback extends Callback {
        void onSuccess();
    }

    public interface GetAllCallback extends Callback {
        void onSuccess(List<SimOneGroup> groups);
    }

    interface Callback {
        void onError(int errorCode);
    }
}
