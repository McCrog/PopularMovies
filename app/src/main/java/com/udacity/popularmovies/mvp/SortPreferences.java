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

package com.udacity.popularmovies.mvp;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.udacity.popularmovies.common.Constants.APP_PREFERENCES;
import static com.udacity.popularmovies.common.Constants.APP_SORT_PREFERENCE;

/**
 * Created by McCrog on 07/03/2018.
 *
 */

public class SortPreferences {
    private SharedPreferences sharedPreferences;

    public SortPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }

    public void saveSortPreference(int referense) {
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(APP_SORT_PREFERENCE, referense);
        editor.apply();
    }

    public int getSortPreference() {
        return sharedPreferences.getInt(APP_SORT_PREFERENCE, 0);
    }
}
