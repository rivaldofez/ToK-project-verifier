package com.example.setripverifier.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.setripverifier.R;
import com.example.setripverifier.activity.CheckinVerifier;
import com.example.setripverifier.activity.CheckoutVerifier;


public class CheckFragment extends Fragment {

    private ImageView imgCheckin;
    private ImageView imgCheckout;

    public CheckFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_check, container, false);

        imgCheckin = view.findViewById(R.id.imgCheckin);
        imgCheckout = view.findViewById(R.id.imgCheckout);

        imgCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckinVerifier.class);
                startActivity(intent);
            }
        });

        imgCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CheckoutVerifier.class);
                startActivity(intent);
            }
        });

        return view;
    }
}