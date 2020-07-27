package apTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * 对停留时间进行测试,进行改进，保留是从哪一个地点转移过来的，使用相似轨迹进行预测 改进：考虑时间 在社团修正时调用预测停留时间
 */
public class IDApTime {
	
	private BufferedReader br;
	private int[] timePredictTotal;// 地点,值为预测时间
	private int[][] timePredictAp;// 上一个地点，该地点,值为预测时间
	private int[][][] timePredict;// 维数时间段，上一个地点，该地点,值为预测时间
	private String[] timeStr;//时间段数组

	/**
	 * 初始化预测停留时间
	 * @param apTime USER_ID在转移到的AP的停留时间文件
	 * @throws Exception
	 */
	public void apTimeRightRate(String apTime) throws Exception {
		br = new BufferedReader(new FileReader(new File(apTime)));
		timeStr = new String[] { "00:00:00", "02:00:00", "04:00:00",
				"06:00:00", "08:00:00", "10:00:00", "12:00:00", "14:00:00",
				"16:00:00", "18:00:00", "20:00:00", "22:00:00", "24:00:00" };// 两个小时一段
		timePredictTotal = new int[400];
		timePredictAp = new int[400][400];
		timePredict = new int[timeStr.length - 1][400][400];
		int[] timeNumberTotal = new int[400];// 该地点,值为数据的次数，为了计算平均值
		int[][] timeNumberAp = new int[400][400];// 上一个地点，该地点,值为数据的次数，为了计算平均值
		int[][][] timeNumber = new int[12][400][400];// 维数时间段，上一个地点，该地点,值为数据的次数，为了计算平均值

		String head = br.readLine();
		int beforeAp = 0;
		String read = "";
		if ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			beforeAp = Integer.parseInt(str[1]);
		}
		while ((read = br.readLine()) != null) {
			String[] str = read.split(",");
			int nowAp = Integer.parseInt(str[1]);
			int time = Integer.parseInt(str[2]);
			if (time < 86400 && nowAp > 0 && beforeAp > 0) {// 时间小于1天，才有计算的必要，要不然是错误的数据
				int index = 0;
				String nowTime = str[0].split(" ")[1];
				for (int i = 0; i < timeStr.length - 1; i++) {
					if (nowTime.compareTo(timeStr[i]) >= 0
							&& nowTime.compareTo(timeStr[i + 1]) <= 0) {
						index = i;// 第i段
						break;
					}
				}
				timeNumberTotal[nowAp - 1]++;
				timePredictTotal[nowAp - 1] += time;

				timeNumberAp[beforeAp - 1][nowAp - 1]++;
				timePredictAp[beforeAp - 1][nowAp - 1] += time;

				timeNumber[index][beforeAp - 1][nowAp - 1]++;// 次数加1
				timePredict[index][beforeAp - 1][nowAp - 1] += time;
			}

			beforeAp = Integer.parseInt(str[1]);// 更新上一个地点

		}
		// 计算平均停留时间
		for (int i = 0; i < timePredict.length; i++) {
			for (int j = 0; j < timePredict[0].length; j++) {
				for (int k = 0; k < timePredict[0].length; k++) {
					if (timeNumber[i][j][k] > 0) {// 在此处设置次数限制,默认为0
						timePredict[i][j][k] = timePredict[i][j][k]
								/ timeNumber[i][j][k];// 平均值
						// System.out.println(i + " " + j + " " + k + " "
						// + timePredict[i][j][k]);
					}

				}
			}
		}
		for (int j = 0; j < timePredict[0].length; j++) {
			for (int k = 0; k < timePredict[0].length; k++) {
				if (timeNumberAp[j][k] > 0) {
					timePredictAp[j][k] = timePredictAp[j][k]
							/ timeNumberAp[j][k];// 平均值
					// System.out.println(j + " " + k + " " +
					// timePredictAp[j][k]);
				}
			}
		}
		for (int k = 0; k < timePredict[0].length; k++) {
			if (timeNumberTotal[k] > 0) {
				timePredictTotal[k] = timePredictTotal[k] / timeNumberTotal[k];// 平均值
				// System.out.println(k + " " + timePredictTotal[k]);
			}
		}

	}

	/**
	 *  testApTime形式为：11-01 08:23:23
	 * @param beforeAp 上一个AP地点
	 * @param nowAp 此刻所在的AP地点
	 * @param time 此刻的具体时间
	 * @return
	 */
	public int test(int beforeAp, int nowAp, String time) {
		int result = 0;
		// 进行测试，返回预测的停留时间
		String[] str = time.split(" ");// 分割得到时间和地点
		int index = 0;
		for (int i = 0; i < timeStr.length - 1; i++) {
			if (str[1].compareTo(timeStr[i]) >= 0
					&& str[1].compareTo(timeStr[i + 1]) <= 0) {
				index = i;// 第i段
				break;
			}
		}
		if (beforeAp < 1 || beforeAp >= 400 || nowAp < 1 || nowAp >= 400) {// 传入无效数据
			return 0;
		}
		// System.out.println(index);
		if (timePredict[index][beforeAp - 1][nowAp - 1] > 0) {
			result = timePredict[index][beforeAp - 1][nowAp - 1];
		} else if (timePredictAp[beforeAp - 1][nowAp - 1] > 0) {
			result = timePredictAp[beforeAp - 1][nowAp - 1];
		} else {
			result = timePredictTotal[nowAp - 1];
		}
		return result;

	}

	public static void main(String[] args) {
		IDApTime att = new IDApTime();
		try {
			for (int i = 1; i <= 1; i++) {
				att.apTimeRightRate("F:\\移动轨迹数据\\result\\idApTime\\idApTime_"
						+ i + ".csv");
				int result = att.test(288, 177, "12-06 08:21:00");
				System.out.println(result);
			}

		} catch (Exception e) {
		}
	}
}
