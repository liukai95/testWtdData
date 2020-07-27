package markovAndSociety;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 计算预测的正确率
 *
 */
public class ResultTest {

	private int num = 3;// 保存个概率数
	private double rate = 0.01;// 概率误差

	/**
	 * 仅使用马尔科夫链预测，考虑步数
	 * 
	 * @param predictRate
	 *            预测每次转移时转移到所有AP的概率数组
	 * @param fileState
	 *            USER_ID的转移AP的马尔科夫链
	 * @param lagPeriodMax
	 *            预测的最大马尔科夫阶数，主要是为了减少各阶比较时的误差
	 * @return 预测的正确率
	 * @throws Exception
	 */
	public double rightRateTotal(double[][] predictRate, String fileState, int lagPeriodMax) throws Exception {
		if (predictRate == null)
			return -1;// 无法进行预测
		BufferedReader br = new BufferedReader(new FileReader(new File(fileState)));
		String read = br.readLine();// 仅一行数据，直接得到
		br.close();
		List<Integer> dataTest = new ArrayList<Integer>();
		if (read.length() > lagPeriodMax) {
			String[] str = read.split(",");
			for (int i = lagPeriodMax; i < str.length; i++) {// 从步长差值开始，前几个数据舍弃
				String[] s = str[i].split(" ");
				dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
			}
		}

		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		double rightRate = 0;
		for (int i = 0; i < predictRate.length; i++) {// 因为之前已经是使用转移链表计算的转移概率，所以可以直接使用概率矩阵的长度进行预测
			double[] max = new double[num];// 存放num个最大的，因为概率可能会存在相等的情况
			int[] next = new int[num];// 存放num个最大的，记录转移概率最大的地点
			if (dataTest.get(i) == 0)
				continue;// 出现0地点，该次不预测

			for (int j = 0; j < predictRate[0].length; j++) {
				for (int k = 0; k < max.length; k++) {
					if (max[k] <= predictRate[i][j]) {
						max[k] = predictRate[i][j];
						next[k] = j + 1;
						break;
					}
				}
			}
			boolean falg = false;
			for (int k = 0; k < max.length; k++) {
				// System.out.println(k+"m"+max[k]);
				if ((max[0] - max[k] < rate) && (next[k] == dataTest.get(i))) {// 与最大概率差距小于rate
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
		if (rightNumber + errorNumber > 4) {// 大于10次的预测进行统计
			rightRate = (double) rightNumber / (rightNumber + errorNumber);
			return rightRate;
		}
		return -1;// 表示没有进行测试,为了统计没有进行测试的个数，方便求平均值。社团修正就不需要了
	}

	/**
	 * 使用马尔科夫链预测加社团修正，考虑步数
	 * 
	 * @param predictMarkov
	 *            马尔科夫预测每次转移时转移到所有AP的概率数组
	 * @param predictSociety
	 *            社团修正预测每次转移时转移到所有AP的概率数组
	 * @param fileState
	 *            USER_ID的转移AP的马尔科夫链
	 * @param lagPeriodMax
	 *            预测的最大马尔科夫阶数，主要是为了减少各阶比较时的误差
	 * @param factor
	 *            社团修正系数
	 * @return 预测的正确率
	 * @throws Exception
	 */
	public double rightRateSocietyTotal(double[][] predictMarkov, double[][] predictSociety, String fileState,
			int lagPeriodMax, double factor) throws Exception {
		if (predictMarkov == null)
			return -1;
		BufferedReader br = new BufferedReader(new FileReader(new File(fileState)));
		String read = br.readLine();// 仅一行数据，直接得到
		br.close();
		List<Integer> dataTest = new ArrayList<Integer>();// 用于判断预测结果是否正确
		if (read.length() > lagPeriodMax) {
			String[] str = read.split(",");
			for (int i = lagPeriodMax; i < str.length; i++) {
				String[] s = str[i].split(" ");
				dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
			}
		}
		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		double rightRate = 0;// 正确率
		// System.out.println(predictMarkov.length+""+predictSociety.length);
		for (int i = 0; i < predictMarkov.length; i++) {// 因为之前已经是使用转移链表计算的转移概率，所以可以直接使用概率矩阵的长度进行预测
			double[] max = new double[num];// 存放num个最大的，因为概率可能会存在相等的情况
			int[] next = new int[num];// 存放num个最大的，记录转移概率最大的地点
			if (dataTest.get(i) == 0)
				continue;// 出现0地点，该次不预测

			for (int j = 0; j < predictMarkov[0].length; j++) {
				double newrate = factor * (predictSociety[i][j] - predictMarkov[i][j]) + predictMarkov[i][j];
				for (int k = 0; k < max.length; k++) {
					if (max[k] <= newrate) {
						max[k] = newrate;
						next[k] = j + 1;
						break;
					}
				}
			}
			boolean falg = false;
			for (int k = 0; k < max.length; k++) {
				// System.out.println(k+"m"+max[k]);
				if ((max[0] - max[k] < rate) && (next[k] == dataTest.get(i))) {
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
		if (rightNumber + errorNumber > 4) {// 大于10次的预测进行统计
			rightRate = (double) rightNumber / (rightNumber + errorNumber);
			return rightRate;
		}
		return -1;// 表示没有进行测试,为了统计没有进行测试的个数，方便求平均值。社团修正就不需要了
	}

	/**
	 * 分段预测 仅使用马尔科夫链预测，考虑步数
	 * 
	 * @param predictRate
	 *            预测每次转移时转移到所有AP的概率数组
	 * @param fileState
	 *            USER_ID的转移AP的马尔科夫链
	 * @param lagPeriodMax
	 *            预测的最大马尔科夫阶数，主要是为了减少各阶比较时的误差
	 * @return 预测的正确率
	 * @throws Exception
	 */
	public double rightRateSplit(double[][] predictRate, String fileState, int lagPeriodMax) throws Exception {
		if (predictRate == null)
			return -1;
		BufferedReader br = new BufferedReader(new FileReader(new File(fileState)));

		br = new BufferedReader(new FileReader(new File(fileState)));
		String read = "";
		List<Integer> dataTest = new ArrayList<Integer>();// 中间变量，存放需要进行预测的状态转
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			if (str.length > lagPeriodMax) {// 只有大于最大步数才有计算的必要
				for (int i = lagPeriodMax; i < str.length; i++) {// 从最大步数开始预测时间，和马尔科夫链预测对应
					String[] s = str[i].split(" ");
					dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
				}
			}
		}
		br.close();

		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		double rightRate = 0;
		for (int i = 0; i < predictRate.length; i++) {// 因为之前已经是使用转移链表计算的转移概率，所以可以直接使用概率矩阵的长度进行预测
			double[] max = new double[num];// 存放num个最大的，因为概率可能会存在相等的情况
			int[] next = new int[num];// 存放num个最大的，记录转移概率最大的地点
			if (dataTest.get(i) == 0)
				continue;// 出现0地点，该次不预测

			for (int j = 0; j < predictRate[0].length; j++) {
				for (int k = 0; k < max.length; k++) {
					if (max[k] <= predictRate[i][j]) {
						max[k] = predictRate[i][j];
						next[k] = j + 1;
						break;
					}
				}
			}
			boolean falg = false;
			for (int k = 0; k < max.length; k++) {
				if ((max[0] - max[k] < rate) && (next[k] == dataTest.get(i))) {
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
		br.close();
		if (rightNumber + errorNumber > 4) {
			rightRate = (double) rightNumber / (rightNumber + errorNumber);
			return rightRate;
		}
		return -1;// 表示没有进行测试
	}

	/**
	 * 使用马尔科夫链预测加社团修正，考虑步数,分段数据
	 * 
	 * @param predictMarkov
	 *            马尔科夫预测每次转移时转移到所有AP的概率数组
	 * @param predictSociety
	 *            社团修正预测每次转移时转移到所有AP的概率数组
	 * @param fileState
	 *            USER_ID的转移AP的马尔科夫链
	 * @param lagPeriodMax
	 *            预测的最大马尔科夫阶数，主要是为了减少各阶比较时的误差
	 * @param factor
	 *            社团修正系数
	 * @return 预测的正确率
	 * @throws Exception
	 */
	public double rightRateSocietySplit(double[][] predictMarkov, double[][] predictSociety, String fileState,
			int lagPeriodMax, double factor) throws Exception {
		if (predictMarkov == null)// 仅考虑马尔科夫即可，一个为空，则另一个也为空
			return -1;
		BufferedReader br = new BufferedReader(new FileReader(new File(fileState)));
		br = new BufferedReader(new FileReader(new File(fileState)));
		String read = "";
		List<Integer> dataTest = new ArrayList<Integer>();// 中间变量，存放需要进行预测的状态转
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			if (str.length > lagPeriodMax) {// 只有大于最大步数才有计算的必要
				for (int i = lagPeriodMax; i < str.length; i++) {// 从最大步数开始预测时间，和马尔科夫链预测对应
					String[] s = str[i].split(" ");
					dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
				}
			}
		}
		br.close();
		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		double rightRate = 0;
		for (int i = 0; i < predictMarkov.length; i++) {// 因为之前已经是使用转移链表计算的转移概率，所以可以直接使用概率矩阵的长度进行预测
			double[] max = new double[num];// 存放num个最大的，因为概率可能会存在相等的情况
			int[] next = new int[num];// 存放num个最大的，记录转移概率最大的地点
			if (dataTest.get(i) == 0)
				continue;// 出现0地点，该次不预测

			for (int j = 0; j < predictMarkov[0].length; j++) {
				double newrate = factor * (predictSociety[i][j] - predictMarkov[i][j]) + predictMarkov[i][j];
				for (int k = 0; k < max.length; k++) {
					if (max[k] <= newrate) {
						max[k] = newrate;
						next[k] = j + 1;
						break;
					}
				}
			}
			boolean falg = false;
			for (int k = 0; k < max.length; k++) {
				if ((max[k] - max[0] < rate) && (next[k] == dataTest.get(i))) {
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
		br.close();
		if (rightNumber + errorNumber > 4) {
			rightRate = (double) rightNumber / (rightNumber + errorNumber);
			return rightRate;
		}
		return -1;// 表示没有进行测试
	}

	/**
	 * 使用分段+组合+马尔科夫+社团修正
	 * 
	 * @param predictMarkov
	 *            马尔科夫预测每次转移时转移到所有AP的概率数组
	 * @param predictCom
	 *            组合预测每次转移时转移到所有AP的概率数组
	 * @param predictSociety
	 *            社团修正预测每次转移时转移到所有AP的概率数组
	 * @param fileState
	 *            USER_ID的转移AP的马尔科夫链
	 * @param lagPeriodMax
	 *            预测的最大马尔科夫阶数，主要是为了减少各阶比较时的误差
	 * @param factor
	 *            社团修正系数
	 * @return 预测的正确率
	 * @throws Exception
	 */
	public double rightRateSocietyCom(double[][] predictMarkov, double[][] predictCom, double[][] predictSociety,
			String fileState, int lagPeriodMax, double factor) throws Exception {
		if (predictMarkov == null)
			return -1;
		// System.out.println(predictMarkov.length+","+predictCom.length+","+predictSociety.length);
		BufferedReader br = new BufferedReader(new FileReader(new File(fileState)));
		String read = br.readLine();// 仅一行数据，直接得到
		br.close();
		List<Integer> dataTest = new ArrayList<Integer>();// 用于判断预测结果是否正确
		if (read.length() > lagPeriodMax) {
			String[] str = read.split(",");
			for (int i = lagPeriodMax; i < str.length; i++) {
				String[] s = str[i].split(" ");
				dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
			}
		}
		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		double rightRate = 0;// 正确率
		for (int i = 0; i < predictMarkov.length; i++) {// 因为之前已经是使用转移链表计算的转移概率，所以可以直接使用概率矩阵的长度进行预测
			double[] max = new double[num];// 存放num个最大的，因为概率可能会存在相等的情况
			int[] next = new int[num];// 存放num个最大的，记录转移概率最大的地点
			if (dataTest.get(i) == 0)
				continue;// 出现0地点，该次不预测

			for (int j = 0; j < predictMarkov[0].length; j++) {
				double newrate = factor * (predictSociety[i][j] - 0.5 * predictMarkov[i][j] - 0.5 * predictCom[i][j])
						+ 0.5 * predictMarkov[i][j] + 0.5 * predictCom[i][j];
				for (int k = 0; k < max.length; k++) {
					if (max[k] <= newrate) {
						max[k] = newrate;
						next[k] = j + 1;
						break;
					}
				}
			}
			boolean falg = false;
			for (int k = 0; k < max.length; k++) {
				// System.out.println(k+"m"+max[k]);
				if ((max[0] - max[k] < rate) && (next[k] == dataTest.get(i))) {
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
		if (rightNumber + errorNumber > 4) {// 大于10次的预测进行统计
			rightRate = (double) rightNumber / (rightNumber + errorNumber);
			return rightRate;
		}
		return -1;// 表示没有进行测试,为了统计没有进行测试的个数，方便求平均值。社团修正就不需要了
	}

	/**
	 * 使用组合+马尔科夫+社团修正,组合使用相似组合与社团组合
	 * 
	 * @param predictMarkov
	 *            马尔科夫预测每次转移时转移到所有AP的概率数组
	 * @param predictCom
	 *            相似组合预测每次转移时转移到所有AP的概率数组
	 * @param predictSoc
	 *            社团组合预测每次转移时转移到所有AP的概率数组
	 * @param predictSociety
	 *            社团修正预测每次转移时转移到所有AP的概率数组
	 * @param fileState
	 *            USER_ID的转移AP的马尔科夫链
	 * @param lagPeriodMax
	 *            预测的最大马尔科夫阶数，主要是为了减少各阶比较时的误差
	 * @param factor
	 *            社团修正系数
	 * @return 预测的正确率
	 * @throws Exception
	 */
	public double rightRateSocietyComTotal(double[][] predictMarkov, double[][] predictCom, double[][] predictSoc,
			double[][] predictSociety, String fileState, int lagPeriodMax, double factor) throws Exception {
		if (predictMarkov == null)
			return -1;
		// System.out.println(predictMarkov.length+","+predictCom.length+","+predictSociety.length);
		BufferedReader br = new BufferedReader(new FileReader(new File(fileState)));
		String read = br.readLine();// 仅一行数据，直接得到
		br.close();
		List<Integer> dataTest = new ArrayList<Integer>();// 用于判断预测结果是否正确
		if (read.length() > lagPeriodMax) {
			String[] str = read.split(",");
			for (int i = lagPeriodMax; i < str.length; i++) {
				String[] s = str[i].split(" ");
				dataTest.add(Integer.parseInt(s[2]));// 通过分割得到地点
			}
		}
		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		double rightRate = 0;// 正确率
		for (int i = 0; i < predictMarkov.length; i++) {// 因为之前已经是使用转移链表计算的转移概率，所以可以直接使用概率矩阵的长度进行预测
			double[] max = new double[num];// 存放num个最大的，因为概率可能会存在相等的情况
			int[] next = new int[num];// 存放num个最大的，记录转移概率最大的地点
			if (dataTest.get(i) == 0)
				continue;// 出现0地点，该次不预测

			for (int j = 0; j < predictMarkov[0].length; j++) {
				double newrate = factor
						* (predictSociety[i][j] - 0.5 * predictMarkov[i][j] - 0.25 * predictCom[i][j]
								- 0.25 * predictSoc[i][j])
						+ 0.5 * predictMarkov[i][j] + 0.25 * predictCom[i][j] + 0.25 * predictSoc[i][j];
				for (int k = 0; k < max.length; k++) {
					if (max[k] <= newrate) {
						max[k] = newrate;
						next[k] = j + 1;
						break;
					}
				}
			}
			boolean falg = false;
			for (int k = 0; k < max.length; k++) {
				// System.out.println(k+"m"+max[k]);
				if ((max[0] - max[k] < rate) && (next[k] == dataTest.get(i))) {
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
		if (rightNumber + errorNumber > 4) {// 大于10次的预测进行统计
			rightRate = (double) rightNumber / (rightNumber + errorNumber);
			return rightRate;
		}
		return -1;// 表示没有进行测试,为了统计没有进行测试的个数，方便求平均值。社团修正就不需要了
	}
}
