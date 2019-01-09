package com.helloandroid.proj2;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.FacebookException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    ActionBar bar;
    private FragmentManager fm;
    private ArrayList<Fragment> fList;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("result",object.toString());
                            }
                        });

                        String str = loginResult.getAccessToken().getToken();
                        String str2 = loginResult.getAccessToken().getUserId();
                        Log.d(str, "AAAAA");
                        Log.d(str2, "AAAAA");

                        Bundle bundle = new Bundle();
                        bundle.getInt(str2);

                        FragmentA fragmentA = new FragmentA();
                        fragmentA.setArguments(bundle);

                        FragmentB fragmentB = new FragmentB();
                        fragmentB.setArguments(bundle);

                        FragmentC fragmentC = new FragmentC();
                        fragmentC.setArguments(bundle);

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("LoginErr",error.toString());
                    }
                });

        viewPager = (ViewPager) findViewById(R.id.pager);
        fm = getSupportFragmentManager();

        bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setTitle("DBDBDIP");
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab1 = bar.newTab().setText("NUMBERS").setTabListener(tabListener);
        ActionBar.Tab tab2 = bar.newTab().setText("GALLERY").setTabListener(tabListener);
        ActionBar.Tab tab3 = bar.newTab().setText("TODOLIST").setTabListener(tabListener);

        bar.addTab(tab1);
        bar.addTab(tab2);
        bar.addTab(tab3);

        fList = new ArrayList<Fragment>();
        fList.add(FragmentA.newInstance());
        fList.add(FragmentB.newInstance());
        fList.add(FragmentC.newInstance());

        viewPager.setOnPageChangeListener(viewPagerListener);

        CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(fm, fList);
        viewPager.setAdapter(adapter);
    }

    ViewPager.SimpleOnPageChangeListener viewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);

            bar.setSelectedNavigationItem(position);
        }
    };

    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //페북 로그인 결과 담기
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
