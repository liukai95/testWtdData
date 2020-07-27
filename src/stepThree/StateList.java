package stepThree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.LinkedList;
import java.util.List;

/**
 * 计算马尔科夫转移矩阵 输入每个人移动数据，输出每个人移动矩阵 计算在每个地点呆了多长时间，格式为SAMPLE_TIME,AP_ID,STAY_TIME
 * 
 * @author Administrator
 *         改进：后期计算多步转移的权重时，需要原始数据，进行马氏检验需要频数，所以此时不需要进行转移矩阵，只需输出数据就行
 */
public class StateList {
	
	private BufferedReader br;
	private PrintWriter pw;// 输出stateList
	private PrintWriter pw2;// 输出idApTime

	/**
	 * 总数据
	 * @param openFileApRemove 每个USER_ID的移动数据
	 * @param saveFileStateList 每个USER_ID转移AP的马尔科夫链
	 * @param saveFileIdApTime 每个USER_ID在转移到的AP的停留时间
	 * @throws Exception
	 */
	public void testTotal(String openFileApRemove, String saveFileStateList,
			String saveFileIdApTime) throws Exception {
		StringBuffer state = new StringBuffer();// 所有转移的状态
		br = new BufferedReader(new FileReader(new File(openFileApRemove)));
		pw = new PrintWriter(new FileWriter(saveFileStateList));
		pw2 = new PrintWriter(new FileWriter(saveFileIdApTime));
		String newHead = "SAMPLE_TIME,AP_ID,STAY_TIME";
		pw2.println(newHead);
		String read = "";
		String head = br.readLine();
		// System.out.println(head);
		String beginTime = "";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"MM-dd HH:mm:ss");
		int num = 0;// 用于计数，去掉ap和nextAp相同的数据，由于计算数据的重复性，可知一个相同一个不同，方便计算，但会出现部分重复数据，先不考虑
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			if (num % 2 == 0) {// 计算一个id在一个地点呆了多长时间，也可以判断str[1]是否等于str[2]
				beginTime = str[0];
			} else {
				long t = (sdf.parse(str[0]).getTime() - sdf.parse(beginTime)
						.getTime()) / 1000;// 使用秒作为单位
				if (t != 0) {// 存在错误数据可能造成结果为0
					pw2.println(beginTime + "," + str[1] + "," + t);
				}
				state.append("," + str[1]);
			}
			num++;
		}
		pw2.close();
		pw.println(printString(state));// 输出结果，去除第一个逗号
		pw.close();
		br.close();
	}

	/**
	 * 对于分段数据进行处理，需要考虑每天的转移，因为两天的数据中间断开了
	 * @param openFileApRemove 分段数据每个USER_ID的移动数据
	 * @param saveFileStateList 分段数据每个USER_ID转移AP的马尔科夫链
	 * @param saveFileIdApTime 分段数据每个USER_ID在转移到的AP的停留时间
	 * @throws Exception
	 */
	public void testSplit(String openFileApRemove, String saveFileStateList,
			String saveFileIdApTime) throws Exception {
		StringBuffer state = new StringBuffer();// 所有转移的状态
		br = new BufferedReader(new FileReader(new File(openFileApRemove)));
		pw = new PrintWriter(new FileWriter(saveFileStateList));
		pw2 = new PrintWriter(new FileWriter(saveFileIdApTime));
		String newHead = "SAMPLE_TIME,AP_ID,STAY_TIME";
		pw2.println(newHead);
		String read = "";
		String head = br.readLine();
		// System.out.println(head);
		String beginTime = "";
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"MM-dd HH:mm:ss");
		int num = 0;// 用于计数，去掉ap和nextAp相同的数据，由于计算数据的重复性，可知一个相同一个不同，方便计算，但会出现部分重复数据，先不考虑

		if ((read = br.readLine()) != null) {// 第一行可能没数据
			String[] ss = read.split(",");
			String oldTime = ss[0].split(" ")[0];// 得到是第几天
			beginTime = ss[0];
			while ((read = br.readLine()) != null) {
				String[] str = read.split(",");
				String newTime = str[0].split(" ")[0];// 得到是第几天
				if (num % 2 != 0) {// 计算一个id在一个地点呆了多长时间，也可以判断str[1]是否等于str[2]
					beginTime = str[0];
				} else {
					long t = (sdf.parse(str[0]).getTime() - sdf
							.parse(beginTime).getTime()) / 1000;// 使用秒作为单位
					if (t != 0) {// 存在错误数据可能造成结果为0
						pw2.println(beginTime + "," + str[1] + "," + t);
					}
					if (oldTime.equals(newTime)) {
						state.append("," + str[1]);
					} else {
						state.append(","+"0"+"," + str[1]);// 使用0区分一天
						oldTime = newTime;// 更新天数
					}
				}
				num++;
			}
		}
		pw2.close();
		pw.println(printString(state));// 输出结果，去除第一个逗号
		pw.close();	
		br.close();
	}

	/**
	 * 输出链表去掉第一个,即逗号
	 * @param stateString
	 * @return
	 */
	public String printString(StringBuffer stateString) {
		if (stateString.length() > 0){
			return stateString.substring(1, stateString.length());
		}
		return "";
	}

}
