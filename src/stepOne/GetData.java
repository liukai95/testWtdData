package stepOne;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * 
 * 截取数据，并得到测试数据
 * 
 */
public class GetData {

	private BufferedReader br;
	private PrintWriter pw;

	// private PrintWriter pw2;
	/**
	 * 
	 * @param openFileData
	 *            源文件
	 * @param saveFileDemo
	 *            实验数据，选取从9-22到10-31共40天的数据
	 * @param saveFileTest
	 *            算法测试数据，即剩下的数据
	 * @return 实验数据的条数
	 * @throws Exception
	 */

	public int getdata(String openFileData, String saveFileDemo, String saveFileTest) throws Exception {
		int demoSum = 0;
		br = new BufferedReader(new FileReader(new File(openFileData)));
		pw = new PrintWriter(new FileWriter(saveFileDemo));
		String read = "";
		String head = br.readLine();
		pw.println(head);
		read = br.readLine();
		String[] str = read.split(",");
		// 截取11-01 00:00:00前的数据
		while (str[1].compareTo("11-01 00:00:00") < 0) {
			demoSum++;
			pw.println(read);
			read = br.readLine();
			str = read.split(",");
		}
		// 可使用finally进行流的关闭
		pw.close();
		// 得到剩余的数据，用于后期测试
		pw = new PrintWriter(new FileWriter(saveFileTest));
		pw.println(head);
		pw.println(read);
		while ((read = br.readLine()) != null) {
			pw.println(read);
		}
		pw.close();
		// 一般先打开的后关闭，后打开的先关闭
		br.close();
		return demoSum;
	}

	public static void main(String[] args) {
		GetData gd = new GetData();
		try {
			gd.getdata("F:/移动轨迹数据/newData.csv", "F:/移动轨迹数据/demo.csv", "F:/移动轨迹数据/demoTest.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
