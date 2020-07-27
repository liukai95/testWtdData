package algorithm;

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
 * 
 * 进行改进 计算任意两个人在所有地点相遇的总时间和次数
 * 输入所有apVisited_i文件，格式USER_ID,SAMPLE_TIME,NEXT_AP_ID输出对应所有地点的时间文件，次数文件
 * 
 * @author Administrator
 * 
 */
public class Area {
	
	private BufferedReader br;
	private PrintWriter pw;
	private PrintWriter pw2;
	private long timeLimit = 300000;// 设置时间限度，一次相遇时间低于5分钟不算相遇

	/**
	 * 计算两个时间的较大者
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public long timeMax(String one, String two) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"MM-dd HH:mm:ss");
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
	public void chuli(int apID, String openFile, String saveFileOne,
			String saveFileTwo) throws Exception {
		Map<String, String> totalPoint = new HashMap<String, String>(); // 在该Ap中的id,以及进来的时间
		br = new BufferedReader(new FileReader(new File(openFile)));
		pw = new PrintWriter(new FileWriter(saveFileOne));
		pw2 = new PrintWriter(new FileWriter(saveFileTwo));
		String read = "";
		long[][] idTime = new long[273][273];
		int[][] idNumber = new int[273][273];
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"MM-dd HH:mm:ss");
		for (int i = 0; i < 273; i++) {
			idTime[i] = new long[273];
			idNumber[i] = new int[273];
		}
		long endTime = 0;// 最终的时间，使用这个时间计算仍旧相遇的人，因为程序计算相遇时间是需要有人离开的前提，最后可能地点还存在id,为了减少误差
		String head = br.readLine();
		// System.out.println(head);
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
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

				} else if (Integer.parseInt(str[2]) == apID) {// 新来一个id,如果下一次还在该地点，否则直接丢弃
					int ii = Integer.parseInt(totalPoint.entrySet().iterator()
							.next().getKey());// 得到原来在该地点的一个id
					int jj = Integer.parseInt(str[0]);
					idNumber[ii][jj] += 1; // 有id进来和原来已经存在该地点的id相遇，次数加1
					idNumber[jj][ii] = idNumber[ii][jj];
					totalPoint.put(str[0], str[1]);

				}
				break;
			default: // 大小大于1时
				if (totalPoint.containsKey(str[0])) { // 包含时该id一定是要移动到其他地点
					long time1 = sdf.parse(str[1]).getTime();
					int ii = Integer.parseInt(str[0]);
					String strTime = totalPoint.get(str[0]);// 得到这个id原来进到这个地点的时间
					totalPoint.remove(str[0]);
					for (String key : totalPoint.keySet()) { // 计算该id和其他人相遇的时间
						long time2 = timeMax(totalPoint.get(key), strTime);
						int jj = Integer.parseInt(key);
						if ((time1 - time2) >= timeLimit) {							
							idTime[ii][jj] = idTime[ii][jj] + (time1 - time2)
									/ 1000;
							idTime[jj][ii] = idTime[ii][jj];
						} else {
							idNumber[ii][jj] -= 1;   //相遇时间小于限度值，之前相加的相遇次数需要去掉                 
							idNumber[jj][ii] = idNumber[ii][jj];
						}

					}
				} else if (Integer.parseInt(str[2]) == apID) { // 新来一个id,如果下一次还在该地点进行计算，否则直接丢弃
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
		}
		List<String> listKey = new ArrayList<String>();
		List<String> listValue = new ArrayList<String>();
		for (String key : totalPoint.keySet()) {
			listKey.add(key);
			listValue.add(totalPoint.get(key));
		}
		for (int i = 0; i < totalPoint.size(); i++) { // 任意两点进行相遇时间增加
			int ii = Integer.parseInt(listKey.get(i));
			for (int j = i + 1; j < totalPoint.size(); j++) {
				int jj = Integer.parseInt(listKey.get(j));
				if ((endTime - timeMax(listValue.get(i), listValue.get(j))) >= timeLimit) {	
					idTime[ii][jj] = idTime[ii][jj]
						+ (endTime - timeMax(listValue.get(i), listValue.get(j)))
						/ 1000;
					idTime[jj][ii] = idTime[ii][jj];
				}else{
					idNumber[ii][jj] -= 1;   //相遇时间小于限度值，之前相加的相遇次数需要去掉                 
					idNumber[jj][ii] = idNumber[ii][jj];
				}

			}
		}
		for (int i = 1; i < 273; i++) {
			for (int j = 1; j < 273; j++) {
				pw.print(idNumber[i][j] + " ");
				pw2.print(idTime[i][j] + " ");
			}
			pw.println();
			pw2.println();
		}
		br.close();
		pw.close();
		pw2.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Area de = new Area();
		try {
			String saveNumber = "F:\\apVisited\\apNumber";
			File file = new File(saveNumber);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			String saveTime = "F:\\apVisited\\apTime";
			File file2 = new File(saveTime);
			// 如果文件夹不存在则创建
			if (!file2.exists() && !file2.isDirectory()) {
				file2.mkdir();
			}

			for (int i = 1; i <= 299; i++) {
				de.chuli(i, "F:\\apVisited\\apVisited_" + i + ".csv",
						saveNumber + "\\apNumber_" + i + ".csv", saveTime
								+ "\\apTime_" + i + ".csv");
			}

		} catch (Exception e) {
		}

	}
}
