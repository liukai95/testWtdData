package combination;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * 组合预测，社团修正概率转移矩阵
 */
public class SocietyCombinationProb {

	private int[][][] idTime; // 需要使用的任意两点在所有地点相遇的时间,使用int类型,减少内存,1地点，2和3是用户，值为时间
	private String[][] totalTime;// 需要使用的任意两点在所有地点相遇的总时间，1和1是用户，值为总时间
	private List<List<Integer>> stateListID;// 存放每个用户样本点状态时间序列,按照时间升序
	private int count;// 状态总数
	private double[][][] probMatrixID; // 概率转移矩阵Pij,第一项为用户编号
	private int[][][] countStaticID;// 频数矩阵,第一项为用户编号
	private boolean[] isMarkovID;// 每个用户的目标序列是否满足马氏性
	private BufferedReader br;
	private boolean flag;// 用户是否有自己的社团

	/**
	 * 
	 * @param fileState
	 *            测试数据的状态转移文件
	 * @param count
	 *            转移AP的个数
	 * @param fileApTotalTime
	 *            任意两个USER_ID在所有地点相遇的总时间文件
	 * @param fileApTime
	 *            任意两个USER_ID在所有地点相遇的时间文件
	 * @throws Exception
	 */
	public void init(String fileState, int count, String fileApTotalTime, String fileApTime) throws Exception {
		this.count = count;
		probMatrixID = new double[245][count][count];
		countStaticID = new int[245][count][count];
		stateListID = new ArrayList<List<Integer>>();
		isMarkovID = new boolean[245];
		idTime = new int[count][245][245];
		totalTime = new String[245][245];

		// 初始化apTotalTime
		br = new BufferedReader(new FileReader(new File(fileApTotalTime)));
		String read = "";
		int i = 0;
		// 可去掉减少计算量
		while ((read = br.readLine()) != null) {
			totalTime[i] = read.split(",");
			i++;
		}
		br.close();

		// 计算预测节点与节点在所有地点相遇的概率，初始化idTime
		for (int fileI = 1; fileI <= count; fileI++) {
			br = new BufferedReader(new FileReader(new File(fileApTime + "/apTime/apTime_" + fileI + ".csv")));
			read = "";
			i = 0;
			while ((read = br.readLine()) != null) {
				String[] str = read.split(" ");
				int j = 0;
				for (String ss : str) {
					idTime[fileI - 1][i][j] = Integer.parseInt(ss);
					j++;
				}
				i++;
			}
			br.close();
		}

		for (int fileI = 1; fileI <= 245; fileI++) {
			br = new BufferedReader(new FileReader(new File(fileState + fileI + ".csv")));
			read = "";
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
			for (i = 0; i < ss.length - 1; i++) {
				countStaticID[fileI - 1][Integer.parseInt(ss[i]) - 1][Integer.parseInt(ss[i + 1]) - 1]++;
				number[Integer.parseInt(ss[i]) - 1]++;
				list.add(Integer.parseInt(ss[i]));
			}
			for (i = 0; i < count; i++) {
				for (int j = 0; j < count; j++) {
					if (countStaticID[fileI - 1][i][j] > 0) {
						probMatrixID[fileI - 1][i][j] = (double) countStaticID[fileI - 1][i][j] / number[i];
						// System.out.print(i + " " + j + ","
						// + probMatrixID[fileI - 1][i][j] + " "
						// + countStaticID[fileI - 1][i][j] + "\n");
					}

				}

			}
			list.add(Integer.parseInt(ss[ss.length - 1]));
			stateListID.add(list);// 将该用户的移动数据存放
		}
	}

	/**
	 * 验证每一个用户是否满足马氏性,默认的显著性水平是0.05，自由度(m-1))^2
	 * X^2>Xa^2((m-1))^2)，m为状态数，只需考虑涉及到的状态总数计科
	 */
	public void validateMarkov() {

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
						gm += 2 * countStaticID[fileI - 1][i][j]
								* Math.abs(Math.log(probMatrixID[fileI - 1][i][j] / cp[j]));
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
	 * 预测
	 * 
	 * @param fileState 测试数据的状态转移文件
	 * @param fileI USER_ID编号
	 * @param fileSociety 社团划分结果文件
	 * @return 预测正确率
	 * @throws Exception
	 */
	public double predictProb(String fileState, int fileI, String fileSociety) throws Exception {// 输出的结果是每一个测试的概率矩阵，行数为测试的数据个数
		if (!isMarkovID[fileI - 1]) {// 不满足马氏性
			// System.out.println("不满足马氏性");
			return -1;
		}
		// 计算新的转移概率矩阵,不重新计算马氏性了
		double[][] probMatrix = new double[count][count];// 新的概率转移矩阵Pij
		// 初始化新的预测概率矩阵
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				probMatrix[i][j] = probMatrixID[fileI - 1][i][j];
			}
		}
		// 判断是否存在社团
		flag = false;
		String read = "";
		br = new BufferedReader(new FileReader(new File(fileSociety)));
		String head = br.readLine();// 没有社团的id
		// System.out.println(head);
		List<Integer> list = new ArrayList<Integer>();// 存放测试id所在社团的成员链表
		while ((read = br.readLine()) != null) {
			String[] ss = read.split(" ");
			for (int i = 0; i < ss.length; i++) {
				int value = Integer.parseInt(ss[i]);
				if (value == fileI) { // 记录所在的社团
					flag = true;
				} else { // 自己这个不需要加到list中
					list.add(value);
				}
			}
			if (!flag) {// 如果没有找到测试id所在社团就继续找，找到就可以退出了
				list.clear();
			} else
				break;
		}
		br.close();

		long totalTimeI = 0;// fileI与社团所有成员在所有AP相遇的总时间

		if (flag) {// 有社团，进行修正
			for (int ii : list) {
				totalTimeI += Double.parseDouble(totalTime[ii - 1][fileI - 1]);// 使用double转换，否则会出现错误
			}
			double[] probRow = new double[count];// 行向量，用于中间求值
			for (int ii : list) {
				for (int i = 0; i < count; i++) {
					probRow[i] = (double) idTime[i][ii - 1][fileI - 1] / totalTimeI;
					// System.out.println(probRow[i]);
					if (probRow[i] > 0) {
						for (int j = 0; j < count; j++) {// probRow[i]与ii的第i行每个数相乘再加到fileI的第i行与第i列
							probMatrix[i][j] += probRow[i] * probMatrixID[ii - 1][i][j];
							probMatrix[j][i] = probMatrix[i][j];// 保持对称
						}
					}

				}
			}

		}

		List<Integer> dataTest = new ArrayList<Integer>();// 测试数据
		try {
			br = new BufferedReader(new FileReader(new File(fileState)));
			read = br.readLine();// 仅一行数据，直接得到
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
		} catch (Exception e) {
		}
		return -1;
	}

	/**
	 * 预测时即得到概率，为了进行分段+组合+社团修正+二阶马尔科夫的方法
	 * 
	 * @param fileState 测试数据的状态转移文件
	 * @param fileI USER_ID编号
	 * @param fileSociety 社团划分结果文件
	 * @return 预测每次转移时转移到所有AP的概率数组
	 * @throws Exception
	 */
	public double[][] predictProb2(String fileState, int fileI, String fileSociety) throws Exception {// 输出的结果是每一个测试的概率矩阵，行数为测试的数据个数
		if (!isMarkovID[fileI - 1]) {// 不满足马氏性
			// System.out.println("不满足马氏性");
			return null;
		}
		// 计算新的转移概率矩阵,不重新计算马氏性了
		double[][] probMatrix = new double[count][count];// 新的概率转移矩阵Pij
		// 初始化新的预测概率矩阵
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				probMatrix[i][j] = probMatrixID[fileI - 1][i][j];
			}
		}

		// 判断是否存在社团
		flag = false;
		String read = "";
		br = new BufferedReader(new FileReader(new File(fileSociety)));
		String head = br.readLine();// 没有社团的id
		// System.out.println(head);
		List<Integer> list = new ArrayList<Integer>();// 存放测试id所在社团的成员链表
		while ((read = br.readLine()) != null) {
			String[] ss = read.split(" ");
			for (int i = 0; i < ss.length; i++) {
				int value = Integer.parseInt(ss[i]);
				if (value == fileI) { // 记录所在的社团
					flag = true;
				} else { // 自己这个不需要加到list中
					list.add(value);
				}
			}
			if (!flag) {// 如果没有找到测试id所在社团就继续找，找到就可以退出了
				list.clear();
			} else
				break;
		}
		br.close();

		long totalTimeI = 0;// fileI与社团所有成员在所有AP相遇的总时间

		if (flag) {// 有社团，进行修正

			for (int ii : list) {
				totalTimeI += Double.parseDouble(totalTime[ii - 1][fileI - 1]);// 使用double转换，否则会出现错误
			}
			System.out.println("***");
			double[] probRow = new double[count];// 行向量，用于中间求值

			for (int ii : list) {
				for (int i = 0; i < count; i++) {
					probRow[i] = (double) idTime[i][ii - 1][fileI - 1] / totalTimeI;
					// System.out.println(probRow[i]);
					if (probRow[i] > 0) {
						for (int j = 0; j < count; j++) {// probRow[i]与ii的第i行每个数相乘再加到fileI的第i行与第i列
							probMatrix[i][j] += probRow[i] * probMatrixID[ii - 1][i][j];
							probMatrix[j][i] = probMatrix[i][j];// 保持对称
						}
					}

				}
			}

		}

		List<Integer> dataTest = new ArrayList<Integer>();// 测试数据
		try {
			br = new BufferedReader(new FileReader(new File(fileState)));
			read = br.readLine();// 仅一行数据，直接得到
			br.close();
			if (read.length() > 2) {
				String[] str = read.split(",");
				for (int i = 1; i < str.length; i++) {// 从1开始，为了和二阶预测对应，这样可以比较两种方法的正确率
					String[] s = str[i].split(" ");
					dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
				}
			}
		} catch (Exception e) {
		}
		double[][] predictValueMarkov = new double[dataTest.size()][count]; // 对于每个测试数据的预测概率，进行返回
		for (int i = 0; i < dataTest.size() - 1; i++) {
			if (dataTest.get(i) == 0)
				continue;// 出现0地点，该次不预测
			for (int j = 0; j < count; j++) {
				predictValueMarkov[i][j] = probMatrix[dataTest.get(i) - 1][j];
			}

		}

		return predictValueMarkov;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SocietyCombinationProb d = new SocietyCombinationProb();
		try {
			d.init("F:/移动轨迹数据/result/stateList/stateList_", 297, "F:/test/apTotalTime.txt", "F:/移动轨迹数据/apVisited");
			d.validateMarkov();
			double totalRate = 0;
			int num = 0;// 不能够进行预测的数目
			int num2 = 0;// 预测正确率较小的的数目
			for (int i = 1; i <= 245; i++) {
				double result = d.predictProb("F:/移动轨迹数据/stateListAddTimeTest/stateListAddTime_" + i + ".csv", i,
						"F:/test/RESULT.txt");
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
				System.out
						.println(num + "社团修正转移矩阵的平均正确率" + totalRate / (245 - num - num2) + "失败率" + (double) num2 / 245);
			}

		} catch (Exception e) {
		}

	}

}
