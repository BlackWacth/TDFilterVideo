package com.bruce.td.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.bruce.td.utils.L;
import com.bruce.td.utils.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class AbstractFilter {

    private static final float[] VERTEX = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
    };

    private static final float[] TEXTURE = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f
    };

    FloatBuffer mGLVertexBuffer;
    FloatBuffer mGLTextureBuffer;

    private int mVertexShaderId; //顶点着色
    private int mFragmentShaderId; //片元着色

    int mGLProgramId;
    int mVPosition;
    int mVCoord;
    int mVMatrix;
    private int mVTexture;

    int mOutputWidth;
    int mOutputHeight;

    AbstractFilter(Context context, int vertexShaderId, int fragmentShaderId) {
        mVertexShaderId = vertexShaderId;
        mFragmentShaderId = fragmentShaderId;

        //4个点， 每个点包含x、y, float是4个字节
        mGLVertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLVertexBuffer.clear();
        mGLVertexBuffer.put(VERTEX);

        mGLTextureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(TEXTURE);

        init(context);
        initCoordinate();
    }

    private void init(Context context) {
        String vShader = OpenGLUtils.readRawTextFile(context, mVertexShaderId);
        String fShader = OpenGLUtils.readRawTextFile(context, mFragmentShaderId);
        L.ii("vShader = %s, \n fShader = %s", vShader, fShader);
        mGLProgramId = OpenGLUtils.loadProgram(vShader, fShader);

        mVPosition = GLES20.glGetAttribLocation(mGLProgramId, "vPosition");
        mVCoord = GLES20.glGetAttribLocation(mGLProgramId, "vCoord");
        mVMatrix = GLES20.glGetUniformLocation(mGLProgramId, "vMatrix");
        mVTexture = GLES20.glGetUniformLocation(mGLProgramId, "vTexture");
    }

    public void onReady(int width, int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }

    public int onDrawFrame(int textureId) {
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight); //设置显示窗口
        GLES20.glUseProgram(mGLProgramId); //使用着色器

        //传递坐标
        mGLVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mVPosition, 2, GLES20.GL_FLOAT, false, 0, mGLVertexBuffer);
        GLES20.glEnableVertexAttribArray(mVPosition);

        mGLTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(mVCoord, 2, GLES20.GL_FLOAT, false, 0, mGLTextureBuffer);
        GLES20.glEnableVertexAttribArray(mVCoord);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mVTexture, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        return textureId;
    }

    public void release() {
        GLES20.glDeleteProgram(mGLProgramId);
    }

    /**
     * 修改坐标
     */
    public void initCoordinate() {

    }
}
