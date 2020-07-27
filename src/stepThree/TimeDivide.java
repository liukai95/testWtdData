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
 * 对测试的数据进行时段的划分,划分为6段，每段占用四个小时 输入oneSignalTwoNextAp以及需要分的时段输出timeNextAp_i
 * 
 * @author Administrator
 * 
 */
public class TimeDivide {
	
	private BufferedReader br;
	private PrintWriter pw;

	/**
	 * 
	 * @param timeStr 进行分段的时间段数组
	 * @param openFileNextAp 带NEXT_AP_ID的数据
	 * @param saveFileTD 保存分段结果的路径
	 * @throws Exception
	 */
	public void divide(String[] timeStr,
			String openFileNextAp, String saveFileTD) throws Exception {
		Map<Integer, List<String>> strList = new HashMap<Integer, List<String>>(); // 存放分时段的数据
		br = new BufferedReader(new FileReader(new File(openFileNextAp)));
		String head = br.readLine();
		// System.out.println(head);
		String read = "";

		// 初始化，使用map,值为一个链表存放相关数据
		for (int i = 0; i < timeStr.length; i++) {// timeStr.length为总划分的段数
			List<String> list = new ArrayList<String>();
			strList.put(i, list);
		}
		boolean flag = false;
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			// 得到时分秒数据；
			String[] keyStr = str[1].split(" "); 
			String key = keyStr[1];
			// System.out.println(key);
			// 判断属于那一段，切记要考虑0点，因此需要分成两段进行判断
			for (int i = 0; i < timeStr.length - 1; i++) {
				if (key.compareTo(timeStr[i]) > 0
						&& key.compareTo(timeStr[i + 1]) <= 0) {
					strList.get(i).add(read);
					flag = true;
				}
			}
			if (flag) {
				strList.get(timeStr.length - 1).add(read);
				flag=false;
			}
		}
		// System.out.println("Load");
		File file = new File(saveFileTD);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		// 输出结果
		for (int i = 0; i < timeStr.length; i++) {
			pw = new PrintWriter(new FileWriter(saveFileTD + "\\timeNextAp_"
					+ (i + 1) + ".csv"));
			pw.println(head);
			for (String s : strList.get(i)) {
				pw.println(s);
			}
			pw.close();
		}
		br.close();
	}

}
