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

package com.udacity.popularmovies.utilities;

import android.support.annotation.StringDef;

/**
 * Created by alex on 07/04/2018.
 */

@StringDef({ SortOrder.POPULAR, SortOrder.TOP_RATED, SortOrder.FAVORITES })
public @interface SortOrder {
    String POPULAR = "popular";
    String TOP_RATED = "top_rated";
    String FAVORITES = "favorites";
}
