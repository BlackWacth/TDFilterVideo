#extension GL_OES_EGL_image_external : require

precision mediump float;

varying vec2 aCoord;

//采样器， 从android的surfaceTexture中的纹理采集数据，所有需要使用Android的扩展纹理采集器 samplerExternal2D
uniform samplerExternalOES vTexture;

void main() {
	gl_FragColor = texture2D(vTexture, aCoord);
}
