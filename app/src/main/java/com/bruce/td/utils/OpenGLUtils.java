package com.bruce.td.utils;

import android.content.Context;
import android.content.res.Resources;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OpenGLUtils {

    /**
     * 从raw目录中读取对应着色器代码
     * @param context context
     * @param rawId id
     * @return 着色器代码
     */
    public static String readRawTextFile(Context context, int rawId) {
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = context.getResources().openRawResource(rawId);
            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Resources.NotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 加载着色器程序
     * @param vSource 顶点着色器代码
     * @param fSource 片元着色器代码
     * @return 着色器程序ID
     */
    public static int loadProgram(String vSource, String fSource) {
        //顶点着色器
        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vShader, vSource); //加载着色器代码
        GLES20.glCompileShader(vShader); //编译着色器
        int[] status = new int[1];
        GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) { //失败
            throw new IllegalStateException("load vertex shader:" + GLES20.glGetShaderInfoLog(vShader));
        }

        //片元着色器
        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShader, fSource);
        GLES20.glCompileShader(fShader);
        GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) { //失败
            throw new IllegalStateException("load fragment shader:" + GLES20.glGetShaderInfoLog(vShader));
        }

        //创建着色器程序
        int program = GLES20.glCreateProgram();

        //绑定顶点和片元着色器
        GLES20.glAttachShader(program, vShader);
        GLES20.glAttachShader(program, fShader);

        //链接着色器程序
        GLES20.glLinkProgram(program);
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {//失败
            throw new IllegalStateException("link program:" + GLES20.glGetProgramInfoLog(program));
        }

        GLES20.glDeleteShader(vShader);
        GLES20.glDeleteShader(fShader);

        return program;
    }

    /**
     * 纹理创建并配置
     * @param textures 保存纹理id的数组
     */
    public static void glGenTextures(int[] textures) {
        GLES20.glGenTextures(textures.length, textures, 0);
        for (int i = 0; i < textures.length; i++) {
            //OpenGL的操作是面向过程的
            //bind是绑定，后续操作都在这个这个纹理上进行，直到解绑.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);

            //过滤参数：当纹理被使用到一个比它大或者比它小的形状上，应该如何处理
            //GLES20.GL_LINEAR  : 使用纹理中坐标附近的若干个颜色，通过平均算法，进行放大
            //GLES20.GL_NEAREST : 使用纹理坐标最接近的一个颜色作为放大的要绘制的颜色。
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);


            //设置纹理环绕方向，纹理坐标一般用st表现，相当于x,y, 纹理坐标的范围0~1。
            //超出0~1的坐标将被OpenGL根据GL_TEXTURE_WRAP参数的值进行处理。
            //GL_TEXTURE_WRAP_S 表示 x 方向
            //GL_TEXTURE_WRAP_T 表示 y 方向
            //GL_REPEAT : 平铺
            //GL_MIRRORED_REPEAT : 纹理坐标是奇数时使用镜像平铺
            //GL_CLAMP_TO_EDGE : 坐标超出部分被截取成0、1，边缘拉伸
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            //解绑
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }
}
