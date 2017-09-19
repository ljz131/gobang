package com.example.gobang;


import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;


public class PlayingGame extends Activity{
    //这是一个用Java写的Activity，用于下棋
	public PhoneScreen phoneScreen;
	public PlayingView playingView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getScreen();//获取当前屏幕大小，方便后面绘制棋盘
		playingView = new PlayingView(this);//初始化工作
		PlayingView.step=0;
		setContentView(playingView);//放一个view上去
	}


	
	private void getScreen() {
		WindowManager manager = this.getWindowManager();
		DisplayMetrics outMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(outMetrics);
		int width = outMetrics.widthPixels;//获取当前屏幕的尺寸（像素数）
		int height = outMetrics.heightPixels;
		phoneScreen = new PhoneScreen(width, height);
	}
}
