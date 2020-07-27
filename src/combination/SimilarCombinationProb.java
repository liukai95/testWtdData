package combination;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
/*
 * 组合预测，使用相对熵，相似用户使用组合的转移矩阵进行预测
 */
public class SimilarCombinationProb {
	
	private List<List<Integer>> stateListID;// 存放每个用户样本点状态时间序列,按照时间升序
	private int count;// 状态总数
	private double[][][] probMatrixID; // 概率转移矩阵Pij,第一项为用户编号
	private double[][] visitedID; // 用户访问地点的概率矩阵,第一项为用户编号
	private double[][] relativeD; // 用户间的相对熵
	private int[][][] countStaticID;// 频数矩阵,第一项为用户编号
	private boolean[][] similarID;// 两个用户是否行为相似，相似为1
	private boolean[] isMarkovID;// 每个用户的目标序列是否满足马氏性
	private BufferedReader br;

	/**
	 * 初始化数据
	 * @param fileState USER_ID的转移AP的马尔科夫链
	 * @param count 转移AP的个数
	 * @throws Exception
	 */
	public void init(String fileState, int count) throws Exception {
		this.count = count;
		visitedID = new double[245][count];
		probMatrixID = new double[245][count][count];
		countStaticID = new int[245][count][count];
		relativeD = new double[245][245];
		stateListID = new ArrayList<List<Integer>>();
		similarID = new boolean[245][245];
		isMarkovID = new boolean[245];
		for (int fileI = 1; fileI <= 245; fileI++) {
			br = new BufferedReader(new FileReader(new File(fileState + fileI
					+ ".csv")));
			String read = "";
			String str = "";
			while ((read = br.readLine()) != null) {// 仅一行数据，可以直接得到第一行
				str += read;
			}
			List<Integer> list = new ArrayList<Integer>();
			if (str.length() == 0) {
				stateListID.add(list);// 存放一个空的链表
				continue; // 原始数据太少，不计算
			}

			String[] ss = str.split(",");
			if (ss.length < 2) {
				stateListID.add(list);// 存放一个空的链表
				continue; // 原始数据太少，不计算
			}
			int[] number = new int[count];// 记录每个数出现的次数
			for (int i = 0; i < ss.length - 1; i++) {
				countStaticID[fileI - 1][Integer.parseInt(ss[i]) - 1][Integer
						.parseInt(ss[i + 1]) - 1]++;
				number[Integer.parseInt(ss[i]) - 1]++;
				list.add(Integer.parseInt(ss[i]));
			}
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < count; j++) {
					if (countStaticID[fileI - 1][i][j] > 0) {
						probMatrixID[fileI - 1][i][j] = (double) countStaticID[fileI - 1][i][j]
								/ number[i];
						// System.out.print(i + " " + j + ","
						// + probMatrixID[fileI - 1][i][j] + " "
						// + countStaticID[fileI - 1][i][j] + "\n");
					}

				}

			}
			number[Integer.parseInt(ss[ss.length - 1]) - 1]++;// 为了计算访问概率，需要把最后一个访问也算上
			list.add(Integer.parseInt(ss[ss.length - 1]));
			stateListID.add(list);// 将该用户的移动数据存放
			for (int i = 0; i < count; i++) {
				visitedID[fileI - 1][i] = (double) number[i] / ss.length;// 每个地点的访问概率
			}

		}
	}

	/**
	 * 验证每一个用户是否满足马氏性,默认的显著性水平是0.05，自由度(m-1))^2
	 * X^2>Xa^2((m-1))^2)，m为状态数，只需考虑涉及到的状态总数计科
	 */
	public void validateMarkov() {

		// 查表得到结果，下面是显著水平0.05时的卡方分布
		double[] table = new double[] { 3.84145882069413, 9.48772903678115,
				16.9189776046204, 26.2962276048642, 37.6524841334828,
				50.9984601657106, 66.3386488629688, 83.6752607427210,
				103.009508712226, 124.342113404004, 147.673529763818,
				173.004059094245, 200.333908832898, 229.663226447109,
				260.992119636005, 294.320668884306, 329.648935544535,
				366.976967201223, 406.304801326655, 447.632467830808,
				490.959990876927, 536.287390198110, 583.614682067880,
				632.941880026341, 684.268995430845, 737.596037878713,
				792.923015535393, 850.249935391850, 909.576803468370,
				970.903624977351, 1034.23040445441, 1099.55714586474,
				1166.88385269006, 1236.21052800010, 1307.53717451179,
				1380.86379463852, 1456.19039053135, 1533.51696411377,
				1612.84351711092, 1694.17005107462, };// 状态数1...40

		for (int fileI = 1; fileI <= 245; fileI++) {
			int totalFij = stateListID.get(fileI - 1).size() - 1;
			double[] cp = new double[count];// 计算P.j,边际概率
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < count; j++) {
					cp[i] += (double) countStaticID[fileI - 1][j][i] / totalFij;
				}
			}

			// 计算伽马平方统计量
			double gm = 0;
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < count; j++) {
					if (countStaticID[fileI - 1][i][j] > 0 && cp[j] > 0) {
						gm += 2
								* countStaticID[fileI - 1][i][j]
								* Math.abs(Math
										.log(probMatrixID[fileI - 1][i][j]
												/ cp[j]));
					}
				}
			}
			// System.out.println("gm" + gm);
			Set<Integer> set = new TreeSet<Integer>();// 求实际的状态数
			for (int value : stateListID.get(fileI - 1)) {
				set.add(value);
			}
			if (set.size() < 2)
				continue;// 跳出，进行下一个测试
			if (set.size() > 80) {
				isMarkovID[fileI - 1] = (gm >= table[39]);// 考虑一下误差
			} else {
				isMarkovID[fileI - 1] = (gm >= table[set.size() / 2 - 1]);// 考虑一下误差
			}
		}

	}

	/**
	 *  计算行为相似性
	 */
	public void similar() {
		for (int ii = 0; ii < 245; ii++) {
			for (int jj = 0; jj < ii; jj++) {// 存在对称性
				double d = 0;
				for (int i = 0; i < 245; i++) {
					if (visitedID[ii][i] > 0 && visitedID[jj][i] > 0) {
						d += visitedID[ii][i]
								* Math.log(visitedID[ii][i] / visitedID[jj][i])
								+ visitedID[jj][i]
								* Math.log(visitedID[jj][i] / visitedID[ii][i]);

					}
					for (int j = 0; j < 245; j++) {
						if (probMatrixID[ii][i][j] > 0
								&& probMatrixID[jj][i][j] > 0) {
							d += probMatrixID[ii][i][j]
									* Math.log(probMatrixID[ii][i][j]
											/ probMatrixID[jj][i][j])
									+ probMatrixID[jj][i][j]
									* Math.log(probMatrixID[jj][i][j]
											/ probMatrixID[ii][i][j]);

						}
					}
				}
				d = d / 2;
				if (d < 0.00001 && d > 0) {
					similarID[ii][jj] = true;
					similarID[jj][ii] = true;
					// System.out.println(ii + " " + jj + " " + d);
				}
				relativeD[ii][jj] = d;// 保存相对熵
				relativeD[jj][ii] = d;
			}
		}
	}

	/**
	 * 组合预测
	 * @param fileState 测试数据的状态转移文件
	 * @param fileI USER_ID编号
	 * @return 预测正确率
	 * @throws Exception
	 */
	public double predictProb(String fileState, int fileI) throws Exception {// 输出的结果是每一个测试的概率矩阵，行数为测试的数据个数
		if (!isMarkovID[fileI - 1]) {// 不满足马氏性
			// System.out.println("不满足马氏性");
			return -1;
		}
		// 计算新的转移概率矩阵,不重新计算马氏性了
		double[][] probMatrix = new double[count][count];// 新的概率转移矩阵Pij
		// 使用相似用户的状态数据

		int[][] countStatic = new int[count][count];// 新的频数矩阵
		int[] number = new int[count];// 记录每个数出现的次数
		// 先计算自身的转移数据
		for (int i = 0; i < stateListID.get(fileI - 1).size() - 1; i++) {
			countStatic[stateListID.get(fileI - 1).get(i) - 1][stateListID.get(
					fileI - 1).get(i + 1) - 1]++;
			number[stateListID.get(fileI - 1).get(i) - 1]++;
		}

		for (int i = 0; i < 245; i++) {
			if (similarID[fileI - 1][i]) {// 用户i与fileI相似
				// System.out.println("相似" + i);
				for (int j = 0; j < stateListID.get(i).size() - 1; j++) {
					countStatic[stateListID.get(i).get(j) - 1][stateListID.get(
							i).get(j + 1) - 1]++;
					number[stateListID.get(i).get(j) - 1]++;
				}
			}
		}
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				if (countStatic[i][j] > 0) {
					probMatrix[i][j] = (double) countStatic[i][j] / number[i];
					// System.out.print(i + " " + j + "," + probMatrix[i][j] +
					// " "
					// + countStatic[i][j] + "\n");
				}
			}

		}

		// 使用相似用户的概率转移矩阵
		// double relativeTotalD = 0;
		// for (int i = 0; i < 245; i++) {
		// if (similarID[fileI - 1][i]) {// 用户i与fileI相似
		// // System.out.println("相似" + i);
		// relativeTotalD += 1 / relativeD[fileI - 1][i];
		// }
		// }
		// // 初始化新的预测概率矩阵
		// for (int i = 0; i < count; i++) {
		// for (int j = 0; j < count; j++) {
		// probMatrix[i][j] = probMatrixID[fileI - 1][i][j];
		// }
		// }
		// for (int i = 0; i < 245; i++) {
		// if (similarID[fileI - 1][i]) {// 用户i与fileI相似
		// // 修正矩阵
		// for (int ii = 0; ii < count; ii++) {
		// for (int jj = 0; jj < count; jj++) {
		// probMatrix[ii][jj] += probMatrixID[i][ii][jj]
		// / relativeD[fileI - 1][i] / relativeTotalD;
		// }
		// }
		// }
		// }
		List<Integer> dataTest = new ArrayList<Integer>();// 测试数据
		br = new BufferedReader(new FileReader(new File(fileState)));
		String read = br.readLine();// 仅一行数据，直接得到
		br.close();
		if (read.length() > 2) {
			String[] str = read.split(",");
			for (int i = 1; i < str.length; i++) {// 从1开始，为了和二阶预测对应，这样可以比较两种方法的正确率
				String[] s = str[i].split(" ");
				dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
			}
		}
		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		double rightRate = 0;
		for (int i = 0; i < dataTest.size() - 1; i++) {
			double[] max = new double[3];// 存放3个最大的，因为概率可能会存在相等的情况
			int[] next = new int[3];// 存放3个最大的，记录转移概率最大的地点
			if (dataTest.get(i) == 0)
				continue;// 出现0地点，该次不预测
			for (int j = 0; j < count; j++) {
				for (int k = 0; k < max.length; k++) {
					if (max[k] <= probMatrix[dataTest.get(i) - 1][j]) {
						max[k] = probMatrix[dataTest.get(i) - 1][j];
						next[k] = j + 1;
						break;
					}
				}
			}
			boolean falg = false;
			for (int k = 0; k < max.length; k++) {
				if ((max[k] - max[0] < 0.01) && next[k] == dataTest.get(i + 1)) {
					// System.out.println("正确");
					rightNumber++;
					falg = true;
					break;
				}
			}
			if (!falg) {
				// System.out.println("错误");
				errorNumber++;
			}

		}
		if (rightNumber + errorNumber > 4) {
			rightRate = (double) rightNumber / (rightNumber + errorNumber);
			return rightRate;
		} else {
			return -1;
		}
	}

	/**
	 *  预测时即得到概率，为了进行分段+组合+社团修正+二阶马尔科夫的方法
	 * @param fileState 测试数据的状态转移文件
	 * @param fileI USER_ID编号
	 * @return 预测每次转移时转移到所有AP的概率数组
	 * @throws Exception
	 */
	public double[][] predictProb2(String fileState, int fileI) throws Exception {// 输出的结果是每一个测试的概率矩阵，行数为测试的数据个数
		if (!isMarkovID[fileI - 1]) {// 不满足马氏性
			// System.out.println("不满足马氏性");
			return null;
		}
		
		// 计算新的转移概率矩阵,不重新计算马氏性了
		double[][] probMatrix = new double[count][count];// 新的概率转移矩阵Pij
		// 使用相似用户的状态数据

		int[][] countStatic = new int[count][count];// 新的频数矩阵
		int[] number = new int[count];// 记录每个数出现的次数
		// 先计算自身的转移数据
		for (int i = 0; i < stateListID.get(fileI - 1).size() - 1; i++) {
			countStatic[stateListID.get(fileI - 1).get(i) - 1][stateListID.get(
					fileI - 1).get(i + 1) - 1]++;
			number[stateListID.get(fileI - 1).get(i) - 1]++;
		}

		for (int i = 0; i < 245; i++) {
			if (similarID[fileI - 1][i]) {// 用户i与fileI相似
				// System.out.println("相似" + i);
				for (int j = 0; j < stateListID.get(i).size() - 1; j++) {
					countStatic[stateListID.get(i).get(j) - 1][stateListID.get(
							i).get(j + 1) - 1]++;
					number[stateListID.get(i).get(j) - 1]++;
				}
			}
		}
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				if (countStatic[i][j] > 0) {
					probMatrix[i][j] = (double) countStatic[i][j] / number[i];
					// System.out.print(i + " " + j + "," + probMatrix[i][j] +
					// " "
					// + countStatic[i][j] + "\n");
				}
			}

		}

		// 使用相似用户的概率转移矩阵
		// double relativeTotalD = 0;
		// for (int i = 0; i < 245; i++) {
		// if (similarID[fileI - 1][i]) {// 用户i与fileI相似
		// // System.out.println("相似" + i);
		// relativeTotalD += 1 / relativeD[fileI - 1][i];
		// }
		// }
		// // 初始化新的预测概率矩阵
		// for (int i = 0; i < count; i++) {
		// for (int j = 0; j < count; j++) {
		// probMatrix[i][j] = probMatrixID[fileI - 1][i][j];
		// }
		// }
		// for (int i = 0; i < 245; i++) {
		// if (similarID[fileI - 1][i]) {// 用户i与fileI相似
		// // 修正矩阵
		// for (int ii = 0; ii < count; ii++) {
		// for (int jj = 0; jj < count; jj++) {
		// probMatrix[ii][jj] += probMatrixID[i][ii][jj]
		// / relativeD[fileI - 1][i] / relativeTotalD;
		// }
		// }
		// }
		// }
		List<Integer> dataTest = new ArrayList<Integer>();// 测试数据
		br = new BufferedReader(new FileReader(new File(fileState)));
		String read = br.readLine();// 仅一行数据，直接得到
		br.close();
		if (read.length() > 2) {
			String[] str = read.split(",");
			for (int i = 1; i < str.length; i++) {// 从1开始，为了和二阶预测对应，这样可以比较两种方法的正确率
				String[] s = str[i].split(" ");
				dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
			}
		}
		double[][] predictValueMarkov = new double[dataTest.size()-1][count]; // 对于每个测试数据的预测概率，进行返回
		for (int i = 0; i < dataTest.size() - 1; i++) {
			if (dataTest.get(i) == 0)
				continue;// 出现0地点，该次不预测
			for (int j = 0; j < count; j++) {
				predictValueMarkov[i][j]=probMatrix[dataTest.get(i) - 1][j];
	
			}

		}
		return predictValueMarkov;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimilarCombinationProb d = new SimilarCombinationProb();
		try {
			d.init("F:/移动轨迹数据/result/stateList/stateList_", 297);
			d.similar();
			d.validateMarkov();
			double totalRate = 0;
			int num = 0;// 不能够进行预测的数目
			int num2 = 0;// 预测正确率较小的的数目
			for (int i = 1; i <= 245; i++) {
				double result = d.predictProb(
						"F:/移动轨迹数据/stateListAddTimeTest/stateListAddTime_"
								+ i + ".csv", i);
				System.out.println(result);
				if (result > 0.3) {
					totalRate += result;
					continue;
				} else if (result > -1) {
					num2++;
					continue;
				}
				num++;
			}
			if (245 > (num + num2)) {
				System.out.println(num+"相似组合预测的平均正确率" + totalRate / (245 - num - num2)
						+ "失败率" + (double) num2 / 245);
			}
			
		} catch (Exception e) {

		}
	}

}
