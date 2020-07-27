package stepTwo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 将同时收到的点，以及原来存在的点合起来，重新编号 输出NewAP；
 * 
 * @author Administrator
 * 
 */
public class NewAP {

	private BufferedReader br;
	private PrintWriter pw;
	private Set<String> apTotal;

	/**
	 * 对处理后的AP重新编号
	 * 
	 * @param openFileAP
	 *            连接的AP数据
	 * @param openFileCombineAP
	 *            合并后的AP数据
	 * @param saveFile
	 *            重新编号后的AP数据文件
	 * @return 返回值为新地点的个数，后期预测时需要该值
	 * @throws Exception
	 */
	public int getNewAP(String openFileAP, String openFileCombineAP, String saveFile) throws Exception {
		apTotal = new LinkedHashSet<String>(); // 存放原始收到的AP
		br = new BufferedReader(new FileReader(new File(openFileAP)));
		String read = "";
		while ((read = br.readLine()) != null) {
			apTotal.add(read);
		}
		br.close();
		br = new BufferedReader(new FileReader(new File(openFileCombineAP)));
		pw = new PrintWriter(new FileWriter(saveFile));
		while ((read = br.readLine()) != null) {
			String[] str = read.split(" ");
			// 对于需要合并的点需要先和原始接收到的点进行比较，如果有的话需要删掉，删完之后把结果存放到最后就行了，存在里面都没有的情况，因为这个数据有时仅仅是被收到了，
			// 却从来没有连上过，即认为从来没有被访问过
			for (int i = 0; i < str.length; i++) {
				if (apTotal.contains(str[i]))
					apTotal.remove(str[i]);
			}
			apTotal.add(read);
		}
		int number = 0;
		for (String s : apTotal) {
			number++;
			pw.println(number + "," + s);
		}
		pw.close();
		br.close();
		return number;
	}

}
