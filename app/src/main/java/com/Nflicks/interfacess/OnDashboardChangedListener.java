package com.Nflicks.interfacess;

import com.Nflicks.model.FollowingCategoryModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by om on 22-Feb-17.
 */
public interface OnDashboardChangedListener {
    void onNoteListChanged(ArrayList<FollowingCategoryModel> customers);
}