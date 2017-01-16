package io.github.waka.sevenhack.views.components;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import io.github.waka.sevenhack.R;

public class MainHeaderView extends FrameLayout {

    public MainHeaderView(Context context) {
        super(context);
        setup();
    }

    public MainHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public MainHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.view_main_header, this, true);
    }
}
