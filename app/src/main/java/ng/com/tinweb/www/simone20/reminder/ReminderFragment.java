package ng.com.tinweb.www.simone20.reminder;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import javax.inject.Inject;

import ng.com.tinweb.www.simone20.R;
import ng.com.tinweb.www.simone20.SimOne;
import ng.com.tinweb.www.simone20.data.reminder.SimOneReminder;
import ng.com.tinweb.www.simone20.databinding.FragmentReminderBinding;
import ng.com.tinweb.www.simone20.util.LinearLayoutDecorator;

/**
 * Reminder Fragment class
 */
public class ReminderFragment extends Fragment implements ReminderContract.View,
        ReminderActionsListener {

    private static final String EDIT_REMINDER_FRAGMENT_TAG = "edit_reminder";

    private FragmentReminderBinding fragmentBinding;
    private SearchView searchView;

    @Inject
    ReminderPresenter reminderPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimOne.get(getActivity().getApplication())
                .getAppComponent()
                .subComponent(new ReminderModule(this))
                .inject(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder,
                container, false);

        loadReminders();
        setupRecyclerView();
        return fragmentBinding.getRoot();
    }

    @Override
    public void onRemindersLoaded(List<SimOneReminder> reminders) {
        fragmentBinding.weeklyRemindersRecyclerView.setAdapter(new ReminderAdapter(reminders,
                this));
        fragmentBinding.remindersFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });
    }

    @Override
    public void onReminderLoadingError() {
        // TODO: 17/04/2017 show visual loading error in UI as well
        Log.e("REMINDER_ERROR", "Error loading reminders");
    }

    @Override
    public void setWeekReminderTextView(int total) {
        fragmentBinding.weeklyRemindersTextView.setText(getResources()
                .getQuantityString(R.plurals.no_of_calls_this_week,
                        total, total));
    }

    @Override
    public void showDeleteSuccessInfo() {
        loadReminders();
        Toast.makeText(getContext(), R.string.reminder_deleted_message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showDeleteErrorInfo() {
        new AlertDialog.Builder(getContext())
                .setMessage("Error deleting reminder")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    @Override
    public void onEditAction(SimOneReminder reminder) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(EDIT_REMINDER_FRAGMENT_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        ReminderDialogFragment dialogFragment = ReminderDialogFragment.getInstance(reminder);
        dialogFragment.setEditMode(true);
        fragmentTransaction.add(dialogFragment, EDIT_REMINDER_FRAGMENT_TAG).commitNow();
        //addReminderFragment.show(fragmentTransaction, EDIT_REMINDER_FRAGMENT_TAG);
    }

    @Override
    public void onDeleteAction(final SimOneReminder reminder) {
        AlertDialog confirmationDialog = new AlertDialog.Builder(getContext())
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        reminderPresenter.deleteReminder(reminder.getReminderContactId());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setMessage(getString(R.string.reminder_delete_confirmation,
                        reminder.getContactName()))
                .create();
        confirmationDialog.show();
        //reminderPresenter.deleteReminder(contactId);
    }

    public void loadReminders() {
        reminderPresenter.loadReminders();
    }

    private void setupRecyclerView() {

        Context context = fragmentBinding.getRoot().getContext();

        fragmentBinding.weeklyRemindersRecyclerView
                .setLayoutManager(new LinearLayoutManager(context));

        fragmentBinding.weeklyRemindersRecyclerView
                .addItemDecoration(new LinearLayoutDecorator(context, null));
    }


}
