package markovAndSociety;

/**
 * 矩阵类，实现两个矩阵的相乘
 * @author Administrator
 *
 */
public class Matrix {
	private int row;
	private int col;
	private double[][] array;

	public double[][] getArray() {
		return array;
	}

	public void setArray(double[][] array) {
		// 判断 二维数组是否为合法矩阵
		int row = array.length;
		int col = array[0].length;
		for (int i = 1; i < row; i++) {
			if (col != array[i].length) {
				System.out.println("输入的不是一个矩阵,请重新输入");
				return;
			}
		}
		this.row = row;
		this.col = col;
		this.array = array;
	}
	
	//输出矩阵
	public String toString() {
		if (array == null) {
			return "矩阵为空,请输入一个矩阵";
		}
		String s = "";
		row = array.length;
		col = array[row - 1].length;

		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				s += array[i][j] + " ";
			}
			s += "\r\n";
		}
		return s;
	}
	
	//传入一个矩阵类进行相乘
	public Matrix multip(Matrix x) {
		Matrix m = new Matrix();
		m.setArray(multip(x.getArray()));
		return m;

	}
	
	//传入一个二维数组进行相乘
	public double[][] multip(double[][] aim) {
		if (this.col != aim.length) {
			System.out.println("两个矩阵不能相乘");
			return null;
		}
		double[][] result = new double[this.row][aim[0].length];
		for (int row = 0; row < this.row; row++) {
			for (int col = 0; col < aim[0].length; col++) {
				double num = 0;
				for (int i = 0; i < this.col; i++) {
					num += array[row][i] * aim[i][col];
				}
				result[row][col] = num;
			}
		}
		return result;
	}

//	public static void main(String[] args) {
//		Matrix m = new Matrix();
//		Matrix m2 = new Matrix();
//		double[][] rec = { { 1, 0, 3 }, { 2, 1, 3 }, { 3, 5, 7 } };
//		double[][] rec2 = { { 4, 1 }, { 1, 1 }, { 2, 0 } };
//
//		m.setArray(rec);
//		m2.setArray(rec2);
//
//		System.out.println(m);
//		System.out.println(m2);
//		System.out.println(m.multip(m2));
//
//	}

}
