package stepThree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class IDDivisionRemove {

	private BufferedReader br;
	private PrintWriter pw;// 输出整的文件
	private PrintWriter pw2;// 输出分开的文件

	/**
	 * 
	 * @param openFileNextAp
	 *            带NEXT_AP_ID的数据
	 * @param saveFileRemoveSort
	 *            按照时间进行排序并且移除了用户停留在AP的数据
	 * @param saveFileRemoveI
	 *            保存结果的路径，输出peopleIDNextApRemove_i，可以得到每个USER_ID的移动过程
	 * @throws Exception
	 */
	public void totalAddSplitFile(String openFileNextAp, String saveFileRemoveSort, String saveFileRemoveI)
			throws Exception {
		br = new BufferedReader(new FileReader(new File(openFileNextAp)));
		pw = new PrintWriter(new FileWriter(saveFileRemoveSort));
		Map<String, List<String>> strList = new HashMap<String, List<String>>();
		Map<String, String> treeMap = new TreeMap<String, String>();// 对总数据按照每个id移除相同地点后的数据按照时间进行排序，方便后面进行apDivision
		String head = br.readLine();
		// System.out.println(head);
		String read = "";
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			String key = str[0];
			if (strList.containsKey(key)) {
				strList.get(key).add(read);
			} else {
				List<String> list = new ArrayList<String>();
				list.add(read);
				strList.put(key, list);
			}
		}
		// System.out.println("Load!");
		File file = new File(saveFileRemoveI);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		String newHead = "SAMPLE_TIME,AP_ID,NEXT_AP_ID";
		// pw2输出每个分文件，总数据放入treeMap进行排序
		for (int i = 1; i <= 245; i++) {
			{
				pw2 = new PrintWriter(new FileWriter(saveFileRemoveI + "\\peopleIDNextApRemove_" + i + ".csv"));
				pw2.println(newHead);
				if (strList.containsKey("" + i)) {
					List<String> list = strList.get("" + i);
					String readTwo = list.get(0);
					int j = 1;
					do {
						String[] s = readTwo.split(",");// 使用有用的列输出到文件中
						treeMap.put(s[1] + s[0], readTwo); // 时间有重复，加上s[0]避免重复
						pw2.println(s[1] + "," + s[2] + "," + s[6]);
						String[] str = readTwo.split(",");
						String id = str[2];
						while (j < list.size() - 1) {
							readTwo = list.get(j);
							String[] str2 = readTwo.split(",");
							String id2 = str2[2];
							String readBefore = list.get(j - 1);
							if (!id.equals(id2)) {
								String[] ss = readBefore.split(",");// 使用有用的列输出到文件中
								treeMap.put(ss[1] + ss[0], readBefore); // 时间有重复，加上ss[0]避免重复
								pw2.println(ss[1] + "," + ss[2] + "," + ss[6]);
								break;
							}
							j++;
						}

					} while (j < list.size() - 2);
				}
				pw2.close();
			}
		}
		// 输出oneSignalTwoNextApRemoveSort
		pw.println(head);
		Iterator<String> it = treeMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = treeMap.get(key);
			pw.println(value);
		}
		br.close();
		pw.close();
	}

	/**
	 * 只输出分文件，测试数据调用该方法
	 * 
	 * @param openFileNextAp
	 *            带NEXT_AP_ID的数据
	 * @param saveFileRemoveI
	 *            保存结果的路径，输出peopleIDNextApRemove_i，可以得到每个USER_ID的移动过程
	 * @throws Exception
	 */
	public void splitFile(String openFileNextAp, String saveFileRemoveI) throws Exception {
		br = new BufferedReader(new FileReader(new File(openFileNextAp)));
		Map<String, List<String>> strList = new HashMap<String, List<String>>();
		String head = br.readLine();
		// System.out.println(head);
		String read = "";
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			String key = str[0];
			if (strList.containsKey(key)) {
				strList.get(key).add(read);

			} else {
				List<String> list = new ArrayList<String>();
				list.add(read);
				strList.put(key, list);
			}
		}
		// System.out.println("Load!");
		File file = new File(saveFileRemoveI);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		String newHead = "SAMPLE_TIME,AP_ID,NEXT_AP_ID";
		for (int i = 1; i <= 245; i++) {
			{
				pw = new PrintWriter(new FileWriter(saveFileRemoveI + "\\peopleIDNextApRemove_" + i + ".csv"));
				pw.println(newHead);
				if (strList.containsKey("" + i)) {
					List<String> lis = strList.get("" + i);
					String readTwo = lis.get(0);
					int j = 1;
					do {
						String[] s = readTwo.split(",");// 使用有用的列输出到文件中
						pw.println(s[1] + "," + s[2] + "," + s[6]);
						String[] str = readTwo.split(",");
						String id = str[2];
						while (j < lis.size() - 1) {
							readTwo = lis.get(j);
							String[] str2 = readTwo.split(",");
							String id2 = str2[2];
							String readBefore = lis.get(j - 1);
							if (!id.equals(id2)) {
								String[] ss = readBefore.split(",");// 使用有用的列输出到文件中
								pw.println(ss[1] + "," + ss[2] + "," + ss[6]);
								break;
							}
							j++;
						}

					} while (j < lis.size() - 2);
				}
				pw.close();
			}
		}
		br.close();
	}

}
