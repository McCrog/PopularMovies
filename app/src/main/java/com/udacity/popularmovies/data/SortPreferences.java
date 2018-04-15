/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;
import static com.udacity.popularmovies.utilities.Constants.APP_PREFERENCES;
import static com.udacity.popularmovies.utilities.Constants.APP_SORT_PREFERENCE;

/**
 * Created by McCrog on 07/03/2018.
 *
 */

public class SortPreferences {
    private static final String LOG_TAG = SortPreferences.class.getSimpleName();

    private final SharedPreferences mSharedPreferences;

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static SortPreferences sInstance;

    private SortPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
    }

    /**
     * Get the singleton for this class
     */
    public static SortPreferences getInstance(Context context) {
        Log.d(LOG_TAG, "Getting the preference data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new SortPreferences(context);
                Log.d(LOG_TAG, "Made new preference data source");
            }
        }
        return sInstance;
    }

    public void saveSortPreference(int preference) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(APP_SORT_PREFERENCE, preference);
        editor.apply();
    }

    public int getSortPreference() {
        return mSharedPreferences.getInt(APP_SORT_PREFERENCE, 0);
    }

    private SharedPreferences.Editor getEditor() {
        return mSharedPreferences.edit();
    }
}
