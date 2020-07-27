package markovAndSociety;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 原有方法
 * 根据社团计算转移到下一点,输入需要预测的ID以及time
 * 
 * 需要的数据有apTotalTime.txt，社团结果RESULT.txt，每个地点的apTime_i,
 * 测试数据的peopleIDNextApRemove_i,matrix_i
 */
public class SocietyPredict {

	private BufferedReader br;
	private int[][][] idTime; // 需要使用的任意两个USER_ID在所有地点相遇的时间,使用int类型,减少内存,1地点，2和3是用户，值为时间
	private String[][] totalTime;// 需要使用的任意两个USER_ID在所有地点相遇的总时间，1和1是用户，值为总时间
	private String[][] myMatrix;// 为了得出比较的节点继续留在该地点的概率，存放的所有id停留在所有地点的概率矩阵,1用户，2地点，值为停留概率
	private boolean flag;

	/**
	 * 初始化任意两个用户总相遇时间，以及所有用户停留在所有地点的概率矩阵
	 * 
	 * @param count
	 *            转移AP的个数
	 * @param fileMatrix
	 *            概率转移矩阵matrix.csv
	 * @param fileApTotalTime
	 *            任意两个USER_ID在所有地点相遇的总时间文件
	 * @param fileApTime
	 *            任意两个USER_ID在所有地点相遇的时间文件
	 * @throws Exception
	 */
	public void init(int count, String fileMatrix, String fileApTotalTime, String fileApTime) throws Exception {
		idTime = new int[count][245][245];
		totalTime = new String[245][245];

		// 初始化apTotalTime
		br = new BufferedReader(new FileReader(new File(fileApTotalTime)));
		String read = "";
		int i = 0;
		// 可去掉减少计算量
		// while ((read = br.readLine()) != null) {
		// totalTime[i] = read.split(",");
		// i++;
		// }
		// br.close();

		br = new BufferedReader(new FileReader(new File(fileMatrix + "\\matrix.csv")));
		myMatrix = new String[245][count];
		read = "";
		i = 0;
		while ((read = br.readLine()) != null) {
			myMatrix[i] = read.split("( )+");
			i++;
		}
		br.close();

		// 计算预测节点与节点在所有地点相遇的概率，初始化idTime
		for (int fileI = 1; fileI <= count; fileI++) {
			br = new BufferedReader(new FileReader(new File(fileApTime + "\\apTime\\apTime_" + fileI + ".csv")));
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
			} // 得到在point点的矩阵
			br.close();
		}

	}

	/**
	 * 进行预测，参数为预测用户、预测的带时间的马尔科夫链、任意两个用户在某地点的相遇时间矩阵、所有用户的测试转移数据、社团分配结果
	 * 
	 * @param testID
	 *            USER_ID编号
	 * @param fileState
	 *            USER_ID的转移AP的马尔科夫链
	 * @param count
	 *            转移AP的个数
	 * @param fileTestIDNextRemove
	 *            USER_ID的移动数据
	 * @param fileSociety
	 *            社团划分文件
	 * @param lagPeriodMax
	 *            预测的最大马尔科夫阶数
	 * @return 社团修正预测每次转移时转移到所有AP的概率数组
	 * @throws Exception
	 */
	public double[][] predictTotal(int testID, String fileState, int count, String fileTestIDNextRemove,
			String fileSociety, int lagPeriodMax) throws Exception {
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
				if (value == testID) { // 记录所在的社团
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

		br = new BufferedReader(new FileReader(new File(fileState)));
		read = br.readLine();// 仅一行数据，直接得到
		br.close();
		String[] time = null;// 相应地点的时间
		double[][] predictValueSociety;
		if (read.length() > lagPeriodMax) {// 有数据
			String[] str = read.split(",");
			time = new String[str.length];// 存放所有的时间
			for (int i = 0; i < str.length; i++) {
				String[] s = str[i].split(" ");
				time[i] = s[0] + " " + s[1];
			}
			predictValueSociety = new double[str.length - lagPeriodMax][count];// 存放所有对于测试数据的概率,减去最大步数开始，与马尔科夫预测对应
		} else
			return null;

		if (flag) {// 有社团，进行预测
			// 社团成员过多时为了计算速度只选择10个人修正
			Random random = new Random();
			while (list.size() > 10) {
				int num = random.nextInt(list.size());
				// System.out.println(list.size()+" "+num);
				list.remove(num);
			}
			for (int timeI = lagPeriodMax; timeI < time.length; timeI++) {// 预测的时间从步数开始
				Map<Integer, Integer> idPoint = new HashMap<Integer, Integer>();// 表示此时有成员在某地点，键为用户，值为地点，无需循环
				int timeSum = 0;// 所有相遇的总和
				for (int ii : list) {
					// System.out.println(ii);
					br = new BufferedReader(
							new FileReader(new File(fileTestIDNextRemove + "\\peopleIDNextApRemove_" + ii + ".csv")));
					head = br.readLine();
					// System.out.println(head);
					String beforePoint = "";// 前一个地点
					String beforeTime = "";// 前一个时间

					if ((read = br.readLine()) != null) {
						String[] str = read.split(",");
						beforePoint = str[1];// 修改上一个地点
						beforeTime = str[0];// 修改上一个时间
					}
					while ((read = br.readLine()) != null) {// 找到第一个时间大于测试时间的数据，str[0]是上一个
															// // 时间
						String[] str = read.split(",");
						if (str[0].compareTo(time[timeI]) > 0 && beforeTime.compareTo(time[timeI]) <= 0) {
							if (beforePoint.equals(str[1])) {// 前后两个地点相同
								int point = Integer.parseInt(beforePoint);
								// System.out.println(ii + " " + "该点此时在这个地点"
								// + point+time[timeI]);
								if (point == 0)
									break;

								timeSum += idTime[point - 1][ii - 1][testID - 1];
								idPoint.put(ii, point);
							} else {
								break;
							}
						}
						beforePoint = str[1];// 修改上一个地点
						beforeTime = str[0];// 修改上一个时间
					}
					br.close();
				}
				java.util.Iterator<Integer> it = idPoint.keySet().iterator();
				while (it.hasNext()) {// 计算去其他地点的概率
					int key = it.next();
					int point = idPoint.get(key);
					// predictValueSociety[timeI][point - 1] += (double)
					// idTime[point - 1][key - 1][testID - 1]
					// / timeSum
					// * (idTime[point - 1][key - 1][testID - 1]
					// / Double.parseDouble(totalTime[key - 1][testID - 1]) /
					// Double
					// .parseDouble(myMatrix[key - 1][point - 1]));//
					// 前面为权值，后面括号为条件概率
					predictValueSociety[timeI - lagPeriodMax][point
							- 1] += (double) idTime[point - 1][key - 1][testID - 1] / timeSum
									/ Double.parseDouble(myMatrix[key - 1][point - 1]);// 不使用总时间，得到结果相差不错，可减少计算量

				}

			}

		} else {
			// System.out.println(testID + "没有社团");
			return predictValueSociety;// 没有社团无需计算
		}
		return predictValueSociety;// 全都是0的数据
	}

	/**
	 * 分段数据 进行预测，参数为预测用户、预测的带时间的马尔科夫链、任意两个用户在某地点的相遇时间矩阵、所有用户的测试转移数据、社团分配结果
	 * 
	 * @param testID
	 *            USER_ID编号
	 * @param fileState
	 *            USER_ID的转移AP的马尔科夫链
	 * @param count
	 *            转移AP的个数
	 * @param fileTestIDNextRemove
	 *            USER_ID的移动数据
	 * @param fileSociety
	 *            社团划分文件
	 * @param lagPeriodMax
	 *            预测的最大马尔科夫阶数
	 * @return 社团修正预测每次转移时转移到所有AP的概率数组
	 * @throws Exception
	 */
	public double[][] predictSplit(int testID, String fileState, int count, String fileTestIDNextRemove,
			String fileSociety, int lagPeriodMax) throws Exception {
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
				if (value == testID) { // 记录所在的社团
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

		br = new BufferedReader(new FileReader(new File(fileState)));
		read = "";
		List<String> time = new ArrayList<String>();// 需要进行预测的时间链表
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			if (str.length > lagPeriodMax) {// 只有大于最大步数才有计算的必要
				for (int i = lagPeriodMax; i < str.length; i++) {// 从最大步数开始预测时间，和马尔科夫链预测对应
					String[] s = str[i].split(" ");
					time.add(s[0] + " " + s[1]);// 通过分割得到时间
				}
			}
		}
		br.close();

		double[][] predictValueSociety = new double[time.size()][count];// 存放所有对于测试数据的概率
		if (flag && time.size() > 0) {// 有社团，进行预测
			// 社团成员过多时为了计算速度只选择10个人修正
			Random random = new Random();
			while (list.size() > 10) {
				int num = random.nextInt(list.size());
				// System.out.println(list.size()+" "+num);
				list.remove(num);
			}
			int timeIndex = 0;// 时间的索引
			for (String predictTime : time) {// 依次遍历所有需要预测的时间

				Map<Integer, Integer> idPoint = new HashMap<Integer, Integer>();// 表示此时有成员在某地点，键为用户，值为地点，无需循环
				int timeSum = 0;// 所有相遇的总和
				for (int ii : list) {
					br = new BufferedReader(
							new FileReader(new File(fileTestIDNextRemove + "\\peopleIDNextApRemove_" + ii + ".csv")));
					head = br.readLine();
					// System.out.println(head);
					String beforePoint = "";// 前一个地点
					String beforeTime = "";// 前一个时间

					if ((read = br.readLine()) != null) {
						String[] str = read.split(",");
						beforePoint = str[1];// 修改上一个地点
						beforeTime = str[0];// 修改上一个时间
					}
					while ((read = br.readLine()) != null) {// 找到第一个时间大于测试时间的数据，str[0]是上一个
															// // 时间
						String[] str = read.split(",");
						if (str[0].compareTo(predictTime) > 0 && beforeTime.compareTo(predictTime) <= 0) {
							if (beforePoint.equals(str[1])) {// 前后两个地点相同
								int point = Integer.parseInt(beforePoint);
								// System.out.println(ii + " " + "该点此时在这个地点"
								// + point);
								if (point == 0)
									break;

								timeSum += idTime[point - 1][ii - 1][testID - 1];
								idPoint.put(ii, point);
							} else {
								break;
							}
						}
						beforePoint = str[1];// 修改上一个地点
						beforeTime = str[0];// 修改上一个时间
					}
					br.close();
				}
				java.util.Iterator<Integer> it = idPoint.keySet().iterator();
				while (it.hasNext()) {// 计算去其他地点的概率
					int key = it.next();
					int point = idPoint.get(key);
					// predictValueSociety[timeIndex][point - 1] += (double)
					// idTime[point - 1][key - 1][testID - 1]
					// / timeSum
					// * (idTime[point - 1][key - 1][testID - 1]
					// / Double.parseDouble(totalTime[key - 1][testID - 1]) /
					// Double
					// .parseDouble(myMatrix[key - 1][point - 1]));//
					// 前面为权值，后面括号为条件概率
					predictValueSociety[timeIndex][point - 1] += (double) idTime[point - 1][key - 1][testID - 1]
							/ timeSum / Double.parseDouble(myMatrix[key - 1][point - 1]);// 不使用总时间，得到结果相差不错，可减少计算量
				}

			}
			timeIndex++;

		} else {
			// System.out.println(testID + "没有社团");
			return predictValueSociety;// 没有社团无需计算
		}
		return predictValueSociety;
	}

}
