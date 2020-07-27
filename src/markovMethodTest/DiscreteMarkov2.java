package markovMethodTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 进行一或多步马尔科夫预测
 * 引入一个新的ID连接每段链，第一种方法
 * 
 * @author Administrator
 * 
 */
public class DiscreteMarkov2 {
	
	private List<Integer> stateList;// 样本点状态时间序列,按照时间升序
	private int count;// 状态总数,对应模型的m
	private static double[][][] probMatrix; // 概率转移矩阵Pij
	private double[] rk;// 各阶的自相关系数
	private double[] wk;// 各阶的权重
	private static int[][] countStatic;// 频数矩阵
	private int lagPeriod;// 滞时期，K
	private double[] predictValue;// 预测概率
	private boolean isMarkov;// 目标序列是否满足马氏性

	public void init(List<Integer> data, int count, int k) {
		stateList = data;
		lagPeriod = k;
		this.count = count;
		countStatic = new int[count][count];
		probMatrix = new double[lagPeriod][count][count];

		staticcount(stateList, count);//进行矩阵的计算
		Matrix m = new Matrix();

		if (validateMarkov()) {
			//满足马氏性
			for (int i = 1; i < k; i++) // 根据CK方程，计算各步的状态转移矩阵
			{
				m.setArray(probMatrix[i - 1]);
				probMatrix[i] = m.multip(probMatrix[0]);
			}
			
			corrCoefficient();
			timeWeight();
		} else {
			System.out.println("马氏性 检验失败,无法进行下一步预测");
		}
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
					gm += 2 * countStatic[i][j]
							* Math.abs(Math.log(probMatrix[0][i][j] / cp[j]));
				}
			}
		}
		System.out.println("gm" + gm);
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
		Set<Integer> set = new HashSet<Integer>();// 求实际的状态数

		for (Integer value : stateList) {
			set.add(value);
		}
		System.out.println(set.size());
		if(set.size()>80){
			isMarkov = (gm >= table[39]);// 考虑一下误差
		}else{
			isMarkov = (gm >= table[set.size() / 2 - 1]);// 考虑一下误差
		}
		return isMarkov;

	}

	/**
	 * 计算相关系数
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
			System.out.println("wk"+k+ wk[k]);

		}
	}

	/**
	 * 预测状态概率
	 */
	public double predictProb(List<Integer> data) {
		if (!isMarkov || data.size() <= lagPeriod)// 不符合马氏性或者数据太少，不预测
			return 0;
		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		// 这里很关键，权重和滞时的关系要颠倒，循环计算的时候要注意
		int[] predictData = new int[lagPeriod];
		while (data.size() > lagPeriod) {// 循环检测，计算正确率
			predictValue = new double[count];// 每次循环，预测结果需重新规零
			for (int i = 0; i < lagPeriod; i++) {
				predictData[i] = data.get(i);
			}
			// 注意predictData数据是升序,最后一位对于的滞时期 是k =1
			for (int i = 0; i < count; i++) {
				for (int j = 0; j < lagPeriod; j++) {
					// 滞时期j的数据状态
					int state = predictData[predictData.length - 1 - j] - 1;
					if (state >= 0) {// else 数据里出现0地点，即出现了之前计算数据中没有的数据，不进行预测
						predictValue[i] += wk[j] * probMatrix[j][state][i];
					}
				}
			}
			double max = 0;
			int next = 0;// 记录转移概率最大的地点
			 System.out.println("转移结果");
			 //去掉引入的地点
			for (int i = 0; i < count-1; i++) {
				 if (predictValue[i] > 0) {
				 //System.out.println(i + 1 + " " + predictValue[i]);
				
				 }
				if (max < predictValue[i]) {
					max = predictValue[i];
					next = i + 1;
				}

			}
			System.out.println("下一个转移的地点为" + next+"实际"+data.get(lagPeriod));
			if (next == data.get(lagPeriod)) {
				 System.out.println("正确");
				rightNumber++;
			} else {
				System.out.println("错误");
				errorNumber++;
			}

			data.remove(0); // 删除第一个元素
		}
		double rightRate = (double) rightNumber / (rightNumber + errorNumber);
		System.out.println(rightNumber + errorNumber);
		System.out.println("正确率" + rightRate);
		return rightRate;
	}

	/**
	 * 统计频数矩阵，以及计算转移概率矩阵
	 * 
	 */
	public static void staticcount(List<Integer> data, int statuscount) {

		int[] number = new int[statuscount];// 记录每个数出现的次数
		for (int i = 0; i < data.size() - 1; i++) {
			countStatic[data.get(i) - 1][data.get(i + 1) - 1]++;
			number[data.get(i) - 1]++;
		}
		for (int i = 0; i < statuscount; i++) {
			for (int j = 0; j < statuscount; j++) {
				if (countStatic[i][j] > 0) {
					probMatrix[0][i][j] = (double) countStatic[i][j]
							/ number[i];
					// System.out.print(i + " " + j + "," + probMatrix[0][i][j]
					// + " " + countStatic[i][j] + "\n");
				}

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

	public static void main(String[] args) {
		//引入一个新的ID连接每段链
		DiscreteMarkov2 dm = new DiscreteMarkov2();
		List<Integer> data = new ArrayList<Integer>();
		List<Integer> data2 = new ArrayList<Integer>();
		String str = "6,4,4,5,2,4,6,1,2,6,5,6,4,4,6,5,3,6,5,2,5,3,3,4,4,4,1,1,1,1,3,7," +
				"6,5,5,5,5,4,6,5,4,1,3,1,3,1,3,1,2,5,2,2,5,7," +
				"1,4,4,2,6,1,5,4,6,3,2,2,6,4,4,4,4,3,1,5,3,1,2,6,5,3,6,7" +
				",6,4,6,2,4,4,6,3,3,6,2,6,1,3,2,2,6,6,4,4,3,1,4,1,2,6,4,4,1,2";
		
		String str2 = "6,4,4,5,2,4,6,1,2,6,5,6,4,4,6,5,3,6,5,2,5,3,3,4,4,4,1,1,1,1,3," +
				"5,6,5,5,5,5,4,6,5,4,1,3,1,3,1,3,1,2,5,2,2,5," +
				"5,1,4,4,2,6,1,5,4,6,3,2,2,6,4,4,4,4,3,1,5,3,1,2,6,5,3,6" +
				",3,6,4,6,2,4,4,6,3,3,6,2,6,1,3,2,2,6,6,4,4,3,1,4,1,2,6,4,4,1,2";
		try {

			String[] ss = str.split(",");
			for (String s : ss) {
				data.add(Integer.parseInt(s));
			}
			ss = str2.split(",");
			for (String s : ss) {
				data2.add(Integer.parseInt(s));
			}
			dm.init(data, 7, 3);
			dm.predictProb(data2);

		} catch (Exception e) {
		}

	}
}
