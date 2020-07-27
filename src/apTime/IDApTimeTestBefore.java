package apTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 对停留时间进行测试,进行改进，保留是从哪一个地点转移过来的，使用相似轨迹进行预测
 * 使用Map只存放需要的数据，替代数组，减少了空间的使用
 */
public class IDApTimeTestBefore {
	
	private BufferedReader br;

	/**
	 * 
	 * @param apTime USER_ID在转移到的AP的停留时间文件
	 * @param testApTime USER_ID在转移到的AP的停留时间的测试文件
	 * @return 预测正确率
	 * @throws Exception
	 */
	public double apTimeRightRate(String apTime, String testApTime)
			throws Exception {
		br = new BufferedReader(new FileReader(new File(apTime)));

		Map<String, Integer> oldMap = new HashMap<String, Integer>();// 实验数据
		Map<String, Integer> newMap = new HashMap<String, Integer>();// 测试数据
		Map<String, Map<String, Integer>> oldMapSplit = new HashMap<String, Map<String, Integer>>();// 实验数据,保留上一个地点
		Map<String, Map<String, Integer>> newMapSplit = new HashMap<String, Map<String, Integer>>();// 实验数据,保留上一个地点
		String head = br.readLine();
		// System.out.println(head);
		int[] totalNum = new int[400];// 测试ap对应出现的次数，使用400大于新编号的ap数，防止出界
		int[][] splitNum = new int[400][400];// 行为上一个地点，测试从上一个ap转移到下一个ap所对应出现的次数，为了求平均值
		String beforeAp = "";
		String read = "";
		if ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			beforeAp = str[1];
		}
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			Map<String, Integer> map;
			if (oldMapSplit.containsKey(beforeAp)) {
				map = oldMapSplit.get(beforeAp);// 得到对应map
			} else {
				map = new HashMap<String, Integer>();// 中间容器，作用和oldMap相似
			}
			if (Integer.parseInt(str[2]) < 86400) {// 时间小于1天
				splitNum[Integer.parseInt(beforeAp) - 1][Integer
						.parseInt(str[1]) - 1]++;
				if (map.containsKey(str[1])) {
					int value = map.get(str[1]) + Integer.parseInt(str[2]);
					map.put(str[1], value);
				} else {
					map.put(str[1], Integer.parseInt(str[2]));
				}
				oldMapSplit.put(beforeAp, map);// 重新存放结果

				// 计算总的，不考虑上一个地点
				totalNum[Integer.parseInt(str[1]) - 1]++;
				if (oldMap.containsKey(str[1])) {
					int value = oldMap.get(str[1]) + Integer.parseInt(str[2]);
					oldMap.put(str[1], value);
				} else {
					oldMap.put(str[1], Integer.parseInt(str[2]));
				}
			}

			beforeAp = str[1];// 更新上一个地点

		}
		br.close();
		// 依次遍历，求平均值
		Iterator<String> it = oldMapSplit.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Map<String, Integer> innerMap = oldMapSplit.get(key);
			Map<String, Integer> map = new HashMap<String, Integer>();// 中间数据

			// 遍历内部map
			Iterator<String> in = innerMap.keySet().iterator();
			while (in.hasNext()) {
				String inKey = in.next();
				int value = innerMap.get(inKey)
						/ splitNum[Integer.parseInt(key) - 1][Integer
								.parseInt(inKey) - 1];
				map.put(inKey, value);
			}
			newMapSplit.put(key, map);// 构造新map，存放平均值
		}

		// 求不带上一个地点的停留时间的平均值
		it = oldMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			int value = oldMap.get(key) / totalNum[Integer.parseInt(key) - 1];
			newMap.put(key, value);
		}
		
		//进行测试

		br = new BufferedReader(new FileReader(new File(testApTime)));
		head = br.readLine();
		if ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			beforeAp = str[1];

		}
		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			String key = str[1];
			if (newMapSplit.containsKey(beforeAp)
					&& Integer.parseInt(str[2]) < 86400) {
				Map<String, Integer> innerMap = newMapSplit.get(beforeAp);// 得到内部map，接下来的内容和不考虑上一个地点的计算方法
				if (innerMap.containsKey(key)) {
					int value = innerMap.get(key);
					if (Math.abs(value - Integer.parseInt(str[2])) < 6000) {// 十分钟误差认为正确
						rightNumber++;
					} else {
						errorNumber++;

					}
				}

			} else if (newMap.containsKey(key)
					&& Integer.parseInt(str[2]) < 86400)// 出现新的上一个ap，则只能使用总的平均值进行预测
			{
				int value = newMap.get(key);
				if (Math.abs(value - Integer.parseInt(str[2])) < 600) {// 十分钟误差认为正确
					rightNumber++;
				} else {
					errorNumber++;
				}
			}
			beforeAp = str[1];// 更新上一个地点
		}

		double rightRate = 0;
		if (rightNumber + errorNumber > 0) {
			rightRate = (double) rightNumber / (rightNumber + errorNumber);
			return rightRate;
		} else {
			return -1;// 表示没有数据或不满足条件，无法进行预测
		}
		// System.out.println("正确率" + rightRate);

	}

	public static void main(String[] args) {
		IDApTimeTestBefore att = new IDApTimeTestBefore();
		double[] apTimRightRate = new double[245];
		PrintWriter pw;
		try {
			// 总数据
			pw = new PrintWriter(new FileWriter(
					"F:\\markovTest\\apTimeRightRate.csv"));
			for (int i = 1; i <= 1; i++) {
				apTimRightRate[i - 1] = att.apTimeRightRate(
						"F:\\移动轨迹数据\\result\\idApTime\\idApTime_" + i + ".csv",
						"F:\\移动轨迹数据\\idApTimeTest\\idApTime_" + i + ".csv");
				pw.println(apTimRightRate[i - 1]);
			}
			pw.close();

			// 分段数据
			for (int projectI = 1; projectI <= 6; projectI++) {
				pw = new PrintWriter(new FileWriter(
						"F:\\markovTest\\apTimeRightRate" + projectI + ".csv"));
				for (int i = 1; i <= 245; i++) {
					apTimRightRate[i - 1] = att.apTimeRightRate(
							"F:\\移动轨迹数据\\timeDivide\\result" + projectI
									+ "\\idApTime\\idApTime_" + i + ".csv",
							"F:\\移动轨迹数据\\timeDivideTest\\idApTimeTest"
									+ projectI + "\\idApTime_" + i + ".csv");
					pw.println(apTimRightRate[i - 1]);
				}
				pw.close();

			}

		} catch (Exception e) {
		}
	}
}
