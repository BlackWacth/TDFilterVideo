precision mediump float; //float数据采用什么精度，这里中等精度

varying vec2 aCoord; //采样点的坐标

//采样器， 不是从android的surfaceTexture中的纹理采集数据，所有采用正常的采集器 sampler2D
uniform sampler2D vTexture;

void main() {
	gl_FragColor = texture2D(vTexture, aCoord);
}
