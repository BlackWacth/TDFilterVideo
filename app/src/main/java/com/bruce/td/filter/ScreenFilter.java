package com.bruce.td.filter;

import android.content.Context;

import com.bruce.td.R;

public class ScreenFilter extends AbstractFilter {

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vertex, R.raw.base_frag);
    }
}
