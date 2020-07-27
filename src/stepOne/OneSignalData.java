package stepOne;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * 得到只有一个信号的数据以及出现的AP，输入测试数据，输出oneSignalRmove以及AP
 */
public class OneSignalData {

	private BufferedReader br;
	private PrintWriter pw;

	/**
	 * 输入实验数据输出去重后的数据，以及出现的AP
	 * 
	 * @param openFileDemo
	 *            原实验数据
	 * @param saveFileRemove
	 *            只保存一个连接信号并且去重的数据
	 * @param saveFileGetAP
	 *            被连接过的AP数据
	 * @throws Exception
	 */
	public void removeGetAp(String openFileDemo, String saveFileRemove, String saveFileGetAP) throws Exception {
		Set<Integer> apSet = new TreeSet<Integer>();// 存放出现的ap
		Set<String> removeRepateDate = new LinkedHashSet<String>();// 对数据去重
		br = new BufferedReader(new FileReader(new File(openFileDemo)));
		pw = new PrintWriter(new FileWriter(saveFileGetAP));
		String head = br.readLine();
		// System.out.println(head);
		String read = "";
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			if (str[5].equals("1")) {
				apSet.add(Integer.parseInt(str[2]));// 存放ap
				removeRepateDate.add(read);// 存放所有数据进行去除重复数据
			}
		}
		// 输出ap
		for (Integer number : apSet) {
			pw.println(number);
		}
		pw.close();
		// 输出去重后的数据
		pw = new PrintWriter(new FileWriter(saveFileRemove));
		pw.println(head);
		for (String s : removeRepateDate) {
			pw.println(s);
		}
		pw.close();
		br.close();
	}

	/**
	 * 输入测试数据输出去重后的数据，工具方法，removeGetAp方法可调用该方法
	 * 
	 * @param openFileDemoTest
	 *            原实验数据
	 * @param saveFileRemove
	 *            只保存一个连接信号并且去重的数据
	 * @throws Exception
	 */
	public void removeRepate(String openFileDemoTest, String saveFileRemove) throws Exception {
		Set<String> removeRepateDate = new LinkedHashSet<String>();
		br = new BufferedReader(new FileReader(new File(openFileDemoTest)));
		String head = br.readLine();
		// System.out.println(head);
		String read = "";
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			if (str[5].equals("1")) {
				removeRepateDate.add(read);// 存放所有数据进行去除重复数据
			}

		}
		br.close();
		// 输出去重后的数据
		pw = new PrintWriter(new FileWriter(saveFileRemove));
		pw.println(head);
		for (String s : removeRepateDate) {
			pw.println(s);
		}
		pw.close();

	}

}
