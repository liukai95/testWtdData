package stepThree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用重新编号的ap对测试的数据进行修改，并且带NEXT_AP_ID，方便进行比较
 * 
 * @author Administrator
 * 
 */
public class OneSignalResult {
	
	private BufferedReader br;
	private PrintWriter pw;
	private Map<String, String> newApMap;

	/**
	 * 
	 * @param openFileNewAp 重新编号后的AP数据文件
	 * @param openFileRemove 只保存一个连接信号并且去重的数据
	 * @param saveFileNextAP 带NEXT_AP_ID的数据
	 * @throws Exception
	 */
	public void getNextAPDate(String openFileNewAp, String openFileRemove,
			String saveFileNextAP) throws Exception {
		newApMap = new LinkedHashMap<String, String>();// 存放新的AP
		Map<String, List<String>> nextMap = new HashMap<String, List<String>>();
		br = new BufferedReader(new FileReader(new File(openFileNewAp)));
		String read = "";
		// 得到新AP
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			String[] strInner = str[1].split(" ");
			for (int i = 0; i < strInner.length; i++) {
				newApMap.put(strInner[i], str[0]);
			}
		}
		br.close();

		// 得到原始数据，然后选择新的AP，进行重新编号
		br = new BufferedReader(new FileReader(new File(openFileRemove)));
		pw = new PrintWriter(new FileWriter(saveFileNextAP));
		String head = br.readLine();
		//System.out.println(head);
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			String newStr = "";
			if (newApMap.containsKey(str[2])) {
				newStr = str[0] + "," + str[1] + "," + newApMap.get(str[2])
						+ "," + str[3] + "," + str[4] + "," + str[5];
			} else {// 测试数据中存在使用数据中没有访问过的地点,地点全部当做0地点
				newStr = str[0] + "," + str[1] + "," + 0 + "," + str[3] + ","
						+ str[4] + "," + str[5];
			}
			// 先把数据按照id存放在map中，然后可以得到下一个移动的地点
			if (nextMap.containsKey(str[0])) {
				nextMap.get(str[0]).add(newStr);

			} else {
				List<String> list = new ArrayList<String>();
				list.add(newStr);
				nextMap.put(str[0], list);
			}
		}
		//System.out.println("Load!");
		String tale = "NEXT_AP_ID";
		pw.println(head + "," + tale);
		//输出文件得到带NEXT_AP_ID并且重新编号的数据
		for (int i = 1; i <= 245; i++) {
			if (nextMap.containsKey("" + i)) {
				List<String> lis = nextMap.get("" + i);
				for (int j = 0; j < lis.size() - 1; j++) {
					String[] s = lis.get(j + 1).split(",");
					pw.println(lis.get(j) + "," + s[2]);
				}
			}
		}		
		pw.close();
		br.close();
	}

}
