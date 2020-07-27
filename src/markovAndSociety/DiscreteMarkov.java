package markovAndSociety;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 进行一阶或多阶马尔科夫预测,对所有数据进行预测
 * 
 * @author Administrator
 * 
 */
public class DiscreteMarkov {

	private List<Integer> stateList;// 样本点状态时间序列,按照时间升序
	private int count;// 状态总数,对应模型的m
	private double[][][] probMatrix; // 概率转移矩阵Pij
	private double[] rk;// 各阶的自相关系数
	private double[] wk;// 各阶的权重
	private int[][] countStatic;// 频数矩阵
	private int lagPeriod;// 滞时期，K
	double[][] predictValueMarkov;// 预测概率
	private boolean isMarkov;// 目标序列是否满足马氏性

	private int[] reaState;// 实际存在的状态序号
	private double[][][] reaMatrix; // 实际存在的状态概率转移矩阵,为了加快计算多步转移概率矩阵

	private BufferedReader br;

	/**
	 * 各矩阵的初始化
	 * 
	 * @param file
	 *            USER_ID的转移AP的马尔科夫链
	 * @param count
	 *            转移AP的个数
	 * @param lagPeriod
	 *            马尔科夫预测的阶数
	 * @return 是否满足马氏性
	 */
	public boolean init(String file, int count, int lagPeriod) {
		this.lagPeriod = lagPeriod;
		this.count = count;
		try {
			stateList = new ArrayList<Integer>();
			br = new BufferedReader(new FileReader(new File(file)));
			String read = "";
			String str = "";
			while ((read = br.readLine()) != null) {// 仅一行数据，可以直接得到第一行
				str += read;
			}
			if (str.length() == 0)
				return false; // 原始数据太少，不计算
			String[] ss = str.split(",");
			for (String s : ss) {
				stateList.add(Integer.parseInt(s));
			}
			countStatic = new int[count][count];
			probMatrix = new double[lagPeriod][count][count];
			staticCount();// 进行频率矩阵，以及转移矩阵的计算

			Matrix m = new Matrix();

			if (validateMarkov()) {
				for (int k = 1; k < lagPeriod; k++) // 根据CK方程，计算各步的状态转移矩阵
				{
					m.setArray(reaMatrix[k - 1]);
					reaMatrix[k] = m.multip(reaMatrix[0]);
					// 给probMatrix赋值
					for (int i = 0; i < reaState.length; i++) {
						for (int j = 0; j < reaState.length; j++) {
							probMatrix[k][reaState[i] - 1][reaState[j] - 1] = reaMatrix[k][i][j];
						}
					}
				}

				corrCoefficient();
				timeWeight();
			}
			// else {
			// System.out.println("马氏性 检验失败,无法进行下一步预测");
			// }
		} catch (Exception e) {
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return isMarkov;

	}

	/**
	 * 验证是否满足马氏性,默认的显著性水平是0.05，自由度(m-1))^2
	 * X^2>Xa^2((m-1))^2)，m为状态数，只需考虑涉及到的状态总数计科
	 */
	public final boolean validateMarkov() {
		int totalFij = stateList.size() - 1;
		double[] cp = new double[count];// 计算P.j,边际概率
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				cp[i] += (double) countStatic[j][i] / totalFij;
			}
		}

		// 计算伽马平方统计量
		double gm = 0;
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				if (countStatic[i][j] > 0 && cp[j] > 0) {
					gm += 2 * countStatic[i][j] * Math.abs(Math.log(probMatrix[0][i][j] / cp[j]));
				}
			}
		}
		// System.out.println("gm" + gm);
		// 查表得到结果，下面是显著水平0.05时的卡方分布
		double[] table = new double[] { 3.84145882069413, 9.48772903678115, 16.9189776046204, 26.2962276048642,
				37.6524841334828, 50.9984601657106, 66.3386488629688, 83.6752607427210, 103.009508712226,
				124.342113404004, 147.673529763818, 173.004059094245, 200.333908832898, 229.663226447109,
				260.992119636005, 294.320668884306, 329.648935544535, 366.976967201223, 406.304801326655,
				447.632467830808, 490.959990876927, 536.287390198110, 583.614682067880, 632.941880026341,
				684.268995430845, 737.596037878713, 792.923015535393, 850.249935391850, 909.576803468370,
				970.903624977351, 1034.23040445441, 1099.55714586474, 1166.88385269006, 1236.21052800010,
				1307.53717451179, 1380.86379463852, 1456.19039053135, 1533.51696411377, 1612.84351711092,
				1694.17005107462, };// 状态数1...40
		if (reaState.length > 80) {
			isMarkov = (gm >= table[39]);// 考虑一下误差
		} else {
			isMarkov = (gm >= table[reaState.length / 2 - 1]);// 考虑一下误差
		}
		return isMarkov;

	}

	/**
	 * 计算相关系数Rk
	 */
	public void corrCoefficient() {
		double mean = (double) mySum(stateList) / (double) stateList.size(); // 均值
		double p = mySum2(stateList, mean);
		rk = new double[lagPeriod];

		for (int k = 0; k < lagPeriod; k++) {
			double s1 = 0;
			for (int l = 0; l < stateList.size() - lagPeriod; l++) {
				s1 += (stateList.get(l) - mean) * (stateList.get(l + k) - mean);
			}
			rk[k] = Math.abs(s1 / p);
		}
	}

	/**
	 * 计算各阶的权重Wk
	 */
	public void timeWeight() {
		wk = new double[lagPeriod];
		double sum = 0;
		for (int k = 0; k < lagPeriod; k++) {
			sum += rk[k];
		}
		for (int k = 0; k < lagPeriod; k++) {
			wk[k] = rk[k] / sum;
		}
	}

	/**
	 * 预测状态概率，输出的结果是每一个测试的概率矩阵，行数为测试的数据个数
	 * 
	 * @param fileState
	 *            带时间的测试数据的状态转移
	 * @param lagPerioDis
	 *            预测阶数
	 * @return 预测每次转移时转移到所有AP的概率数组
	 */
	public double[][] predictProb(String fileState, int lagPerioDis) {
		List<Integer> dataTest = new ArrayList<Integer>();
		try {
			br = new BufferedReader(new FileReader(new File(fileState)));
			String read = br.readLine();// 仅一行数据，直接得到
			if (read.length() > (lagPeriod + lagPerioDis)) {
				String[] str = read.split(",");
				for (int i = lagPerioDis; i < str.length; i++) {
					String[] s = str[i].split(" ");
					dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
				}
			}

			if (!isMarkov)// 不符合马氏性不预测
				return null;
			predictValueMarkov = new double[dataTest.size() - lagPeriod][count]; // 马尔科夫预测概率
			// 这里很关键，权重和滞时的关系要颠倒，循环计算的时候要注意
			int[] predictData = new int[lagPeriod];
			int index = 0;
			while (dataTest.size() > lagPeriod) {// 循环检测，计算正确率
				for (int i = 0; i < lagPeriod; i++) {
					predictData[i] = dataTest.get(i);
				}
				// 注意predictData数据是升序,最后一位对于的滞时期 是k =1
				for (int i = 0; i < count; i++) {
					for (int j = 0; j < lagPeriod; j++) {
						// 滞时期j的数据状态
						int state = predictData[predictData.length - 1 - j] - 1;
						if (state >= 0) {// else 数据里出现0地点，即出现了之前计算数据中没有的数据，不进行预测
							predictValueMarkov[index][i] += wk[j] * probMatrix[j][state][i];
						}

					}
				}
				dataTest.remove(0); // 删除第一个元素,依次检验
				index++;
			}
			return predictValueMarkov;
		} catch (Exception e) {
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 统计频数矩阵，以及计算转移概率矩阵
	 * 
	 */
	public void staticCount() {
		if (stateList.size() < 2)
			return;
		Set<Integer> set = new TreeSet<Integer>();// 求实际的状态数
		int[] number = new int[count];// 记录每个数出现的次数
		for (int i = 0; i < stateList.size() - 1; i++) {
			countStatic[stateList.get(i) - 1][stateList.get(i + 1) - 1]++;
			number[stateList.get(i) - 1]++;
			set.add(stateList.get(i));
		}
		set.add(stateList.get(stateList.size() - 1));
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				if (countStatic[i][j] > 0) {
					probMatrix[0][i][j] = (double) countStatic[i][j] / number[i];
					// System.out.print(i + " " + j + "," + probMatrix[0][i][j]
					// + " " + countStatic[i][j] + "\n");
				}

			}

		}
		// 初始化数组reaState
		reaState = new int[set.size()];
		reaMatrix = new double[lagPeriod][set.size()][set.size()];
		int index = 0;
		for (int value : set) {
			reaState[index] = value;
			index++;
		}
		// 初始化reaMatrix
		for (int i = 0; i < set.size(); i++) {
			for (int j = 0; j < set.size(); j++) {
				reaMatrix[0][i][j] = probMatrix[0][reaState[i] - 1][reaState[j] - 1];
			}
		}
	}

	/**
	 * 计算链表中所有数据的和
	 */
	public int mySum(List<Integer> state) {
		int sum = 0;
		for (int data : state) {
			sum += data;
		}
		return sum;
	}

	/**
	 * 计算链表中所有数据的方差
	 */
	public double mySum2(List<Integer> state, double mean) {
		double sum = 0;
		for (int data : state) {
			sum += (data - mean) * (data - mean);
		}
		return sum;
	}

}
