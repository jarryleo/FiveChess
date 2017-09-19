package cn.leo.fivechess.AI;

import cn.leo.fivechess.bean.Chess;

public class FiveChessAI /* extends Thread */{
	/**
	 * 五子棋AI
	 * 
	 * @author 刘佳睿
	 */
	Chess chess[][];// = new Chess[15][15]; // 接受棋盘的所有棋子
	private int weight[][] = new int[15][15]; // 权重
	private int computerColor; // 电脑要走的棋子颜色 黑1 或 白2 ,0为空
	private int lastX; // 遍历坐标
	private int lastY;

	public Chess getPoint(Chess chess[][], int color) { // 返回计算机落子
		this.chess = chess;
		this.computerColor = color;
		Chess point = new Chess(); // 定义返回的对象
		int x = -1, y = -1; // 计算机要走的坐标
		int max = 0; // 最大权重
		getWeight(); // 计算权重
		for (int i = 0; i < chess.length; i++) {
			for (int j = 0; j < chess[i].length; j++) {
				if (max <= weight[i][j]) {
					if (Math.random()*100 > 66 && max == weight[i][j] ) { //加点随机事件
						continue;
					}
					max = weight[i][j]; // 获取最大权重
					x = i; // 获取坐标
					y = j;
				}
			}
		}
		point.x = x; // 返回的是编号，不是坐标
		point.y = y;
		point.color = computerColor;
		return point;
	}

	private void getWeight() { // 获取所有坐标的权重
		for (int i = 0; i < chess.length; i++) {
			for (int j = 0; j < chess[i].length; j++) {
				lastX = i;
				lastY = j;
				weight[i][j] = weightSum(computerColor)
						+ weightSum(3 - computerColor);
			}
		}
	}

	private int weightSum(int color) { // 一个坐标的四线总权重
		int weight = 0; // 总权重 = 四条线权重相加
		if (chess[lastX][lastY].color > 0) { // 坐标有子权重为0
			return 0;
		}
		weight = singleWeight(1, 0, color) + singleWeight(0, 1, color)
				+ singleWeight(1, 1, color) + singleWeight(-1, 1, color);
		if (lastX == 7 && lastY == 7) { // 中间点权重+1
			weight+=100;
		}
		return weight;
	}

	private int singleWeight(int x, int y, int color) { // 单线权重(棋力调教这里)
		int weight = 2; // 权重
		int twoSide=twoSide(x, y, color);//两边障碍
		int num = sameLine(x, y, color);// 连线同色子数
		boolean mid = isMid(x, y, color);//判断落点是否在中间
		if (liveSpace(x, y, color) < 5) { // 这条线生存空间小于5权重为0
			weight = 0;
			return weight;
		}
		weight = weight << num; // 计算权重
		if (num >= 5) {
			weight = weight + 10000; //5子连线权重+10000
		}else if (num == 4){
			weight = weight + 8000;
		}else if (num == 3){
			weight = weight + 5000;
		}else if (num == 2){
			weight = weight + 1000;
		}
		if (twoSide > 0 && num < 5) { // 一边被阻权重减半
			weight = weight >> 1;
		}
		if (color != computerColor ) { // 敌对棋子权重
			if (num > 4 ) { 
				if (twoSide == 0 && !mid) { //对方5子且 两边没障碍不拦
					weight = 0;
					return weight;  
				}else if(twoSide == 1 || mid){
					weight += 10000;
				}
			}else if (weight > 1000) {
				weight-=1000;
			}
		}else{
			if (num > 4 ) { 
				if (twoSide < 2) { //己方全力下子
					weight += 10000; 
				}
			}
		}
		return weight;
	}
	private boolean isMid(int x, int y, int color) { // 判断下的子是中间还是两端
		int i = lastX;
		int j = lastY;
		boolean mid = false;
		int left = 0; //左边棋子数
		int right = 0; //右边棋子数
		do { // 检测直线一边的同色子或空格数
			if (chess[i][j].color == 0 && !(i == lastX && j == lastY)) {
				break;
			}
			i += x;
			j += y;
			left ++;
		} while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测

		i = lastX;
		j = lastY;
		do { // 检测直线另一边同色子或空格数
			if (chess[i][j].color == 0 && !(i == lastX && j == lastY)) {
				break;
			}
			i -= x;
			j -= y;
			right ++;
		} while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测
		if (left >1 && right >1) { //两边棋子数大于1,落点在中间
			mid =true;
		}
		return mid;
	}
	private int twoSide(int x, int y, int color) { // 判断2端是否有障碍，返回两端障碍数
		int i = lastX;
		int j = lastY;
		int side = 2; // 初始化障碍
		do { // 检测直线一边的同色子或空格数
			if (chess[i][j].color != color && !(i == lastX && j == lastY)) {
				if (chess[i][j].color == 0) {
					side--; // 遇到1边空格，障碍-1
				}
				break;
			}
			i += x;
			j += y;
		} while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测

		i = lastX;
		j = lastY;
		do { // 检测直线另一边同色子或空格数
			if (chess[i][j].color != color && !(i == lastX && j == lastY)) {
				if (chess[i][j].color == 0) {
					side--; // 遇到1边空格，障碍-1
				}
				break;
			}
			i -= x;
			j -= y;
		} while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测
		return side;
	}

	private int liveSpace(int x, int y, int color) { // 判断一条线的生存空间，两段遇到异色子或边界前
													 // 中间的格子数
		int num = 0; // 同一直线同色棋子数
		int i = lastX;
		int j = lastY;
		do { // 检测直线一边的同色子或空格数
			if (chess[i][j].color == 3 - color) { // 遇到异色子跳出
				break;
			}
			i += x;
			j += y;
		} while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测
		num = Math.max(Math.abs(lastX - i), Math.abs(lastY - j));

		i = lastX;
		j = lastY;
		do { // 检测直线另一边同色子或空格数
			if (chess[i][j].color == 3 - color) { // 遇到异色子跳出
				break;
			}
			i -= x;
			j -= y;
		} while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测
		num = --num + Math.max(Math.abs(lastX - i), Math.abs(lastY - j));
		return num;
	}

	private int sameLine(int x, int y, int color) { // 落子后判断一条线有多少个连线的同色子
		int num = 0; // 同一直线同色棋子数
		int i = lastX;
		int j = lastY;
		do { // 检测直线一边同色棋子数
			if (chess[i][j].color != color && !(i == lastX && j == lastY)) {
				break;
			}
			i += x;
			j += y;
		} while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测
		num = Math.max(Math.abs(lastX - i), Math.abs(lastY - j));

		i = lastX;
		j = lastY;
		do { // 检测直线另一边同色棋子数
			if (chess[i][j].color != color && !(i == lastX && j == lastY)) {
				break;
			}
			i -= x;
			j -= y;
		} while (!(i < 0 || i > 14 || j < 0 || j > 14)); // 边界检测
		num = --num + Math.max(Math.abs(lastX - i), Math.abs(lastY - j));
		return num;
	}
}
