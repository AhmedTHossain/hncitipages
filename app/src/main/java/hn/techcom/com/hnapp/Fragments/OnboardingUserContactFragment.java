package hn.techcom.com.hnapp.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.Random;

import hn.techcom.com.hnapp.Models.NewUser;
import hn.techcom.com.hnapp.Models.User;
import hn.techcom.com.hnapp.R;


public class OnboardingUserContactFragment extends Fragment {

    private static final String TAG = "ContactFragment";
    private TextInputEditText emailText, phoneText;
    private CountryCodePicker countryCodePicker;
    private NewUser newUser = null;

    public OnboardingUserContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_user_contact, container, false);

        //hooks
        LinearLayout phoneLayout = view.findViewById(R.id.layout_phone_onboarding_contact);
        LinearLayout emailLayout = view.findViewById(R.id.layout_email_onboarding_contact);

        emailText = view.findViewById(R.id.textinputedittext_email);
        phoneText = view.findViewById(R.id.textinputedittext_phone);
        countryCodePicker = view.findViewById(R.id.country_code_picker);

        // Shows layout based on user's sign-in method
        newUser = getNewUserFromSharedPreference();
        String email = newUser.getEmail();

        if(email != null){
            phoneLayout.setVisibility(View.VISIBLE);
        }else{
            emailLayout.setVisibility(View.VISIBLE);
        }



        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        String currentPhoneFieldValue = countryCodePicker.getSelectedCountryCodeWithPlus() + Objects.requireNonNull(phoneText.getText()).toString();

        Log.d(TAG,"email = "+emailText.getText().toString());
        Log.d(TAG,"phone = "+ currentPhoneFieldValue);

        //adding user's phone number to shared preference
        newUser.setMobileNumber(currentPhoneFieldValue);
        storeNewUserToSharedPref();
    }

    private NewUser getNewUserFromSharedPreference() {
        SharedPreferences pref = getContext().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("NewUser","");
        NewUser user = gson.fromJson(json, NewUser.class);

        return user;
    }

    private void storeNewUserToSharedPref() {
        SharedPreferences.Editor editor = getContext().getSharedPreferences("MY_PREF", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(newUser);
        editor.putString("NewUser",json);
        editor.apply();
    }
}