package stepOne;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * 计算每个用户的数据总条数，低于100条的用户删除，进行重新编号，最开始进行
 * 
 * @author Administrator
 * 
 */
public class Demo0NewData {

	private Map<String, Integer> strList;// 记录每个用户出现的数据条数
	private BufferedReader br;
	private PrintWriter pw;
	private int numberLimit = 100;// 数据最少的个数

	/**
	 * 
	 * @param openFile
	 *            源文件
	 * @param saveFile
	 *            处理后的文件
	 * @throws Exception
	 */
	public void testNumber(String openFile, String saveFile) throws Exception {
		// 键->USER_ID,使用字符串，无需转成整形；值->出现的条数
		strList = new HashMap<String, Integer>();
		br = new BufferedReader(new FileReader(new File(openFile)));
		pw = new PrintWriter(new FileWriter(saveFile));
		String head = br.readLine();// 头信息
		System.out.println(head);
		String read = "";
		long sum = 0;// 处理后的数据数目

		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			// 只存放设备连接到信号的数据
			if (str[5].equals("1")) {
				sum++;
				String key = str[0];
				if (strList.containsKey(key)) {
					int value = strList.get(str[0]);
					value++;
					strList.put(str[0], value);

				} else {
					strList.put(key, 1);
				}
			}
		}
		br.close();

		List<Integer> list = new ArrayList<Integer>();
		// 对处理过的USER_ID重新编号
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		System.out.println(sum);
		// 遍历Map，只保留大于numberLimit条的数据
		for (Map.Entry<String, Integer> entry : strList.entrySet()) {
			if (entry.getValue() > numberLimit) {
				list.add(Integer.parseInt(entry.getKey()));
			}
		}
		Collections.sort(list);// 对链表按照USER_ID进行排序
		int i = 1;
		// 对用户重新编号
		for (int key : list) {
			map.put(key, i);
			i++;
		}
		br = new BufferedReader(new FileReader(new File(openFile)));
		head = br.readLine();
		pw.println(head);
		System.out.println(head);
		read = "";
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			if (map.containsKey(Integer.parseInt(str[0]))) {
				pw.println(map.get(Integer.parseInt(str[0])) + "," + str[1] + "," + str[2] + "," + str[3] + "," + str[4]
						+ "," + str[5]);
			}

		}
		pw.close();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Demo0NewData de = new Demo0NewData();
		try {
			de.testNumber("F:/移动轨迹数据/data.csv", "F:/移动轨迹数据/newData.csv");
		} catch (Exception e) {
		}

	}

}
