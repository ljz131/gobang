package com.example.gobang;


import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PlayingView extends View {

	private static int screenHeight = PhoneScreen.screenHeight;
	private static int screenWidth = PhoneScreen.screenWidth;
	public static final int boardrow = 15; // 棋盘行为15
	public static final int boardcolumn = 15; // 棋盘行为15
	public static int step = 0; // 设定棋子步数，悔棋时用
	public static int regretRow = 0; // 获得棋子的行和列，悔棋的时候用
	public static int regretColummn = 0;

	private ChessType[][] chessBoard = new ChessType[boardrow][boardcolumn];// 创建一个15*15的棋盘，为空、白、黑的枚举类型
	private static float padding = ((float) (screenWidth) / (boardcolumn - 1)) / 2;// padding是棋盘距离边界的长度
	private static float padding_left,padding_top;//棋盘到左边和上面的距离
	private static float row_margin = (screenHeight - padding * 2) / (boardrow - 1);//行间距
	private static float column_margin = (screenWidth - padding * 2) / (boardcolumn - 1);//列间距
	private static float margin= row_margin < column_margin ? row_margin : column_margin; // 获得间距最小值，行和列取小;
	private Context context = null; // 这是一个context，用于传递
	private boolean gameOver = false; // 游戏结束的标志
	private ChessType player1 = ChessType.BLACK;// 玩家一，执黑先行
	private ChessType player2 = ChessType.WHITE;// 玩家二，执白

	public PlayingView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		this.context = context; // 让指针赋值
		this.setBackgroundResource(R.drawable.woodenbackground);
		padding_left = (screenWidth - (boardcolumn - 1) * margin) / 2;//棋盘到屏幕左边距离
		padding_top = (screenHeight - (boardrow - 1) * margin) / 2; // 棋盘到屏幕上面距离
		initChess(); // 初始化棋子，让棋子置空
	}

	public void initChess() { // 初始化棋子的函数，整个棋盘置空
		int i, j;
		for (i = 0; i < boardcolumn; i++) {
			for (j = 0; j < boardrow; j++) {
				chessBoard[i][j] = ChessType.NONE;
			}
		}
		invalidate(); // 重新绘制棋盘，刷新画面
	}

	public void reStart() { // 重新开始，让gameover为false，以免影响触控函数的判断
		initChess();
		gameOver = false;
		step = 0;
		regretColummn = 0;
		regretRow = 0;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) { // 安卓默认绘图函数，用来绘制棋盘
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Paint paint = new Paint(); // 创建一个画笔
		paint.setStrokeWidth(3); // 默认太细，为了看得清键盘
		paint.setColor(Color.WHITE); // 选择画笔颜色为白色
		for (int i = 0; i < boardrow; i++) {
			canvas.drawLine(padding_left, i * margin + padding_top,
					(boardcolumn - 1) * margin + padding_left, i * margin
							+ padding_top, paint);
		}// 用绘制线的方法来绘制行,先绘制水平线
		for (int i = 0; i < boardcolumn; i++) {
			canvas.drawLine(padding_left + i * margin, padding_top,
					padding_left + i * margin, margin * (boardrow - 1)
							+ padding_top, paint);
		}// 绘制垂直线
		paint.setColor(0x404040FF); // 画悔棋按钮背景
		canvas.drawRect(padding_left,padding_top + 15 * margin,padding_left + 6 * margin,padding_top + 17 * margin, paint);
		paint.setColor(Color.RED);//在画布上写字
		paint.setStrokeWidth(3);
		paint.setTextSize(70);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText("我要悔棋",padding_left + 3*margin,padding_top + 16 * margin,paint);
		paint.setColor(0x404040FF); // 画重开按钮背景
		canvas.drawRect(padding_left + 8 * margin,padding_top + 15 * margin,padding_left + 14 * margin,padding_top + 17 * margin, paint);
		paint.setColor(Color.GREEN);//在画布上写字
		paint.setStrokeWidth(3);
		paint.setTextSize(70);
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText("重开一局",padding_left + 11*margin,padding_top + 16 * margin,paint);
		for (int row = 0; row < boardrow; row++) { // 检测棋盘上的棋子状态，根据记录的棋子类型画棋子
			for (int column = 0; column < boardcolumn; column++) {
				if (chessBoard[row][column] == ChessType.NONE) {
					continue;
				} // 为空不用管
				if (chessBoard[row][column] == ChessType.BLACK) {
					paint.setColor(Color.BLACK); // 是黑棋就调用黑颜料画黑棋，方式为画圆
					canvas.drawCircle(row * margin + padding_left, column
							* margin + padding_top, margin / 2, paint);
				} else if (chessBoard[row][column] == ChessType.WHITE) { // 白棋用白色颜料画，方式为画圆
					paint.setColor(Color.WHITE);
					canvas.drawCircle(row * margin + padding_left, column
							* margin + padding_top, margin / 2, paint);
				}
			}
		}
	}

	public boolean SomebodyVictory(int row, int column) { // 获取最后下的棋子的位置
		ChessType chessType = chessBoard[row][column]; // 记录该位置棋子的颜色
		int count = 1; // 设定一个计数器，用来计算棋子数目，因为已经选中一个，所以值为1
		for (int i = row + 1; i < row + 5; i++) { // 由上往下纵向查找
			if (i >= PlayingView.boardrow) { // 出下边界
				break;
			}
			if (chessBoard[i][column] == chessType) { // 如果棋子类型与其相等，计数器就加一，并进行双向查找，如果有不同颜色就停止，
				count++; // 因为有可能是在中间下子达成五连珠
			} else {
				break;
			}
		}
		for (int i = row - 1; i > row - 5; i--) {// 由下往上纵向查找
			if (i < 0) { // 出上边界
				break;
			}
			if (chessBoard[i][column] == chessType) {
				count++;
			} else {
				break;
			}
		}
		if (count >= 5) {
			return true; //上下方向出现连续5个相同棋子
		}
		count = 1; // 执行到这儿意味着纵向没到5个，就初始化计数器，开始横向查找
		for (int i = column + 1; i < column + 5; i++) {
			if (i >= PlayingView.boardcolumn) { // 出右边界
				break;
			}
			if (chessBoard[row][i] == chessType) {
				count++;
			} else {
				break;
			}
		}
		for (int i = column - 1; i > column - 5; i--) {
			if (i < 0) { // 出左边界
				break;
			}
			if (chessBoard[row][i] == chessType) {
				count++;
			} else {
				break;
			}
		}
		if (count >= 5) {
			return true;
		}
		count = 1; // 继续初始化计数器，开始斜向查找，左上到右下
		for (int i = row + 1, j = column + 1; i < row + 5; i++, j++) { // 因为i与j加减情况相同，所以循环条件只设定一个就好
			if (i >= PlayingView.boardrow || j >= PlayingView.boardcolumn) { // 超出下边界或者右边界
				break;
			}
			if (chessBoard[i][j] == chessType) {
				count++;
			} else {
				break;
			}
		}
		for (int i = row - 1, j = column - 1; i > row - 5; i--, j--) {
			if (i < 0 || j < 0) { // 超出左边界或者上边界
				break;
			}
			if (chessBoard[i][j] == chessType) {
				count++;
			} else {
				break;
			}
		}
		if (count >= 5) {
			return true;
		}
		count = 1; // 再次初始化计数器，开始斜向查找，由左下到右上
		for (int i = row - 1, j = column + 1; j < column + 5; i--, j++) {
			if (j >= PlayingView.boardrow || i < 0) { // 超出右或上边界
				break;
			}
			if (chessBoard[i][j] == chessType) {
				count++;
			} else {
				break;
			}
		}
		for (int i = row + 1, j = column - 1; i < row + 5; i++, j--) {
			if (j < 0 || i >= PlayingView.boardcolumn) { // 超出左或下边界
				break;
			}
			if (chessBoard[i][j] == chessType) {
				count++;
			} else {
				break;
			}
		}
		if (count >= 5) {
			return true;
		}
		return false;//所有方向都没到，就返回false
	}

	public void SomebodyRegret() {
		chessBoard[regretRow][regretColummn] = ChessType.NONE;//悔棋就清空该位置
		step--;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) { // 检测触控点的函数
		// TODO Auto-generated method stub
		String tmp;//用于存储获胜信息
		StoreInfo DB=new StoreInfo(getContext());

		float x = event.getX();
		float y = event.getY();
		int row = Math.round((x - this.padding_left) / this.margin);//计算落在哪一个格子，四舍五入
		int column = Math.round((y - this.padding_top) / this.margin);
		if (x >= this.padding_left && x <= padding_left + 6 * margin
				&& y >= padding_top + 15 * margin
				&& y <= padding_top + 17 * margin && step != 0
				&& chessBoard[regretRow][regretColummn] != ChessType.NONE
				&& gameOver == false) { // 悔棋按钮，在按钮内，步数不为零，未曾悔过棋，未胜利
			new AlertDialog.Builder(context)
					.setMessage("您是否要悔棋呢")
					.setPositiveButton("是的",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									SomebodyRegret();
									invalidate();
								}
							}).setNegativeButton("并不", null).show();

		}
		if (x >= this.padding_left + 8 * margin
				&& x <= padding_left + 14 * margin
				&& y >= padding_top + 15 * margin
				&& y <= padding_top + 17 * margin) { // 重新开始按钮
			new AlertDialog.Builder(context)
					.setMessage("您是否要重新开始呢")
					.setPositiveButton("是的",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									reStart();
									invalidate();
								}
							}).setNegativeButton("并不", null).show();
		}
		if (!(row >= 0 && row < boardrow && column >= 0 && column < boardcolumn)) {
			return false; // 检测是否在范围内，不在范围内不响应
		}
		if (!gameOver) { // 未结束时候的状态
			if (chessBoard[row][column] == ChessType.NONE) { // 该位置非空
				regretColummn = column;//当前落的子作为可能悔棋的那一步
				regretRow = row;
				this.step++;
				if (this.step % 2 == 1) { // 步数为奇数时黑子下
					chessBoard[row][column] = this.player1;
					if (this.SomebodyVictory(row, column)) {
						this.gameOver = true;

							Calendar time = Calendar.getInstance();//获取当前时间
							String message1 = time.get(Calendar.YEAR) + "年" + (time.get(Calendar.MONTH)+1)
									+ "月" + time.get(Calendar.DAY_OF_MONTH) + "日" + time.get(Calendar.HOUR_OF_DAY)
									+ "点" + time.get(Calendar.MINUTE) + "分" + time.get(Calendar.SECOND)
									+ "秒" ;
							String message2 = "黑棋在第" + ((this.step + 1) / 2)
									+ "步胜利了~~\n" ;
							try {
								tmp=message1 + message2;
								DB.insert(tmp);//写入数据库
							} catch (Exception e) {
								e.printStackTrace();
							}
							//}

						new AlertDialog.Builder(context)
								.setTitle("胜利者的荣耀")
								.setMessage(
										"黑棋在第" + ((this.step + 1) / 2)
												+ "步胜利了~~")
								.setPositiveButton("确定", null).show();

					}
				}
				if (this.step % 2 == 0) { // 步数为偶数时白子下
					chessBoard[row][column] = this.player2;
					if (this.SomebodyVictory(row, column)) {
						this.gameOver = true;
						//if (Environment.getExternalStorageState().equals(
								//Environment.MEDIA_MOUNTED)) {// 判断是否有读写的权限
							//File sdFile = Environment.getExternalStorageDirectory();
							//String filePath = sdFile.getAbsolutePath();
							Calendar time = Calendar.getInstance();
							String message1 = time.get(Calendar.YEAR) + "年" + (time.get(Calendar.MONTH)+1)
									+ "月" + time.get(Calendar.DAY_OF_MONTH) + "日" + time.get(Calendar.HOUR_OF_DAY)
									+ "点" + time.get(Calendar.MINUTE) + "分" + time.get(Calendar.SECOND)
									+ "秒" ;
							String message2 = "白棋在第" + (this.step / 2)
									+ "步胜利了~~\n" ;
							try {
								tmp=message1 + message2;
								DB.insert(tmp);//写入数据库
							} catch (Exception e) {
								e.printStackTrace();
							}

						//}
						new AlertDialog.Builder(context)
								.setTitle("胜利者的荣耀")
								.setMessage("白棋在第" + (this.step / 2) + "步胜利了~~")
								.setPositiveButton("确定", null).show();

					}
				}

			}
		} else { // 结束时的状态
			new AlertDialog.Builder(context)
					.setTitle("这一局已经结束啦~")
					.setMessage("您是否想要再来一局呢？")
					.setPositiveButton("是的",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									reStart();
								}
							}).setNegativeButton("并不", null).show();

		}
		invalidate();// 重新绘制棋盘，刷新
		return super.onTouchEvent(event);
	}
}
