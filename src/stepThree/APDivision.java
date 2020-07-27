package stepThree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APDivision2程序Java实现 输入oneSignalTwoNextApRemoveSort 输出apVisited_i文件
 * 得到每个地点被用户访问的数据，从而计算两者相遇
 */
public class APDivision {

	private BufferedReader br;
	private PrintWriter pw;

	/**
	 * 
	 * @param newAPnum
	 *            合并的权重
	 * @param openFileRemoveSort
	 *            按照时间进行排序并且移除了用户停留在AP的数据
	 * @param saveFileApVisited
	 *            保存结果的路径，输出apVisited_i，可以得到每个AP被USER_ID访问的数据
	 * @throws Exception
	 */
	public void apDivision(int newAPnum, String openFileRemoveSort, String saveFileApVisited) throws Exception {
		br = new BufferedReader(new FileReader(new File(openFileRemoveSort)));
		Map<String, List<String>> strList = new HashMap<String, List<String>>();
		String head = br.readLine();
		// System.out.println(head);
		String read = "";
		// 按照ap存放数据
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			String key = str[2]; // ap编号
			if (strList.containsKey(key)) {
				strList.get(key).add(str[0] + "," + str[1] + "," + str[6]); // 可以之存储着几项数据

			} else {
				List<String> list = new ArrayList<String>();
				list.add(str[0] + "," + str[1] + "," + str[6]);
				strList.put(key, list);
			}
		}

		// System.out.println("Load!");
		File file = new File(saveFileApVisited);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		String newHead = "USER_ID,SAMPLE_TIME,NEXT_AP_ID";
		// 输出结果
		for (int i = 1; i <= newAPnum; i++) {
			pw = new PrintWriter(new FileWriter(saveFileApVisited + "\\apVisited_" + i + ".csv"));
			pw.println(newHead);
			if (strList.containsKey("" + i)) {// 是否存在
				List<String> lis = strList.get("" + i);
				for (String s : lis) {
					pw.println(s);
				}
			}
			pw.close();
		}
		br.close();
	}

}
