package com.example.bib;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bib.database.DBHelper;
import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment {

    private static final String ARG_USER_ROLE = "user_role";
    private RecyclerView recyclerView;
    private TextView tvEmptyList;
    private UserAdapter userAdapter;
    private List<User> userList = new ArrayList<>();
    private DBHelper dbHelper;
    private String userRoleFilter;

    // Use a listener to communicate actions back to the hosting Activity
    private UserAdapter.OnUserActionListener userActionListener;

    // Factory method to create new instances of the fragment with a specific role
    public static UserListFragment newInstance(String userRole) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ROLE, userRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Ensure the hosting Activity implements the listener interface
        if (context instanceof UserAdapter.OnUserActionListener) {
            userActionListener = (UserAdapter.OnUserActionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UserAdapter.OnUserActionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userRoleFilter = getArguments().getString(ARG_USER_ROLE);
        }
        dbHelper = new DBHelper(getContext()); // Initialize DBHelper here
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        tvEmptyList = view.findViewById(R.id.tvEmptyList);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Pass the listener from the Activity to the Adapter
        userAdapter = new UserAdapter(getContext(), userList, dbHelper, userActionListener);
        recyclerView.setAdapter(userAdapter);

        loadUsersByRole();
    }

    // Method to load users based on the role filter
    public void loadUsersByRole() {
        if (dbHelper == null) {
            dbHelper = new DBHelper(getContext()); // Re-initialize if null (e.g., after detachment)
        }
        List<User> allUsers = dbHelper.getAllUsers(); // Get all users first

        userList.clear();
        for (User user : allUsers) {
            if (userRoleFilter.equals("admin") && user.getRole().equalsIgnoreCase("admin")) {
                userList.add(user);
            } else if (userRoleFilter.equals("user") && !user.getRole().equalsIgnoreCase("admin")) {
                // Assuming 'user' role is anything not 'admin'
                userList.add(user);
            }
            // Add more conditions if you have other specific roles
        }
        userAdapter.notifyDataSetChanged();
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (userList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyList.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyList.setVisibility(View.GONE);
        }
    }

    // Call this method from the hosting Activity to refresh data if something changed
    public void refreshUserList() {
        loadUsersByRole();
    }

    // You might also need to handle the user removal/update directly in the fragment's list
    // This example assumes the listener will update the DB and then trigger a refresh
    public void removeUserFromList(String email) {
        int initialSize = userList.size();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getEmail().equals(email)) {
                userList.remove(i);
                userAdapter.notifyItemRemoved(i);
                userAdapter.notifyItemRangeChanged(i, userList.size());
                break;
            }
        }
        updateEmptyView();
    }
}