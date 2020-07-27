package markovMethodTest;

import java.util.List;
/**
 * 测试正确率
 *
 */
public class ResultTest {

	/**
	 *  仅使用马尔科夫链预测，考虑步数
	 * @param predictRate
	 * @param dataTest
	 * @param lagPeriod
	 * @return
	 * @throws Exception
	 */
	public double rightRateTotal(double[][] predictRate,List<Integer> dataTest,
			int lagPeriod) throws Exception {
		if (predictRate == null)
			return -1;// 无法进行预测


		int rightNumber = 0;// 正确的个数
		int errorNumber = 0;// 错误的个数
		double rightRate = 0;
		for (int i = 0; i < predictRate.length; i++) {//因为之前已经是使用转移链表计算的转移概率，所以可以直接使用概率矩阵的长度进行预测
			double max = 0;
			int next = 0;// 记录转移概率最大的地点
			for (int j = 0; j < predictRate[0].length; j++) {
				if (max < predictRate[i][j]) {
					max = predictRate[i][j];
					next = j + 1;
				}

			}

			if (next == dataTest.get(i + lagPeriod)) {
				 System.out.println("正确");
				rightNumber++;
			} else {
				System.out.println("错误");
				errorNumber++;
			}
		}
		System.out.println(rightNumber + errorNumber);
		if (rightNumber + errorNumber > 4) {// 大于10次的预测进行统计
			rightRate = (double) rightNumber / (rightNumber + errorNumber);
			return rightRate;
		}
		return -1;// 表示没有进行测试,为了统计没有进行测试的个数，方便求平均值。社团修正就不需要了
	}



}
