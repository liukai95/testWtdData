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
import java.util.TreeMap;

/**
 * ；格式USER_ID,SAMPLE_TIME,AP_ID,SIG_STRENGTH,AC_POWER,ASSOCIATED,NEXT_AP_ID
 * 改进：oneSignalTwoNextAp直接输出所有id停留在每个ap的概率的文件
 * 
 * @author Administrator
 * 
 */
public class IDTotalMatrix {

	private BufferedReader br;
	private PrintWriter pw;

	/**
	 * 
	 * @param aPNum
	 *            AP 合并的权重
	 * @param openFile
	 *            带NEXT_AP_ID的数据
	 * @param saveFileResult
	 *            保存结果的路径，生成一个概率转移矩阵matrix.csv
	 * @throws Exception
	 */
	public void getStayMatrix(int aPNum, String openFile, String saveFileResult) throws Exception {
		Map<String, List<String>> strList = new HashMap<String, List<String>>();
		br = new BufferedReader(new FileReader(new File(openFile)));
		String head = br.readLine();
		// System.out.println(head);
		String read = "";
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			String key = str[0];
			if (strList.containsKey(key)) {
				strList.get(key).add(str[1] + "," + str[2] + "," + str[6]);

			} else {
				List<String> list = new ArrayList<String>();
				list.add(str[1] + "," + str[2] + "," + str[6]);
				strList.put(key, list);
			}
		}

		File file = new File(saveFileResult);
		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()) {
			file.mkdir();
		}
		pw = new PrintWriter(new FileWriter(saveFileResult + "\\matrix.csv"));
		double[][] apRate = new double[245][aPNum];// 所有id,停留在每个ap的次数
		for (int i = 1; i <= 245; i++) {
			int[] apNumber = new int[aPNum];// 一个id停留在每个ap的次数

			Map<String, Integer> apMap = new TreeMap<String, Integer>(); // 一个id访问每个点的次数
			if (strList.containsKey("" + i)) {
				List<String> lis = strList.get("" + i);
				for (String s : lis) {
					String[] str = s.split(",");
					String key = str[1];
					if (apMap.containsKey(key)) {
						int value = apMap.get(key);
						value++;
						apMap.put(key, value);

					} else {
						apMap.put(key, 1);
					}
					if (str[1].equals(str[2])) {
						apNumber[Integer.parseInt(str[1]) - 1]++;
					}
				}
			}
			java.text.DecimalFormat df = new java.text.DecimalFormat("#.000");
			for (int j = 1; j <= aPNum; j++) {
				if (apNumber[j - 1] > 0)
					apRate[i - 1][j - 1] = (double) apNumber[j - 1] / apMap.get("" + j);
				pw.print(df.format(apRate[i - 1][j - 1]) + " ");
			}
			pw.println();
		}
		pw.close();
		br.close();
	}
}
