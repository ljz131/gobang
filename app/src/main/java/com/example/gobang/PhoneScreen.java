package com.example.gobang;

public class PhoneScreen {
	public static int screenWidth;
	public static int screenHeight;
//通过主函数获得屏幕大小,在构建棋盘的时候作为参数引用
	public PhoneScreen(int screenWidth,int screenHeight) {
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
	}

}
