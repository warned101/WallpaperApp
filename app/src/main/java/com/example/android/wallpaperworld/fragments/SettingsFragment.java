package com.example.android.wallpaperworld.fragments;

import android.content.Intent;
<<<<<<< HEAD
import android.net.Uri;
=======
>>>>>>> f03574b653ff620a6b6504157962ea2ed6ad46fc
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.Button;
=======
>>>>>>> f03574b653ff620a6b6504157962ea2ed6ad46fc
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.wallpaperworld.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SettingsFragment extends Fragment {

    private static final int GOOGLE_SIGN_IN_CODE = 212;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return inflater.inflate(R.layout.fragment_settings_default, container, false);
        }
        return inflater.inflate(R.layout.fragment_settings_logged_in, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            ImageView imageView = view.findViewById(R.id.image_view);
            TextView textViewName = view.findViewById(R.id.text_view_name);
<<<<<<< HEAD
            Button btn = view.findViewById(R.id.idbtn);
=======
>>>>>>> f03574b653ff620a6b6504157962ea2ed6ad46fc

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Glide.with(getActivity())
                    .load(user.getPhotoUrl().toString())
                    .into(imageView);

            textViewName.setText(user.getDisplayName());

<<<<<<< HEAD
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setData(Uri.parse("Email"));
                    String[] s = {"astitvagupta123@gmail.com"};
                    intent.putExtra(Intent.EXTRA_EMAIL, s);
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Featuring Images");
                    intent.putExtra(Intent.EXTRA_TEXT, "Hello, I ensure that all the images that I am sharing belong to only me. I someone reports a duplicacy the developer has the right to remove my images!");
                    intent.setType("message/rfc822");
                    Intent chooser = Intent.createChooser(intent, "Share via Email");
                    startActivity(chooser);
                }
            });

=======
>>>>>>> f03574b653ff620a6b6504157962ea2ed6ad46fc
            view.findViewById(R.id.text_view_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_area, new SettingsFragment())
                            .commit();
                        }
                    });
                }
            });

        } else {
            view.findViewById(R.id.button_google_sign_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(intent, GOOGLE_SIGN_IN_CODE);
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthwithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthwithGoogle(GoogleSignInAccount account) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(),
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_area, new SettingsFragment())
                            .commit();
                        }else{
                            Toast.makeText(getActivity(),"Login Failure", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
