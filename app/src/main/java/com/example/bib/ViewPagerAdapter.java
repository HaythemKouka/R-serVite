package com.example.bib;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private String[] tabTitles = new String[]{"Admins", "Users"};

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return UserListFragment.newInstance("admin"); // For Admin users
            case 1:
                return UserListFragment.newInstance("user");  // For general users (non-admin)
            default:
                return UserListFragment.newInstance("all"); // Fallback or "All Users"
        }
    }

    @Override
    public int getItemCount() {
        return tabTitles.length;
    }

    public String getTabTitle(int position) {
        return tabTitles[position];
    }
}