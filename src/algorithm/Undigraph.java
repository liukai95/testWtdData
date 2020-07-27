package algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

public class Undigraph

{

	/** 点A[i]和B[i]的边的权重 */

	private double[] weight = new double[530000];

	/** 点A */

	private int[] pa = new int[530000];

	/** 点B */

	private int[] pb = new int[530000];

	/** 边数 */

	private int length = 0;

	/** 点数 */

	private int pointCount = 0;

	/** 记录被遍历了的点 */

	private StringBuffer sb = new StringBuffer();

	private StringBuffer result = new StringBuffer();

	/**
	 * 
	 * 记录已经被“访问”的点，如果该点已经被访问，则不再记录
	 * 
	 * 
	 * 
	 * @param k
	 *            要记录的被访问的点值
	 */

	private void addToVnew(int k)

	{

		if (!isInVnew(k))

		{

			String c = sb.length() == 0 ? "" : ",";

			sb.append(c + k);

		}

	}

	/**
	 * 
	 * 判断点是否已经被记录
	 * 
	 * 
	 * 
	 * @param k
	 * 
	 * @return
	 */

	private boolean isInVnew(int k)

	{

		boolean b1 = sb.indexOf("," + k + ",") != -1;

		boolean b2 = sb.toString().equals("" + k)

		|| sb.toString().startsWith(k + ",");

		boolean b3 = sb.toString().endsWith("," + k);

		return b1 || b2 || b3;

	}

	/** 返回被访问的点的数目，当其值为总点数的时候，证明算法已经完成 */
	private int countVnew()

	{

		return sb.toString().split(",").length;

	}

	/**
	 * 
	 * 构造函数初始化一个图，inis[a][b]表示点a和b所连边的权值 由于是无向图，初始化时只记录数组中a<b的值
	 * 
	 * 除此之外，还会记录点的数目和有效边的数目（权值不为0），也未把没有边的点添加进来
	 * 
	 * 
	 * 
	 * @param inis
	 *            二维数组所表示的一个无向图
	 */

	public Undigraph(double inis[][])

	{

		for (int i = 0; i < inis.length; i++) {
			for (int j = i + 1; j < inis[i].length; j++)

				if (inis[i][j] > 0)

				{

					pa[length] = i;

					pb[length] = j;

					weight[length] = inis[i][j];
					length++;

					this.addToVnew(i);

					this.addToVnew(j);

				}
		}

		pointCount = inis.length;

		sb.delete(0, sb.length());

		sort();// 注意：初始化后的图已经按照边权值由大到小排序了
	}

	private void show()

	{

		System.out.println("-------------------");

		for (int i = 0; i < length; i++)

		{

			System.out.println(i + "--" + pa[i] + "--" + pb[i] + " :"
					+ weight[i]);
		}

	}

	private void swap(double[] a, int i, int j)

	{

		if (i != j)

		{
			double temp = a[i];
			a[i] = a[j];
			a[j] = temp;

		}

	}

	private void swap2(int[] a, int i, int j)

	{

		if (i != j)

		{
			int temp = a[i];
			a[i] = a[j];
			a[j] = temp;

		}

	}

	/** 按照边的权重值重新排序 */

	private void sort()

	{

		for (int i = 1; i < length; i++) {
			for (int j = 0; j < length - i; j++) {
				if (weight[j] < weight[j + 1])

				{

					swap(weight, j, j + 1);

					swap2(pa, j, j + 1);

					swap2(pb, j, j + 1);

				}
			}
			

		}
	}

	public void getMinTreeByPrim()

	{

		result.delete(0, result.length());

		int i = 0, k = 0;

		addToVnew(pa[i]);

		while (countVnew() < pointCount)

		{

			if (length == i)

				i = 0;

			boolean b1 = isInVnew(pa[i]) && !isInVnew(pb[i]);

			boolean b2 = isInVnew(pb[i]) && !isInVnew(pa[i]);

			if (b1 || b2)

			{

				addToResult(i, ++k);

				addToVnew(pb[i]);

				addToVnew(pa[i]);

				i = 0;

			}

			i++;// 第一条边在第一次循环时肯定已经被记录，所以不用再从第一条边考察

		}

		// System.out.println(result);

	}

	public void getMinTreeByKruskal()

	{

		result.delete(0, result.length());

		int j, i = 0, k = 0;

		// vset表示各点处于的连通分量，若vset[0]=vset[2]，则表示点0和点2处于同一个连通分量

		int[] vset = new int[pointCount];

		for (j = 0; j < pointCount; j++)

			vset[j] = j;

		// 生成的边数小于pointCount-1时循环
		while (k < pointCount - 1)

		{
			System.out.println(pointCount+"pb"+pb[i]);

			if (vset[pa[i]] != vset[pb[i]]) {

				addToResult(i, ++k);

				// 两个集合统一编号

				for (j = 0; j < pointCount; j++)
					if (vset[j] == vset[pb[i]])
						vset[j] = vset[pa[i]];
			}

			// 扫描下一条边

			i++;

		}
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(
					"F:\\移动轨迹数据2\\doublePointRateMaxtirResult.txt"));
			pw.println(result);
		} catch (IOException e) {

			e.printStackTrace();
		}
		System.out.println(result);

	}

	private void addToResult(int i, int step) {

		result.append("第" + step + "步: " + (pa[i] + 1) + "-" + (pb[i] + 1)
				+ " 权值:" + weight[i] + "\n");

	}

	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(new File(
				"F:\\移动轨迹数据2\\doublePointRateMaxtir.txt")));

		String read = "";
		double[][] totalNum = new double[524][524];

		for (int i = 0; i < 524; i++) {
			totalNum[i] = new double[524];
			read = br.readLine();
			System.out.println(read);
			String[] str = read.split(" ");
			for (int j = 0; j < 524; j++) {

				totalNum[i][j] = Double.parseDouble(str[j]);

			}

		}
		Undigraph p = new Undigraph(totalNum);
		// double prim[][] = new double[7][7];
		//
		// prim[0] = new double[]
		//
		// { 0, 0, 0, 4, 2, 6,0 };
		//
		// prim[1] = new double[]
		//
		// { 0, 0, 6, 1, 5, 0,0 };
		//
		// prim[2] = new double[]
		//
		// { 0, 6, 0, 5, 0, 3,0 };
		//
		// prim[3] = new double[]
		//
		// { 4, 1, 5, 0, 5, 6,0 };
		//
		// prim[4] = new double[]
		//
		// { 2, 5, 0, 5, 0, 0,0 };
		//
		// prim[5] = new double[]
		//
		// { 6, 0, 3, 6, 0, 0,0 };
		// prim[6] = new double[]
		//
		// { 0, 0, 0, 0, 0, 0,0};
		//
		// Undigraph p = new Undigraph(prim);
		p.getMinTreeByKruskal();
		// p.getMinTreeByPrim();
		p.show();
	}
}
