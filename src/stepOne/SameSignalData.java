package stepOne;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 得到一个用户在同一时间同时收到的ap 输入原始数据输出sameSignal文件
 * 
 * @author Administrator
 * 
 */
public class SameSignalData {

	private BufferedReader br;
	private PrintWriter pw;

	/**
	 * 
	 * @param openFileDemo
	 *            原实验数据
	 * @param saveFileSameSignalRemove
	 *            去重过后每个用户在同一时间接收的AP数据
	 * @param demoSum
	 *            实验数据的个数
	 * @throws Exception
	 */

	public void getSignalData(String openFileDemo, String saveFileSameSignalRemove, int demoSum) throws Exception {
		Set<String> set = new LinkedHashSet<String>();
		br = new BufferedReader(new FileReader(new File(openFileDemo)));
		pw = new PrintWriter(new FileWriter(saveFileSameSignalRemove));
		String head = br.readLine();
		String read = br.readLine();// 第一行数据
		int i = 0;
		do {
			String[] str = read.split(",");
			String time = str[0] + "," + str[1];
			// System.out.println(time);
			String apID = str[2];
			while ((read = br.readLine()) != null) {
				i++;
				String[] str2 = read.split(",");
				String time2 = str2[0] + "," + str2[1];
				// 判断是不是同一时间的数据
				if (!time.equals(time2)) {
					break;
				}
				apID = apID + "," + str2[2];

			}
			// 使用Set防止重复数据
			set.add(apID);

		} while (i < demoSum - 2); // 防止越界，用数据的条数
		// 用于清除重复数据
		Set<String> removeRepateSet = new LinkedHashSet<String>();
		for (String value : set) {
			String[] str = value.split(","); // 注意是空格还是逗号
			// 对得到的相同信号的数据去除重复的点，方便计算同时收到的概率
			for (String s : str) {
				removeRepateSet.add(s);
			}
			String ss = "";
			for (String s : removeRepateSet) {
				ss = ss + "," + s;
			}
			ss = ss.substring(1, ss.length());
			pw.println(ss);
			removeRepateSet.clear();
		}
		br.close();
		pw.close();
	}

}
