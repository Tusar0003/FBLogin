package com.example.fblogin;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private LoginButton mFBLoginButton;

    private CallbackManager mFBCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFBLoginButton = findViewById(R.id.fb_login_button);

        mFBCallbackManager = CallbackManager.Factory.create();
//        mFBLoginButton.setReadPermissions(Arrays.asList("email", "public profile"));
        mFBLoginButton.setReadPermissions(Arrays.asList("email"));

        mFBLoginButton.registerCallback(mFBCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "onSuccess: " + loginResult.toString());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: " + error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mFBCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    AccessTokenTracker mAccessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            // If current access token is null that user is log out
            if (currentAccessToken == null) {
                Log.e(TAG, "onCurrentAccessTokenChanged: user is logged out.");
            } else {
                loadUserProfile(currentAccessToken);
            }
        }
    };

    private void loadUserProfile(AccessToken currentAccessToken) {
        // Gor getting the user info we have to make graph API request
        GraphRequest graphRequest = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.e(TAG, "onCompleted: " + object);

                    String firstName = object.getString("first_name");
                    String lastName = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");

                    // From ID creating the image url
                    String imageUrl = "https://graph.facebook.com/" + id + "picture?type=normal";

                    Log.e(TAG, "onCompleted: " + firstName + "\n" + lastName + "\n" + email + "\n" +
                            id + "\n" + imageUrl);
                } catch (JSONException e) {
                    Log.e(TAG, "onCompleted: " + e);
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
//        parameters.putString("fields", "first_name, last_name, email, id");
        parameters.putString("fields", "email");
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}
