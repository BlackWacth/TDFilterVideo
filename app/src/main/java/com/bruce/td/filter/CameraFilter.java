package com.bruce.td.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.bruce.td.R;
import com.bruce.td.utils.OpenGLUtils;

public class CameraFilter extends AbstractFilter {

    /**
     * 坐标变换
     * 1、首先把纹理坐标逆时针旋转90度
     * 2、然后沿竖直方向的中线翻转180度
     * 也可以
     * 以左下和右上的对角线翻转180度
     */
    private static final float[] TEXTURE = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };

    private int[] mFrameBuffers;
    private int[] mFrameBufferTextures;
    private float[] mMatrix;

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vertex, R.raw.camera_frag);
    }

    @Override
    public void initCoordinate() {
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(TEXTURE);
    }

    @Override
    public void onReady(int width, int height) {
        super.onReady(width, height);

        if (mFrameBuffers != null) {
            destroyFrameBuffers();
        }

        //FBO的创建(Frame Buffer Object)
        //1 创建FBO,离屏缓存
        mFrameBuffers = new int[1];
        //参数1：创建FBO的数量。
        //参数2：保存FBO的id数据。
        //参数3：偏移量，从数组的第几个开始保存。
        GLES20.glGenFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);

        //创建FBO的纹理
        mFrameBufferTextures = new int[1];
        OpenGLUtils.glGenTextures(mFrameBufferTextures);

        //绑定FBO纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        //创建一个2D图像，参数 ： 2D纹理 + 等级 + 格式 + 宽 + 高 + 格式 + 数据格式（byte） + 像素数据
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mOutputWidth, mOutputHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        //FBO纹理和FBO缓存绑定起来。
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);

        //解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);

        //绑定FBO，不绑定的默认操作的是GLSurfaceView中的纹理，显示在屏幕上
        //这里只是画的FBO缓存中
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);

        //使用着色器
        GLES20.glUseProgram(mGLProgramId);

        //传递坐标
        mGLVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mVPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);
        GLES20.glEnableVertexAttribArray(mVPosition);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mVCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(mVCoord);

        //变换矩阵
        GLES20.glUniformMatrix4fv(mVMatrix, 1, false, mMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //解绑
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        return mFrameBufferTextures[0];
    }

    @Override
    public void release() {
        super.release();
        destroyFrameBuffers();
    }

    private void destroyFrameBuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(mFrameBufferTextures.length, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }

        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(mFrameBuffers.length, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    public void setMatrix(float[] matrix) {
        mMatrix = matrix;
    }
}
