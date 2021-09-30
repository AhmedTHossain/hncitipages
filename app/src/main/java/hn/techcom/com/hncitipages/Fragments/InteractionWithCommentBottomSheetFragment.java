package hn.techcom.com.hncitipages.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class InteractionWithCommentBottomSheetFragment extends BottomSheetDialogFragment {

    private Utils myUtils;
    private Profile userProfile;
    private String commentedUserHnid;
    private int commentId;

    private NavigationView navigationView;

    public InteractionWithCommentBottomSheetFragment(int id, String hnid) {
        // Required empty public constructor
        commentId = id;
        commentedUserHnid = hnid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interaction_with_comment_bottom_sheet, container, false);

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        navigationView = view.findViewById(R.id.navigation_interact_with_post);

        return view;
    }
}