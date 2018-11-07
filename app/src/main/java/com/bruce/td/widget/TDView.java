package com.bruce.td.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class TDView extends GLSurfaceView {

    private TDRenderer mTDRenderer;

    public TDView(Context context) {
        this(context, null);
    }

    public TDView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);
        mTDRenderer = new TDRenderer(this);
        setRenderer(mTDRenderer);

        //按需渲染， 当调用requestRender请求GLThread就会回调一次onDrawFrame
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        mTDRenderer.onSurfaceDestroyed();
    }
}
