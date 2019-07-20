package com.example.teamproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.teamproject.models.User;
import com.parse.ParseUser;


public class UserProfileFragment extends Fragment {

    Button logoutBT;
    ImageView ivProfileImage;
    TextView tvName;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        super.onViewCreated(view, savedInstanceState);

        logoutBT = getView().findViewById(R.id.logout);
        logoutBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        ivProfileImage = getView().findViewById(R.id.ivProfileImage);
        //TODO - get profile image from backend and set profile image

        tvName = getView().findViewById(R.id.tvName);
        tvName.setText(((User) currentUser).getFirstName() + " " + ((User) currentUser).getLastName());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

}