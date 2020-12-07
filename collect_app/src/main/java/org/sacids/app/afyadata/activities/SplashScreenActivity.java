/*
 * Copyright (C) 2011 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.sacids.app.afyadata.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import org.sacids.app.afyadata.R;
import org.sacids.app.afyadata.app.PrefManager;
import org.sacids.app.afyadata.utilities.AfyaDataUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SplashScreenActivity extends AppCompatActivity {

    static final String TAG = "splash";
    Context mContext = this;
    PrefManager mPrefManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AfyaDataUtils.loadLanguage(SplashScreenActivity.this);
        setContentView(R.layout.splash_screen);

        mPrefManager = new PrefManager(this);

        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                if (mPrefManager.isLoggedIn())
                    startActivity(new Intent(mContext, MainActivity.class));
                else
                    startActivity(new Intent(mContext, FrontPageActivity.class));

                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}