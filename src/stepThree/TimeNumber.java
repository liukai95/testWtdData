package stepThree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计算任意两个人在所有地点相遇的总时间和次数
 * 输入所有apVisited_i文件，格式USER_ID,SAMPLE_TIME,NEXT_AP_ID输出对应所有地点的时间文件，次数文件
 * 输出apTime以及apNumber
 * 
 * @author Administrator
 * 
 */
public class TimeNumber {

	private BufferedReader br;
	private PrintWriter pw; //输出总相遇次数
	private PrintWriter pw2; //输出总相遇时间
	private long timeLimit = 300000;// 设置时间限度（毫秒），一次相遇时间低于5分钟不算相遇

	/**
	 * 计算两个时间的较大者
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public long timeMax(String one, String two) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm:ss");
		try {
			long x = sdf.parse(one).getTime();
			long y = sdf.parse(two).getTime();
			if (x > y) {
				return x;
			} else {
				return y;
			}
		} catch (ParseException e) {
		}
		return 0;
	}

	/**
	 * 得到任意两个USER_ID在所有AP相遇的总时间和次数
	 * 
	 * @param apID
	 *            AP编号
	 * @param openFileApVisited
	 *            每个AP被USER_ID访问的数据
	 * @param saveFileApNumber
	 *            保存结果的路径，任意两个USER_ID在所有AP相遇的总次数
	 * @param saveFileApTime
	 *            保存结果的路径，任意两个USER_ID在所有AP相遇的总时间
	 * @throws Exception
	 */
	public void getTN(int apID, String openFileApVisited, String saveFileApNumber, String saveFileApTime)
			throws Exception {
		// 对于分时段的数据而言，每天的数据不是连续的，必须记录是第几天，然后计算这个时段的相遇,初始化为09-22
		String nowDay = "09-22";

		Map<String, String> totalPoint = new HashMap<String, String>(); // 在该Ap中的id,以及进来的时间
		br = new BufferedReader(new FileReader(new File(openFileApVisited)));
		pw = new PrintWriter(new FileWriter(saveFileApNumber));
		pw2 = new PrintWriter(new FileWriter(saveFileApTime));
		String read = "";
		long[][] idTime = new long[246][246];
		int[][] idNumber = new int[246][246];
		// 最终的时间，使用这个时间计算仍旧相遇的人，因为程序计算相遇时间是需要有人离开的前提，最后可能地点还存在id,为了减少误差
		long endTime = 0;
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm:ss");
		for (int i = 0; i < 246; i++) {
			idTime[i] = new long[246];
			idNumber[i] = new int[246];
		}
		String head = br.readLine();
		// System.out.println(head);
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			// 得到日期，比如09-22
			String nowDayTwo = str[1].split(" ")[0];
			// 同一天的数据进行计算
			if (nowDay.equals(nowDayTwo)) {
				// 最终使用的时间将是同一天的最后一个时间，然后计算仍然存在的id的相遇
				endTime = sdf.parse(str[1]).getTime();
				int pointSize = totalPoint.size();
				switch (pointSize) {
				case 0:
					if (Integer.parseInt(str[2]) == apID) {
						totalPoint.put(str[0], str[1]);
					}
					break;
				case 1:
					if (totalPoint.containsKey(str[0])) {
						totalPoint.remove(str[0]);
						// 新来一个id,如果下一次还在该地点，否则直接丢弃
					} else if (Integer.parseInt(str[2]) == apID) {
						// 得到原来在该地点的一个id
						int ii = Integer.parseInt(totalPoint.entrySet().iterator().next().getKey());
						int jj = Integer.parseInt(str[0]);
						// 有id进来和原来已经存在该地点的id相遇，次数加1
						idNumber[ii][jj] += 1;
						idNumber[jj][ii] = idNumber[ii][jj];
						totalPoint.put(str[0], str[1]);
					}
					break;
				default: // 大小大于1时
					// 包含时该id一定是要移动到其他地点
					if (totalPoint.containsKey(str[0])) {
						long time1 = sdf.parse(str[1]).getTime();
						int ii = Integer.parseInt(str[0]);
						// 得到这个id原来进到这个地点的时间
						String strTime = totalPoint.get(str[0]);
						totalPoint.remove(str[0]);
						// 计算该id和其他人相遇的时间
						for (String key : totalPoint.keySet()) {
							long time2 = timeMax(totalPoint.get(key), strTime);
							int jj = Integer.parseInt(key);
							if ((time1 - time2) >= timeLimit) {
								idTime[ii][jj] = idTime[ii][jj] + (time1 - time2) / 1000;
								idTime[jj][ii] = idTime[ii][jj];
							} else {
								// 相遇时间小于限度值，之前相加的相遇次数需要去掉
								idNumber[ii][jj] -= 1;
								idNumber[jj][ii] = idNumber[ii][jj];
							}
						}
						// 新来一个id,如果下一次还在该地点进行计算，否则直接丢弃
					} else if (Integer.parseInt(str[2]) == apID) {
						// 新进来的id和其他id相遇次数加1
						int id = Integer.parseInt(str[0]);
						for (String key : totalPoint.keySet()) {
							int ii = Integer.parseInt(key);
							idNumber[id][ii] += 1;
							idNumber[ii][id] = idNumber[id][ii];
						}
						totalPoint.put(str[0], str[1]);
					}
					break;
				}
				// 如果是下一天的数据时，先计算仍然存在的id的相遇时间，然后清空该地点的数据，更新nowDay
			} else {
				// 得到新的日期
				nowDay = str[1].split(" ")[0];
				// System.out.println(apID+" "+nowDay);
				List<String> listKey = new ArrayList<String>(); // 保存所有的键值，即id
				List<String> listValue = new ArrayList<String>(); // 保存所有的值，即id进入的时间
				for (String key : totalPoint.keySet()) {
					listKey.add(key);
					listValue.add(totalPoint.get(key));
				}
				// 任意两点进行相遇时间的增加
				for (int i = 0; i < totalPoint.size(); i++) {
					int ii = Integer.parseInt(listKey.get(i));
					for (int j = i + 1; j < totalPoint.size(); j++) {
						int jj = Integer.parseInt(listKey.get(j));
						if ((endTime - timeMax(listValue.get(i), listValue.get(j))) >= timeLimit) {
							idTime[ii][jj] = idTime[ii][jj]
									+ (endTime - timeMax(listValue.get(i), listValue.get(j))) / 1000;
							idTime[jj][ii] = idTime[ii][jj];
						} else {
							// 相遇时间小于限度值，之前相加的相遇次数需要去掉
							idNumber[ii][jj] -= 1;
							idNumber[jj][ii] = idNumber[ii][jj];
						}
					}
				}

				totalPoint.clear();
				// 重新计算新一天的数据
				if (Integer.parseInt(str[2]) == apID) {
					totalPoint.put(str[0], str[1]);
				}
			}
		}

		List<String> listKey = new ArrayList<String>(); // 保存所有的键值，即id
		List<String> listValue = new ArrayList<String>(); // 保存所有的值，即id进入的时间
		for (String key : totalPoint.keySet()) {
			listKey.add(key);
			listValue.add(totalPoint.get(key));
		}
		// 任意两点进行相遇时间的增加
		for (int i = 0; i < totalPoint.size(); i++) {
			int ii = Integer.parseInt(listKey.get(i));
			for (int j = i + 1; j < totalPoint.size(); j++) {
				int jj = Integer.parseInt(listKey.get(j));
				if ((endTime - timeMax(listValue.get(i), listValue.get(j))) >= timeLimit) {
					idTime[ii][jj] = idTime[ii][jj] + (endTime - timeMax(listValue.get(i), listValue.get(j))) / 1000;
					idTime[jj][ii] = idTime[ii][jj];
				} else {
					// 相遇时间小于限度值，之前相加的相遇次数需要去掉
					idNumber[ii][jj] -= 1;
					idNumber[jj][ii] = idNumber[ii][jj];
				}

			}
		}
		// 输出结果
		for (int i = 1; i < 246; i++) {
			for (int j = 1; j < 246; j++) {
				pw.print(idNumber[i][j] + " ");
				pw2.print(idTime[i][j] + " ");
			}
			pw.println();
			pw2.println();

		}		
		pw.close();
		pw2.close();
		br.close();
	}

}
