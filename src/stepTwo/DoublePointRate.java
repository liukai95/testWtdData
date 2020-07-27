package stepTwo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

/**
 * 两个地点同时被接收到的概率 输出doublePointRate使用APArea算法 输出doublePointRateMaxtir使用克鲁斯科尔算法
 * 共存在525个地点
 * @author Administrator
 * 
 */
public class DoublePointRate {
	private BufferedReader br;
	private PrintWriter pw;
	private Map<String, Integer> strList;

	/**
	 * 
	 * @param openFile 去重过后每个用户在同一时间接收的AP数据
	 * @param saveFile 两个AP同时被接收到的概率数据
	 * @throws Exception
	 */
	public void getPointRate(String openFile, String saveFile) throws Exception {
		strList = new TreeMap<String, Integer>();
		br = new BufferedReader(new FileReader(new File(openFile)));
		pw = new PrintWriter(new FileWriter(saveFile));
		String read = "";
		int[][] totalNum = new int[525][525];
		for (int i = 0; i < 525; i++) {
			totalNum[i] = new int[525];
		}
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			for (int i = 0; i < str.length; i++) {
				String key = str[i];
				if (strList.containsKey(key)) {
					int value = strList.get(key);
					value++;
					strList.put(key, value);

				} else {
					strList.put(key, 1);
				}
			}
			if (str.length > 1) {
				for (int i = 0; i < str.length - 1; i++) {
					for (int j = i + 1; j < str.length; j++) {
						int ii = Integer.parseInt(str[i]);
						int jj = Integer.parseInt(str[j]);
						totalNum[ii][jj] = totalNum[ii][jj] + 1;
						totalNum[jj][ii] = totalNum[jj][ii] + 1;
					}
				}
			}
		}
		//保留两位小数
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		//文件头信息
		pw.println("one,two,rate");
		for (int i = 1; i < 525; i++) {
			for (int j = i; j < 525; j++) {
				if (totalNum[i][j] != 0) {
					int ii = strList.get("" + i);
					int jj = strList.get("" + j);
					double d;
					d = (double) totalNum[i][j] * 2 / (ii + jj);
					pw.println(i + "," + j + "," + df.format(d));
				}
			}
		}		
		pw.close();
		br.close();
	}

}
