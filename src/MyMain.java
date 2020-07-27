import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import combination.SimilarCombinationProb;
import combination.SocietyCombinationProb;

import apTime.IDApTimeTest;
import markovAndSociety.DiscreteMarkov;
import markovAndSociety.DiscreteMarkovSplit;
import markovAndSociety.SocietyPredictNew;
import markovAndSociety.SocietyPredict;
import markovAndSociety.ResultTest;

import stepOne.GetData;
import stepOne.OneSignalData;
import stepOne.SameSignalData;
import stepThree.IDTotalMatrix;
import stepThree.IDDivisionRemove;
import stepThree.APDivision;
import stepThree.TimeNumber;
import stepThree.StateList;
import stepThree.StateListAddTime;
import stepThree.TimeDivide;
import stepTwo.DoublePointRate;
import stepTwo.APArea;
import stepTwo.NewAP;
import stepThree.OneSignalResult;
/**
 * 改进，进行分段的马尔科夫预测时对分段的数据的处理不同，对结果进行比较
 *
 */
public class MyMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		// int[] newAPNum = new int[9];
		int[] newAPNum = new int[] { 148, 180, 233, 297, 338, 357, 359, 359,
				360 };// 先列出，可去掉一二步的重复计算
		/*
		 * 设置进行分段的时间段
		 */

		// String[] timeStr = new String[] { "12:00:00", "24:00:00" };

		// String[] timeStr = new String[] { "08:00:00", "16:00:00","24:00:00"
		// };

		String[] timeStr = new String[] { "06:00:00", "12:00:00", "18:00:00",
				"24:00:00" };

		// String[] timeStr = new String[] { "04:00:00", "08:00:00", "12:00:00",
		// "06:00:00", "20:00:00", "24:00:00" };

		/*
		 * 第一步，对原始数据进行处理，分为实验数据和测试数据，对相邻地点进行合并处理
		 */
//		 try {
//		 GetData gd = new GetData();
//		 //实验数据的个数
//		 int demoSum = gd.getdata("F:/移动轨迹数据/newData.csv", "F:/移动轨迹数据/demo.csv",
//		 "F:/移动轨迹数据/demoTest.csv");
//		
//		 OneSignalData osd = new OneSignalData();
//		 osd.removeGetAp("F:/移动轨迹数据/demo.csv",
//		 "F:/移动轨迹数据/oneSignalRemove.csv", "F:/移动轨迹数据/AP.txt");
//		
//		 SameSignalData ssd = new SameSignalData();
//		 ssd.getSignalData("F:/移动轨迹数据/demo.csv", "F:/移动轨迹数据/sameSignal.txt",demoSum);
//		
//		 // 测试数据
//		 osd.removeRepate("F:/移动轨迹数据/demoTest.csv",
//		 "F:/移动轨迹数据/oneSignalRemoveTest.csv");
//		
//		 } catch (Exception e) {
//		 }
//			/*
//			 * 第二步，进行相邻AP的合并，选择同时收到的最小概率为0.1至0.9
//			 */
//		 try {
//		 double myRate = 0.1;
//		 int i = 1;
//		
//		 while (myRate <= 0.9) {
//		 DoublePointRate dpr = new DoublePointRate();
//		 dpr.getPointRate("F:/移动轨迹数据/sameSignal.txt",
//		 "F:/移动轨迹数据/doublePointRate.txt");
//		
//		 APArea aa = new APArea();
//		 aa.combineAP(myRate, "F:/移动轨迹数据/doublePointRate.txt",
//		 "F:/移动轨迹数据/doublePointRateResult.txt");
//		
//		 NewAP na = new NewAP();
//		 newAPNum[i - 1] = na.getNewAP("F:/移动轨迹数据/AP.txt",
//		 "F:/移动轨迹数据/doublePointRateResult.txt",
//		 "F:/移动轨迹数据/NewAP" + i + ".txt");
//		
//		 myRate += 0.1;
//		 i++;
//		 }
//		 } catch (Exception e) {
//		 }

		// 可以加在一起不编号，中间数据覆盖也没有什么问题

		for (int osI = 4; osI <= 4; osI++) {

			/*
			 * 第三步，总数据
			 */
//			 try {
//			 OneSignalResult de30 = new OneSignalResult();
//			 de30.getNextAPDate("F:/移动轨迹数据/NewAP" + osI + ".txt",
//			 "F:/移动轨迹数据/oneSignalRemove.csv",
//			 "F:/移动轨迹数据/oneSignalTwoNextAp.csv");
//			
//			 // 测试数据
//			 de30.getNextAPDate("F:/移动轨迹数据/NewAP" + osI + ".txt",
//			 "F:/移动轨迹数据/oneSignalRemoveTest.csv",
//			 "F:/移动轨迹数据/oneSignalTwoNextApTest.csv");
//			
//			 IDTotalMatrix de31 = new IDTotalMatrix();
//			 de31.getStayMatrix(newAPNum[osI - 1],
//			 "F:/移动轨迹数据/oneSignalTwoNextAp.csv",
//			 "F:/移动轨迹数据/result");
//			
//			 IDDivisionRemove dr = new IDDivisionRemove();
//			 dr.totalAddSplitFile("F:/移动轨迹数据/oneSignalTwoNextAp.csv",
//			 "F:/移动轨迹数据/oneSignalTwoNextApRemoveSort.csv",
//			 "F:/移动轨迹数据/result/Remove");
//			 // 测试数据
//			 dr.splitFile("F:/移动轨迹数据/oneSignalTwoNextApTest.csv",
//			 "F:/移动轨迹数据/peopleIDNextApRemoveTest");
//			
//			 String saveSL = "F:/移动轨迹数据/result/stateList";
//			 File file = new File(saveSL);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			 String apTime = "F:/移动轨迹数据/result/idApTime";
//			 file = new File(apTime);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			 // 得到转移地点的链
//			 StateList de37 = new StateList();
//			 for (int i = 1; i <= 245; i++) {
//			 de37.testTotal(
//			 "F:/移动轨迹数据/result/Remove/peopleIDNextApRemove_"
//			 + i + ".csv", saveSL + "/stateList_" + i
//			 + ".csv", apTime + "/idApTime_" + i
//			 + ".csv");
//			 }
//			 // 不进行社团预测时可省去
//			 APDivision de35 = new APDivision();
//			 de35.apDivision(newAPNum[osI - 1],
//			 "F:/移动轨迹数据/oneSignalTwoNextApRemoveSort.csv",
//			 "F:/移动轨迹数据/apVisited");
//			 // System.out.println("计算两者相遇");
//			 TimeNumber de36 = new TimeNumber();
//			 String saveNumber = "F:/移动轨迹数据/apVisited/apNumber";
//			 file = new File(saveNumber);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			 String saveTime = "F:/移动轨迹数据/apVisited/apTime";
//			 file = new File(saveTime);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			
//			 for (int i = 1; i <= newAPNum[osI - 1]; i++) {
//			 de36.getTN(i, "F:/移动轨迹数据/apVisited/apVisited_" + i
//			 + ".csv", saveNumber + "/apNumber_" + i + ".csv",
//			 saveTime + "/apTime_" + i + ".csv");
//			 }
//			
//			 // 测试数据
//			 String saveState = "F:/移动轨迹数据/stateListAddTimeTest";
//			 file = new File(saveState);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			 String saveApTime = "F:/移动轨迹数据/idApTimeTest";
//			 file = new File(saveApTime);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			
//			 StateListAddTime de37time = new StateListAddTime();
//			 for (int i = 1; i <= 245; i++) {
//			 de37time.testTotal(
//			 "F:/移动轨迹数据/peopleIDNextApRemoveTest/peopleIDNextApRemove_"
//			 + i + ".csv", saveState
//			 + "/stateListAddTime_" + i + ".csv",
//			 saveApTime + "/idApTime_" + i + ".csv");
//			 }
//			 } catch (Exception e) {
//			 }
			//
//
//			/*
//			 * 第三步，分段数据
//			 */
//			 try {
//			
//			 TimeDivide deTime = new TimeDivide();
//			 deTime.divide(timeStr, "F:/移动轨迹数据/oneSignalTwoNextAp.csv",
//			 "F:/移动轨迹数据/timeDivide");
//			
//			 // 测试数据
//			 deTime.divide(timeStr, "F:/移动轨迹数据/oneSignalTwoNextApTest.csv",
//			 "F:/移动轨迹数据/timeDivideTest");
//			
//			 for (int projectI = 1; projectI <= timeStr.length; projectI++) {
//			 // 输入分段过后的数据,数据为timeNextAp
//			
//			 IDTotalMatrix de31 = new IDTotalMatrix();
//			 de31.getStayMatrix(newAPNum[osI - 1],
//			 "F:/移动轨迹数据/timeDivide/timeNextAp_" + projectI
//			 + ".csv", "F:/移动轨迹数据/timeDivide/result"
//			 + projectI);
//			
//			 IDDivisionRemove de33 = new IDDivisionRemove();
//			 de33.totalAddSplitFile(
//			 "F:/移动轨迹数据/timeDivide/timeNextAp_" + projectI
//			 + ".csv",
//			 "F:/移动轨迹数据/timeDivide/timeNextApRemoveSort_"
//			 + projectI + ".csv",
//			 "F:/移动轨迹数据/timeDivide/result" + projectI
//			 + "/Remove");
//			
//			 String saveSL = "F:/移动轨迹数据/timeDivide/result" + projectI
//			 + "/stateList";
//			 File file = new File(saveSL);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			 String apTime = "F:/移动轨迹数据/timeDivide/result" + projectI
//			 + "/idApTime";
//			 file = new File(apTime);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			
//			 StateList de37 = new StateList();
//			 for (int i = 1; i <= 245; i++) {
//			 de37.testTotal("F:/移动轨迹数据/timeDivide/result"
//			 + projectI + "/Remove/peopleIDNextApRemove_"
//			 + i + ".csv", saveSL + "/stateList_" + i
//			 + ".csv", apTime + "/idApTime_" + i + ".csv");
//			 }
//			
//			 // 不进行社团预测时可省去
//			 APDivision de35 = new APDivision();
//			 de35.apDivision(newAPNum[osI - 1],
//			 "F:/移动轨迹数据/timeDivide/timeNextApRemoveSort_"
//			 + projectI + ".csv",
//			 "F:/移动轨迹数据/timeDivide/apVisited" + projectI);
//			 // System.out.println("计算两者相遇");
//			 TimeNumber de36 = new TimeNumber();
//			 String saveNumber = "F:/移动轨迹数据/timeDivide/apVisited"
//			 + projectI + "/apNumber";
//			 file = new File(saveNumber);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			 String saveTime = "F:/移动轨迹数据/timeDivide/apVisited"
//			 + projectI + "/apTime";
//			 file = new File(saveTime);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			
//			 for (int i = 1; i <= newAPNum[osI - 1]; i++) {
//			 de36.getTN(i, "F:/移动轨迹数据/timeDivide/apVisited"
//			 + projectI + "/apVisited_" + i + ".csv",
//			 saveNumber + "/apNumber_" + i + ".csv",
//			 saveTime + "/apTime_" + i + ".csv");
//			 }
//			
//			 // 测试数据
//			 de33.splitFile("F:/移动轨迹数据/timeDivideTest/timeNextAp_"
//			 + projectI + ".csv",
//			 "F:/移动轨迹数据/timeDivideTest/peopleIDNextApRemoveTest"
//			 + projectI);
//			
//			 String saveState =
//			 "F:/移动轨迹数据/timeDivideTest/stateListAddTimeTest"
//			 + projectI;
//			 file = new File(saveState);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			 String saveApTime = "F:/移动轨迹数据/timeDivideTest/idApTimeTest"
//			 + projectI;
//			 file = new File(saveApTime);
//			 // 如果文件夹不存在则创建
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			
//			 StateListAddTime de37Time = new StateListAddTime();
//			 for (int i = 1; i <= 245; i++) {
//			 de37Time.testTotal(
//			 "F:/移动轨迹数据/timeDivideTest/peopleIDNextApRemoveTest"
//			 + projectI + "/peopleIDNextApRemove_"
//			 + i + ".csv", saveState
//			 + "/stateListAddTime_" + i + ".csv",
//			 saveApTime + "/idApTime_" + i + ".csv");
//			 }
//			 }
//			
//			 } catch (Exception e) {
//			 }

			System.out.println("划分区域" + osI + "，分成" + timeStr.length
					+ "段，预测结果如下:");
			// 进行马尔科夫链预测
			DiscreteMarkov dm = new DiscreteMarkov();
			int lagPeriodMax = 2;// 设置所求最大步长为2
			int count = newAPNum[osI - 1];
			double[] rightRate = new double[245];
			double[][] rightRateSociety = new double[10][245];// 改变修正因子后的结果
			double errorRate = 0.3;// 计算算法的失败率,设置为0.3
			PrintWriter pw;
			String saveTest = "F:/markovTest/area" + osI;
			File file = new File(saveTest);
			// 如果文件夹不存在则创建
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			double goodRate = 0.9;

			// 总数据
			ResultTest rst = new ResultTest();
			SocietyPredictNew spn = new SocietyPredictNew();

			// SocietyPredict sp = new SocietyPredict();//原有方法

			/*
			 * 仅马尔科夫预测
			 */
//			 try {
//			 int goodNum = 0;// 统计正确率超过0.9的个数
//			 for (int lagPeriodI = 1; lagPeriodI <= lagPeriodMax;
//			 lagPeriodI++) {// 步数循环
//			 double averRate = 0;// 比较划分时的平均正确率，考虑步数
//			 int lagPeriodDis = lagPeriodMax - lagPeriodI;//
//			 // 步长差值，为了使各步长预测的数据相同，如果不考虑会造成短步长预测值多的情况，产生误差
//			
//			 pw = new PrintWriter(new FileWriter(saveTest
//			 + "/ResultlagPeriod" + lagPeriodI + ".csv"));//
//			 // 输出到文件,考虑步数，进行比较正确率
//			
//			 int num = 0;// 不能够进行预测的数目
//			 int num2 = 0;// 预测正确率较小的的数目
//			 for (int fileI = 1; fileI <= 245; fileI++) {
//			
//			 if (dm.init("F:/移动轨迹数据/result/stateList/stateList_"
//			 + fileI + ".csv", count, lagPeriodI)) {// 是否满足马氏性，满足马氏性才进行预测
//			 String fileState =
//			 "F:/移动轨迹数据/stateListAddTimeTest/stateListAddTime_"
//			 + fileI + ".csv";
//			 double[][] predictmarkov = dm.predictProb(
//			 fileState, lagPeriodDis);
//			 double value = rst.rightRateTotal(predictmarkov,
//			 fileState, lagPeriodMax);
//			 if (value > goodRate) {
//			 goodNum++;
//			 }
//			 if (value > errorRate) {
//			 rightRate[fileI - 1] = value;
//			 averRate += rightRate[fileI - 1];// 求总和，然后计算平均值
//			 pw.println(rightRate[fileI - 1]);// 输出正确值
//			 continue;// 跳出不进行num++;
//			 } else if (value > -1) {
//			 num2++;
//			 pw.println(rightRate[fileI - 1]);// 输出正确值
//			 continue;// 跳出不进行num++;
//			 }
//			 }
//			 num++;
//			 pw.println("-1");// -1代表没有预测
//			
//			 }
//			 if (245 > (num + num2)) {
//			 pw.println("aver:" + averRate / (245 - num - num2));
//			 }
//			 pw.println("失败率:" + (double) num2 / 245);
//			 System.out.println("失败率:" + (double) num2 / 245);
//			 pw.close();
//			 System.out.println("num" + num);
//			 //System.out.println("goodNum" + goodNum);
//			 if (245 > (num + num2)) {
//			 System.out.println(lagPeriodI + "阶" + "马尔科夫平均正确率"
//			 + averRate / (245 - num - num2) + "\n");
//			 }
//			
//			 }
//			 } catch (Exception e) {
//			 }

			/*
			 * 总数据的社团修正
			 */
//			 try {
//			 int goodNum = 0;// 统计正确率超过0.9的个数
//			 // 社团修正初始化
//			 spn.init(count, "F:/test/apTotalTime.txt",
//			 "F:/移动轨迹数据/apVisited");
//			
//			 // sp.init(count, "F:/移动轨迹数据/result",
//			 // "F:/test/apTotalTime.txt", "F:/移动轨迹数据/apVisited");
//			 double[] averRateSociety = new double[10];// 进行社团修正的平均正确率，考虑步数
//			
//			 pw = new PrintWriter(new FileWriter(saveTest
//			 + "/ResultSociety.csv"));//
//			 // 输出到文件,考虑步数，进行比较正确率
//			 int num = 0;// 不能够进行预测的数目
//			 int num2 = 0;// 社团修正预测正确率较小的的数目
//			 for (int fileI = 1; fileI <= 245; fileI++) {
//			 if (dm.init("F:/移动轨迹数据/result/stateList/stateList_"
//			 + fileI + ".csv", count, 1)) {// 是否满足马氏性，满足马氏性才进行预测
//			 String fileState =
//			 "F:/移动轨迹数据/stateListAddTimeTest/stateListAddTime_"
//			 + fileI + ".csv";
//			 double[][] predictmarkov = dm.predictProb(fileState, 1);
//			 double value = rst.rightRateTotal(predictmarkov,
//			 fileState, lagPeriodMax);// 最后一个参数为1会出错
//			 if (value > -1) {// 能进行社团修正
//			 rightRate[fileI - 1] = value;
//			 // 社团预测
//			 double[][] predictSociety = spn.predictTotal(fileI,
//			 fileState, count,
//			 "F:/移动轨迹数据/peopleIDNextApRemoveTest",
//			 "F:/移动轨迹数据/result/idApTime",
//			 "F:/test/RESULT.txt", lagPeriodMax);
//			
//			 // double[][] predictSociety = sp.predictTotal(
//			 // fileI, fileState, count,
//			 // "F:/移动轨迹数据/peopleIDNextApRemoveTest",
//			 // "F:/test/RESULT.txt", lagPeriodMax);
//			 // 进行社团修正
//			 boolean falg = false;
//			 for (int fator = 0; fator < 10; fator++) {
//			 rightRateSociety[fator][fileI - 1] = rst
//			 .rightRateSocietyTotal(predictmarkov,
//			 predictSociety, fileState,
//			 lagPeriodMax,
//			 (double) (fator + 1) / 10);
//			 averRateSociety[fator] += rightRateSociety[fator][fileI - 1];
//			 if (rightRateSociety[fator][fileI - 1] > (goodRate)
//			 && !falg) {
//			 goodNum++;
//			 falg = true;
//			 }
//			 }
//			 }
//			
//			 if (rightRateSociety[0][fileI - 1] > errorRate) {// 判断失败率
//			
//			 pw.println(rightRateSociety[0][fileI - 1]);// 输出正确值
//			 continue;// 跳出不进行num++;
//			 } else if (value < errorRate
//			 && rightRateSociety[0][fileI - 1] < errorRate
//			 && value > -1) {
//			 num2++;
//			 value = rightRateSociety[0][fileI - 1] > value ?
//			 rightRateSociety[0][fileI - 1]
//			 : value;
//			 pw.println(value);// 输出正确值
//			 continue;// 跳出不进行num++;
//			 }
//			 }
//			 num++;
//			 pw.println("-1");// -1代表没有预测
//			
//			 }
//			 if (245 > (num + num2)) {
//			 pw.println("aver:" + averRateSociety[0]
//			 / (245 - num - num2));
//			 }
//			 pw.println("失败率:" + (double) num2 / 245);
//			 System.out.println("失败率:" + (double) num2 / 245);
//			 pw.close();
//			 double max = 0;
//			 int result = 0;
//			 for (int fator = 0; fator < 10; fator++) {
//			 if (max < averRateSociety[fator]) {
//			 max = averRateSociety[fator];
//			 result = fator + 1;
//			 }
//			 }
//			 System.out.println("num" + num);
//			 if (245 > (num + num2)) {
//			 System.out.println("数据修正结果最好为" + result);
//			 // System.out.println("goodNum" + goodNum);
//			 System.out.println("平均正确率" + averRateSociety[0]
//			 / (245 - num - num2) + "\n");
//			 }
//			 } catch (Exception e) {
//			 }

			/*
			 * 分段数据,为了提高计算时间，可将分段的社团修正代码注释掉，仅从总数据的社团修正结果中便可看出效果，分段修正的效果更好
			 */
//			 try {
//			 int reaLen = 0;// 实际参与预测的段数，有的段数全部不符合马氏性，正确率为0，但如果除以reaLen，也会造成误差
//			 double[] averRateSplit = new double[lagPeriodMax];
//			 // 分段的平均正确率,考虑步数
//			 double[] averErrorRateSplit = new double[lagPeriodMax];
//			 // 分段的平均失败率,考虑步数
//			 for (int projectI = 1; projectI <= timeStr.length; projectI++) {
//			 // 初始化
//			 // spn.init(count,
//			 // "F:/test/apTotalTime"+timeStr.length + projectI +
//			 // ".txt",
//			 // "F:/移动轨迹数据/timeDivide/apVisited" + projectI);
//			
//			 /*
//			 * 另一种修正的方法
//			 */
//			 // sp.init(count, "F:/移动轨迹数据/timeDivide/result" +
//			 // projectI,
//			 // "F:/test/apTotalTime" + projectI + ".txt",
//			 // "F:/移动轨迹数据/timeDivide/apVisited" + projectI);
//			 for (int lagPeriodI = 1; lagPeriodI <= lagPeriodMax;
//			 lagPeriodI++) {// 步数循环
//			 int lagPeriodDis = lagPeriodMax - lagPeriodI;//
//			 // 步长差值，为了使各步长预测的数据相同，如果不考虑会造成短步长预测值多的情况，产生误差
//			 double averRate = 0;// 比较划分时的平均正确率，考虑步数和分段
//			 double[] averRateSociety = new double[10];// 进行社团修正的平均正确率，考虑步数
//			
//			 String saveFile = saveTest + "/ResultTime" + projectI;
//			 file = new File(saveFile);
//			 if (!file.exists() && !file.isDirectory()) {
//			 file.mkdir();
//			 }
//			 pw = new PrintWriter(new FileWriter(saveFile
//			 + "/ResultlagPeriod" + lagPeriodI + ".csv"));//
//			 // 输出到文件,考虑步数，进行比较正确率
//			
//			 int num = 0;// 不能够进行预测的数目
//			 int num2 = 0;// 预测正确率较小的的数目
//			 for (int fileI = 1; fileI <= 245; fileI++) {
//			 // 进行初始化
//			 if (dm.init("F:/移动轨迹数据/timeDivide/result"
//			 + projectI + "/stateList/stateList_"
//			 + fileI + ".csv", count, lagPeriodI)) {// 是否满足马氏性，满足马氏性才进行预测
//			
//			 String fileState =
//			 "F:/移动轨迹数据/timeDivideTest/stateListAddTimeTest"
//			 + projectI
//			 + "/stateListAddTime_"
//			 + fileI + ".csv";
//			 double[][] predictmarkov = dm.predictProb(
//			 fileState, lagPeriodDis);
//			 double value = rst.rightRateTotal(
//			 predictmarkov, fileState, lagPeriodMax);
//			
//			 if (value > errorRate) {
//			 rightRate[fileI - 1] = value;
//			 // 社团预测，注意调用分段的社团修正方法
//			 // double[][] predictSociety = spn
//			 // .predictTotal(
//			 // fileI,
//			 // fileState,
//			 // count,
//			 // "F:/移动轨迹数据/timeDivideTest/peopleIDNextApRemoveTest"
//			 // + projectI,
//			 // "F:/移动轨迹数据/timeDivideTest/idApTimeTest"
//			 // + projectI,
//			 // "F:/test/RESULT"+timeStr.length
//			 // + projectI + ".txt",
//			 // lagPeriodMax);
//			 // // 进行社团修正
//			 // for (int fator = 0; fator < 10; fator++)
//			 // {
//			 // rightRateSociety[fator][fileI - 1] = rst
//			 // .rightRateSocietyTotal(
//			 // predictmarkov,
//			 // predictSociety,
//			 // fileState,
//			 // lagPeriodMax,
//			 // (double) (fator + 1) / 10);
//			 // averRateSociety[fator] +=
//			 // rightRateSociety[fator][fileI - 1];
//			 // }
//			 pw.println(rightRate[fileI - 1]);// 输出正确值
//			 averRate += rightRate[fileI - 1];
//			 continue;
//			 } else if (value > -1) {
//			 num2++;
//			 pw.println(rightRate[fileI - 1]);// 输出正确值
//			 continue;// 跳出不进行num++;
//			 }
//			 }
//			 num++;
//			 pw.println("-1");// -1代表没有预测
//			 }
//			 if (245 > (num + num2)) {
//			 pw.println("aver:" + averRate / (245 - num - num2));
//			 }
//			 pw.println("失败率:" + (double) num2 / 245);
//			 averErrorRateSplit[lagPeriodI - 1] += (double) num2 / 245;
//			 pw.close();
//			 double max = 0;
//			 int result = 0;
//			 for (int fator = 0; fator < 10; fator++) {
//			 if (max < averRateSociety[fator]) {
//			 max = averRateSociety[fator];
//			 result = fator + 1;
//			 }
//			 }
//			 if (max < averRate) {
//			 max = averRate;
//			 }
//			 System.out.println(num);
//			 if (245 > (num + num2)) {
//			 System.out.println(lagPeriodI + "步，分段" + projectI
//			 + "马尔科夫平均正确率" + averRate
//			 / (245 - num - num2));
//			 // System.out.println("数据修正结果最好为" + result +
//			 // " 平均正确率" + max
//			 // / (245 - num - num2));
//			
//			 averRateSplit[lagPeriodI - 1] += averRate
//			 / (245 - num - num2);
//			 reaLen++;
//			 }
//			
//			 }
//			 }
//			 reaLen=reaLen/lagPeriodMax;
//			 for (int lagPeriodI = 1; lagPeriodI <= lagPeriodMax;
//			 lagPeriodI++) {// 步数循环
//			 averErrorRateSplit[lagPeriodI - 1] =
//			 averErrorRateSplit[lagPeriodI - 1]+timeStr.length
//			 - reaLen;
//			 System.out.println(lagPeriodI + "步为"
//			 + averRateSplit[lagPeriodI - 1] / timeStr.length
//			 + "平均失败率" + averErrorRateSplit[lagPeriodI - 1]
//			 / timeStr.length);
//			 // 求对于每一段总的平均正确率
//			 }
//			 } catch (Exception e) {
//			 }

			/*
			 * 进行停留的时间的预测
			 */
			 IDApTimeTest iatt = new IDApTimeTest();// 考虑上一点与时间的预测，可增大正确率
			 double[] apTimRightRate = new double[245];
			 double averRateTotal = 0;// 比较划分时的平均正确率
			 int errorTime = 1800;// 预测时间误差
			 System.out.println("########################");
			 try {
			 // 总数据
			 pw = new PrintWriter(new FileWriter(saveTest
			 + "/apTimeRightRate.csv"));
			
			 int num = 0;// 不能进行停留时间预测的个数
			 for (int i = 1; i <= 245; i++) {
			 double value = iatt.apTimeRightRate(
			 "F:/移动轨迹数据/result/idApTime/idApTime_" + i
			 + ".csv",
			 "F:/移动轨迹数据/idApTimeTest/idApTime_" + i + ".csv",
			 errorTime);
			 if (value > -1) {
			 apTimRightRate[i - 1] = value;
			 pw.println(apTimRightRate[i - 1]);
			 averRateTotal += apTimRightRate[i - 1];
			 } else {
			 num++;
			 pw.println("-1");
			 }
			
			 }
			 System.out.println(num);
			 if (245 > num) {
			 System.out.println(errorTime + "秒误差，停留时间预测平均正确率"
			 + averRateTotal / (245 - num));
			 // System.out.println("失败率:" + (double) num2 / 245 + "\n");
			 pw.println("aver:" + averRateTotal / (245 - num));
			 // pw.println("失败率:" + (double) num2 / 245);
			 }
			
			 pw.close();
			 double averRateSplit = 0;// 分段的平均正确率
			 // 分段数据
			 for (int projectI = 1; projectI <= timeStr.length; projectI++) {
			 double averRate = 0;// 比较划分时的平均正确率
			 pw = new PrintWriter(new FileWriter(saveTest
			 + "/apTimeRightRate" + projectI + ".csv"));
			 num = 0;// 不能进行停留时间预测的个数
			 for (int i = 1; i <= 245; i++) {
			 double value = iatt
			 .apTimeRightRate(
			 "F:/移动轨迹数据/timeDivide/result"
			 + projectI
			 + "/idApTime/idApTime_" + i
			 + ".csv",
			 "F:/移动轨迹数据/timeDivideTest/idApTimeTest"
			 + projectI + "/idApTime_" + i
			 + ".csv", errorTime);
			
			 if (value > -1) {
			 apTimRightRate[i - 1] = value;
			 pw.println(apTimRightRate[i - 1]);
			 averRate += apTimRightRate[i - 1];
			 } else {
			 num++;
			 pw.println("-1");
			 }
			
			 }
			 System.out.println(num);
			 if (245 > num) {
			 System.out.println("分段" + projectI + "停留时间预测平均正确率"
			 + averRate / (245 - num));
			 pw.println("aver:" + averRate / (245 - num));
			 pw.close();
			 averRateSplit += averRate / (245 - num);
			 }
			
			 }
			 System.out.println("分段后平均正确率" + averRateSplit / timeStr.length);
			 } catch (Exception e) {
			 }

			/*
			 * 总数据的组合预测
			 */
			 SimilarCombinationProb com = new SimilarCombinationProb();
			 try {
			 pw = new PrintWriter(new FileWriter(saveTest
			 + "/combinationRightRate.csv"));
			 com.init("F:/移动轨迹数据/result/stateList/stateList_", count);
			 com.similar();
			 com.validateMarkov();
			 double totalRate = 0;
			 int num = 0;// 不能够进行预测的数目
			 int num2 = 0;// 预测正确率较小的的数目
			 for (int i = 1; i <= 245; i++) {
			 double result = com.predictProb(
			 "F:/移动轨迹数据/stateListAddTimeTest/stateListAddTime_"
			 + i + ".csv", i);
			 //System.out.println(result);
			 pw.println(result);
			 if (result > 0.3) {
			 totalRate += result;
			 continue;
			 } else if (result > -1) {
			 num2++;
			 continue;
			 }
			 num++;
			 }
			 if (245 > (num + num2)) {
			 System.out.println("相似组合预测的平均正确率" + totalRate / (245 - num -
			 num2)
			 + "失败率" + (double) num2 / 245);
			 pw.println("相似组合预测的平均正确率" + totalRate / (245 - num - num2)
			 + "失败率" + (double) num2 / 245);
			 }
			 pw.close();
			
			 } catch (Exception e) {}
			
			 //社团组合修正转移矩阵
			 SocietyCombinationProb sop = new SocietyCombinationProb();
			 try {
			 pw = new PrintWriter(new FileWriter(saveTest
			 + "/societyRightRate.csv"));
			 sop.init("F:/移动轨迹数据/result/stateList/stateList_", count,
			 "F:/test/apTotalTime.txt", "F:/移动轨迹数据/apVisited");
			 sop.validateMarkov();
			 double totalRate = 0;
			 int num = 0;// 不能够进行预测的数目
			 int num2 = 0;// 预测正确率较小的的数目
			 for (int i = 1; i <= 245; i++) {
			 double result = sop.predictProb(
			 "F:/移动轨迹数据/stateListAddTimeTest/stateListAddTime_"
			 + i + ".csv", i, "F:/test/RESULT.txt");
			 //System.out.println(result);
			 pw.println(result);
			 if (result > 0.3) {
			 totalRate += result;
			 continue;
			 } else if (result > -1) {
			 num2++;
			 continue;
			 }
			 num++;
			
			 }
			 if (245 > (num + num2)) {
			 System.out.println("社团修正转移矩阵的平均正确率" + totalRate
			 / (245 - num - num2) + "失败率" + (double) num2 / 245);
			 pw.println("社团修正转移矩阵的平均正确率" + totalRate
			 / (245 - num - num2) + "失败率" + (double) num2 / 245);
			 }
			 pw.close();
			 } catch (Exception e) {
			 }

			/*
			 * 使用分段+组合+社团修正+二阶马尔科夫的方法
			 */

			 try {
			 // 组合预测初始化
			 //CombinationProb com = new CombinationProb();
			
			 int reaLen = 0;// 实际参与预测的段数，有的段数全部不符合马氏性，正确率为0，但如果除以reaLen，也会造成误差
			 double averRateSplit = 0;
			 // 分段的平均正确率,考虑步数
			 double averErrorRateSplit = 0;
			 // 分段的平均失败率,考虑步数
			 for (int projectI = 1; projectI <= 4; projectI++) {
			 // 初始化
			 spn.init(count, "F:/test/apTotalTime" + timeStr.length
			 + projectI + ".txt",
			 "F:/移动轨迹数据/timeDivide/apVisited" + projectI);
			 com.init("F:/移动轨迹数据/timeDivide/result" + projectI
			 + "/stateList/stateList_", count);
			 com.similar();
			 com.validateMarkov();
			 /*
			 * 另一种修正的方法
			 */
			 // sp.init(count, "F:/移动轨迹数据/timeDivide/result" +
			 // projectI,
			 // "F:/test/apTotalTime" + projectI + ".txt",
			 // "F:/移动轨迹数据/timeDivide/apVisited" + projectI);
			 int lagPeriodDis = 0;
			 // 步长差值，为了使各步长预测的数据相同，如果不考虑会造成短步长预测值多的情况，产生误差
			 double[] averRateSociety = new double[10];// 进行社团修正的平均正确率，考虑步数
			
			 String saveFile = saveTest + "/ResultTime" + projectI;
			 file = new File(saveFile);
			 if (!file.exists() && !file.isDirectory()) {
			 file.mkdir();
			 }
			 pw = new PrintWriter(new FileWriter(saveFile
			 + "/ResultComSoclagPeriod.csv"));
			 // 输出到文件,考虑步数，进行比较正确率
			
			 int num = 0;// 不能够进行预测的数目
			 int num2 = 0;// 预测正确率较小的的数目
			 for (int fileI = 1; fileI <= 245; fileI++) {
			 // System.out.println(fileI);
			 // 进行初始化
			 if (dm.init("F:/移动轨迹数据/timeDivide/result" + projectI
			 + "/stateList/stateList_" + fileI + ".csv",
			 count, lagPeriodMax)) {// 是否满足马氏性，满足马氏性才进行预测
			 String fileState =
			 "F:/移动轨迹数据/timeDivideTest/stateListAddTimeTest"
			 + projectI
			 + "/stateListAddTime_"
			 + fileI
			 + ".csv";
			 double[][] predictmarkov = dm.predictProb(
			 fileState, lagPeriodDis);// 进行两阶预测
			 double value = rst.rightRateTotal(predictmarkov,
			 fileState, lagPeriodMax);
			 if (value > -1) {
			 double[][] predictCom = com.predictProb2(
			 fileState, fileI);// 进行相似组合预测
			
			 // 社团预测
			 double[][] predictSociety = spn.predictTotal(
			 fileI, fileState, count,
			 "F:/移动轨迹数据/timeDivideTest/peopleIDNextApRemoveTest"
			 + projectI,
			 "F:/移动轨迹数据/timeDivideTest/idApTimeTest"
			 + projectI, "F:/test/RESULT"
			 + timeStr.length + projectI
			 + ".txt", lagPeriodMax);
			
			 // 进行组合+社团修正
			 for (int fator = 0; fator < 10; fator++) {
			 rightRateSociety[fator][fileI - 1] = rst
			 .rightRateSocietyCom(predictmarkov,
			 predictCom, predictSociety,
			 fileState, lagPeriodMax,
			 (double) (fator + 1) / 10);
			 averRateSociety[fator] += rightRateSociety[fator][fileI - 1];
			 }
			
			 }
			 if (rightRateSociety[0][fileI - 1] > errorRate) {
			 pw.println(rightRateSociety[0][fileI - 1]);// 输出正确值
			 continue;
			 } else if (value < errorRate
			 && rightRateSociety[0][fileI - 1] < errorRate
			 && value > -1) {
			 value = rightRateSociety[0][fileI - 1] > value ?
			 rightRateSociety[0][fileI - 1]
			 : value;
			 pw.println(value);// 输出正确值
			 num2++;
			 continue;
			 }
			 }
			 num++;
			 pw.println("-1");// -1代表没有预测
			 }
			 if (245 > (num + num2)) {
			 pw.println("aver:" + averRateSociety[0]
			 / (245 - num - num2));
			 }
			 pw.println("失败率:" + (double) num2 / 245);
			 System.out.println("失败率:" + (double) num2 / 245);
			 averErrorRateSplit += (double) num2 / 245;
			 pw.close();
			 double max = 0;
			 int result = 0;
			 for (int fator = 0; fator < 10; fator++) {
			 if (max < averRateSociety[fator]) {
			 max = averRateSociety[fator];
			 result = fator + 1;
			 }
			 }
			 System.out.println(num);
			 if (245 > (num + num2)) {
			 System.out.println("数据修正结果最好为" + result + " 平均正确率"
			 + max / (245 - num - num2));
			 averRateSplit += averRateSociety[0]
			 / (245 - num - num2);
			 reaLen++;
			 }
			
			 }
			 System.out.println("分段平均正确率为");
			 averErrorRateSplit += timeStr.length - reaLen;
			 System.out.println("2步为" + averRateSplit / timeStr.length
			 + "平均失败率" + averErrorRateSplit / timeStr.length);
			 // 求对于每一段总的平均正确率
			
			 } catch (Exception e) {
			 }
		}

		long end = System.currentTimeMillis();
		System.out.println("总耗时：" + (end - start) + "毫秒");
	}
}
