package stepTwo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 对同时收到的AP进行区域的划分,同时出现的概率可任意条件，one,two,rate 可更改概率最小值
 * 
 * @author Administrator
 * 
 */
public class APArea {

	private BufferedReader br;
	private PrintWriter pw;

	/**
	 * 
	 * @param sameTimePointRate
	 *            同时出现的概率，即合并的权重
	 * @param openFile
	 *            两个AP同时被接收到的概率数据
	 * @param saveFile
	 *            满足sameTimePointRate权重合并后的AP数据
	 * @throws Exception
	 */
	public void combineAP(double sameTimePointRate, String openFile, String saveFile) throws Exception {
		Set<String> result = new LinkedHashSet<String>();
		Queue<String> queue = new LinkedList<String>();
		Map<String, List<String>> strList = new LinkedHashMap<String, List<String>>();
		br = new BufferedReader(new FileReader(new File(openFile)));
		pw = new PrintWriter(new FileWriter(saveFile));
		String head = br.readLine();
		// System.out.println(head);
		String read = "";
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			if (Double.parseDouble(str[2]) > sameTimePointRate) {
				String key = str[0];
				if (strList.containsKey(key)) {
					List<String> list = strList.get(key);
					list.add(str[1]);
					strList.put(key, list);
				} else {
					List<String> list = new ArrayList<String>();
					list.add(str[1]);
					strList.put(key, list);
				}
			}
		}
		//依次搜索关联数据
		while (strList.size() > 0) {
			String first = strList.entrySet().iterator().next().getKey();
			queue.add(first);// 加入第一个AP
			while (queue.size() > 0) {
				String s = queue.poll();
				result.add(s);
				if (strList.containsKey(s)) {
					List<String> apList = strList.get(s);
					for (String str : apList) {
						queue.add(str);
					}
				}
				strList.remove(s);
			}
			// 遍历Set,输出结果
			for (String str : result) {
				pw.print(str + " ");
			}
			pw.println();
			result.clear();
		}
		pw.close();
		br.close();
	}

}
