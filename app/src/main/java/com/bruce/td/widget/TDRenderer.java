package com.bruce.td.widget;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.bruce.td.filter.CameraFilter;
import com.bruce.td.filter.ScreenFilter;
import com.bruce.td.utils.CameraHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TDRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private TDView mTDView;

    private ScreenFilter mScreenFilter;
    private CameraFilter mCameraFilter;

    private CameraHelper mCameraHelper;
    private SurfaceTexture mSurfaceTexture;

    private float[] mtx = new float[16];
    private int[] mTextures;

    public TDRenderer(TDView TDView) {
        mTDView = TDView;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mTDView.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
        mTextures = new int[1];
        GLES20.glGenTextures(mTextures.length, mTextures, 0);
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCameraFilter = new CameraFilter(mTDView.getContext());
        mScreenFilter = new ScreenFilter(mTDView.getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width, height);
        mScreenFilter.onReady(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清除屏幕
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //输出摄像头数据
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mtx); //获得变换矩阵
        mCameraFilter.setMatrix(mtx);
        int id = mCameraFilter.onDrawFrame(mTextures[0]);
        mScreenFilter.onDrawFrame(id);
    }

    public void onSurfaceDestroyed() {
        mCameraHelper.stopPreview();
    }
}
