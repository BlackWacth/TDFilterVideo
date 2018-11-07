
//vec4表示4个float，代表2个平面坐标点.
attribute vec4 vPosition; //把顶点坐标给这个变量，确定要画的形状

//接收纹理坐标，接收采样器采样图片的坐标
//不用和矩阵相乘，接收一个点只要2个float就可以了。
attribute vec2 vCoord;

varying vec2 aCoord; //传给片元着色器 像素点

void main() {
	gl_Position = vPosition;

	aCoord = vCoord;
}
