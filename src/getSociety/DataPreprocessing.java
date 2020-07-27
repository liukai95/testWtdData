package getSociety;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


public class DataPreprocessing {
	private double[][] linkMatrix;// 原始数据，在算法中不断删除边
	private double[][] originMatrix;// 用于存储原始数据
	public List<Double> Qlist;// Q的集合
	public List<edgeInfo> deleteEgdes;// 删除的边
	public ArrayList<nodeInfo> previousNodes;// Gn算法中一次已经成功入队的节点
	public ArrayList<nodeInfo> currentNodes;// Gn算法中的当前节点
	public ArrayList<ArrayList<nodeInfo>> resultList;
	public ArrayList<nodeInfo> record;// Gn算法中上一次访问过节点
	public double[] disArray;// Gn算法中一次的各个边的距离
	public double[] weightArray;// Gn算法中一次的各边的权重
	public double[][] disMatrix;// 用于记录源节点与其他节点的距离
	public double[][] sumDisMatix;// 记录边介数
	private int MAX_NUM;// 最大的数目
	private int[] belong;// 用于区别节点所述社团

	// 节点对象
	class nodeInfo {
		public List<Integer> parentNodes;// 父节点
		public List<Integer> childrenNodes; // 子节点
		public int name;

		// public double weightOfNode;//点权,
		public nodeInfo(int n) {
			parentNodes = new ArrayList<Integer>();
			childrenNodes = new ArrayList<Integer>();
			name = n;
			// weightOfNode=1;

		}

	}

	// 边对象
	class edgeInfo {
		public int start;// 开始的节点
		public int end;// 结束的节点
		public double weightOfEdge;// 边权

		public void setInfo(int a, int b, double c) {
			this.start = a;
			this.end = b;
			this.weightOfEdge = c;
		}
	}

	// 初始化变量
	public void init(String openFile) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					openFile)));
			String read = br.readLine();
			String[] str = read.split(",");
			MAX_NUM = str.length;
			System.out.println(MAX_NUM);
			linkMatrix = new double[MAX_NUM][MAX_NUM];
			originMatrix = new double[MAX_NUM][MAX_NUM];
			for (int i = 0; i < MAX_NUM; i++) {
				linkMatrix[i] = new double[MAX_NUM];
				originMatrix[i] = new double[MAX_NUM];
			}
			for (int j = 0; j < MAX_NUM; j++) {
				linkMatrix[0][j] = Double.parseDouble(str[j]);
				originMatrix[0][j] = linkMatrix[0][j];
			}
			int i = 1;
			while ((read = br.readLine()) != null) {
				str = read.split(",");
				for (int j = 0; j < MAX_NUM; j++) {
					linkMatrix[i][j] = Double.parseDouble(str[j]);
					originMatrix[i][j] = linkMatrix[i][j];
				}
				i++;
			}
			br.close();

		} catch (Exception e) {
		}

		Qlist = new ArrayList<Double>();
		deleteEgdes = new ArrayList<edgeInfo>();
		resultList = new ArrayList<ArrayList<nodeInfo>>();

	}

	public nodeInfo findNodeInfoById(int n, ArrayList<nodeInfo> al) {
		for (int i = 0; i < al.size(); i++) {
			if (al.get(i).name == n)
				return al.get(i);
		}
		return null;
	}

	public boolean isNodeOut(int n) {
		for (int i = 0; i < MAX_NUM; i++) {
			if (linkMatrix[n][i] > 0)
				return false; // 只要和该节点和其他点有联系就需要计算
		}
		return true;
	}

	public void calGn() {
		while (true) { // 每次删除一条边，直到删除所有
			sumDisMatix = new double[MAX_NUM][MAX_NUM];
			belong = new int[MAX_NUM];
			// 使用GN算法计算每一个点，从而得出边介数
			for (int i = 0; i < MAX_NUM; i++) {
				if (isNodeOut(i) == false) {
					calOneNodeGn(i);
				}

			}
			double max = 0;
			int x = 0, y = 0;
			// 累加Q值
			for (int i = 0; i < MAX_NUM; i++)
				for (int j = 0; j < MAX_NUM; j++) {
					if (linkMatrix[i][j] > 0)
						sumDisMatix[i][j] = sumDisMatix[i][j]
								/ linkMatrix[i][j]; // 计算边权比
					else {
						sumDisMatix[i][j] = 0;
					}
				}
			for (int i = 0; i < MAX_NUM; i++) {
				for (int j = 0; j < MAX_NUM; j++) {
					if (max < sumDisMatix[i][j]) {
						max = sumDisMatix[i][j];
						x = i;
						y = j;
					}
				}
			}
			if (max == 0)
				break;
			calQ();
			deleEdge(x, y);
			// System.out.println("删除"+x + " " + y);

		}
		// 查找最大的Q值，此时为最佳划分
		double maxq = 0;
		int index = 0;
		for (int i = 0; i < Qlist.size(); i++) {
			if (maxq < Qlist.get(i)) {
				maxq = Qlist.get(i);
				index = i;
			}
		}
		System.out.println("maxQ:" + maxq + " " + "index:" + index);
		for (int i = 0; i < index; i++) {
			edgeInfo deInfo = deleteEgdes.get(i);
			originMatrix[deInfo.start][deInfo.end] = 0;
			originMatrix[deInfo.end][deInfo.start] = 0;
		}
	}

	// 计算所有用户属于的社团
	public void society() {
		Set<Integer> result = new LinkedHashSet<Integer>();
		Queue<Integer> queue = new LinkedList<Integer>();
		Map<Integer, List<Integer>> strList = new LinkedHashMap<Integer, List<Integer>>();
		for (int i = 0; i < MAX_NUM; i++) {
			for (int j = i + 1; j < MAX_NUM; j++) {
				if (originMatrix[i][j] > 0) {
					if (strList.containsKey(i)) {
						List<Integer> list = strList.get(i);
						list.add(j);
						strList.put(i, list);

					} else {
						List<Integer> list = new ArrayList<Integer>();
						list.add(j);
						strList.put(i, list);
					}
				}
			}
		}
		int num = 1;// 社团代号，默认为0,即不属于任何社团
		while (strList.size() > 0) {
			int first = strList.entrySet().iterator().next().getKey();
			queue.add(first);// 加入第一个AP
			while (queue.size() > 0) {
				int v = queue.poll();
				result.add(v);
				if (strList.containsKey(v)) {
					List<Integer> apList = strList.get(v);
					for (int value : apList) {
						queue.add(value);
					}
				}
				strList.remove(v);
			}
			// 遍历Set,输出结果
			for (int value : result) {
				belong[value] = num;
			}
			num++;
			result.clear();
		}
	}

	// 输出结果,先要计算一下社团
	public void print(String saveFile) {
		// 输出结果
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(saveFile));

			Set<Integer> result = new LinkedHashSet<Integer>();
			Queue<Integer> queue = new LinkedList<Integer>();
			Map<Integer, List<Integer>> strList = new LinkedHashMap<Integer, List<Integer>>();
			List<String> no = new LinkedList<String>();// 没有社团的id
			for (int i = 1; i <= MAX_NUM; i++) {
				no.add(i + "");
			}
			for (int i = 0; i < MAX_NUM; i++) {
				for (int j = i + 1; j < MAX_NUM; j++) {
					if (originMatrix[i][j] > 0) {
						if (strList.containsKey(i)) {
							List<Integer> list = strList.get(i);
							list.add(j);
							strList.put(i, list);

						} else {
							List<Integer> list = new ArrayList<Integer>();
							list.add(j);
							strList.put(i, list);
						}
						if (no.contains(i + 1 + ""))
							no.remove(i + 1 + "");
						if (no.contains(j + 1 + ""))
							no.remove(j + 1 + "");
					}
				}
			}
			for (String s : no)
				pw.print(s + " ");// 先输出不属于任何社团的用户
			pw.println();
			while (strList.size() > 0) {
				int first = strList.entrySet().iterator().next().getKey();
				queue.add(first);// 加入第一个AP
				while (queue.size() > 0) {
					int v = queue.poll();
					result.add(v);
					if (strList.containsKey(v)) {
						List<Integer> apList = strList.get(v);
						for (int value : apList) {
							queue.add(value);
						}
					}
					strList.remove(v);
				}
				// 遍历Set,输出结果
				for (int value : result) {
					pw.print(value + 1 + " ");
				}
				pw.println();
				result.clear();
			}
			pw.close();
		} catch (IOException e) {
		}
	}

	// 计算Q的值
	public double calQ() {
		double Q = 0;
		double M = 0;
		double sum = 0;
		double[] k = new double[MAX_NUM];
		for (int i = 0; i < MAX_NUM; i++) {
			for (int j = 0; j < MAX_NUM; j++) {
				M += linkMatrix[i][j];
				k[i] += linkMatrix[i][j];

			}
		}
		M = M / 2;
		society();
		for (int i = 0; i < MAX_NUM; i++) {
			for (int j = i + 1; j < MAX_NUM; j++) {// 选择j=i+1,可以加快速度
				if (belong[i] != 0 && belong[i] == belong[j]) // i与j属于同一个社团
					sum += (linkMatrix[i][j] - k[i] * k[j] / (2 * M));
			}
		}
		Q = sum / M;
		Qlist.add(Q);
		// System.out.print(Q + "\n");
		return Q;
	}

	// 计算一个节点
	public void calOneNodeGn(int n) {
		if (isNodeOut(n) == true)
			return;
		disMatrix = new double[MAX_NUM][MAX_NUM];
		previousNodes = new ArrayList<DataPreprocessing.nodeInfo>();
		disArray = new double[MAX_NUM];
		weightArray = new double[MAX_NUM];
		Boolean isStop = false;
		int deep1 = 2;
		currentNodes = new ArrayList<DataPreprocessing.nodeInfo>();
		singleNodeGNFirst(new nodeInfo(n), 1);
		// 不断往下寻找，建立以节点n为源节点的树
		while (isStop == false) {
			isStop = true;
			record = currentNodes;
			currentNodes = new ArrayList<DataPreprocessing.nodeInfo>();
			for (int i = 0; i < record.size(); i++) {
				boolean ldl = singleNodeGN(record.get(i), deep1);// 如果还有子节点，继续循环
				if (ldl == true)
					isStop = false;
			}
			deep1++;
			// System.out.print(previousNodes.size()+"\n");
		}
		for (int i = 0; i < previousNodes.size(); i++) {
			nodeInfo ne = previousNodes.get(i);
			if (ne.childrenNodes.size() == 0)
				for (int j = 0; j < ne.parentNodes.size(); j++) {
					int x = ne.parentNodes.get(j);
					disMatrix[i][x] = weightArray[x] / weightArray[i];
					disMatrix[x][i] = disMatrix[i][x];
				}
		}// 对叶子节点进行赋初值
		double max = 0;
		for (int i = 0; i < MAX_NUM; i++)
			if (max < disArray[i]) {
				max = disArray[i];
			}

		// 进行第二步,计算各边的权重
		for (int i = previousNodes.size() - 1; i >= 0; i--)
			singleSencondStep(previousNodes.get(i).name, n);
		// 计算边介数
		for (int i = 0; i < MAX_NUM; i++)
			for (int j = 0; j < MAX_NUM; j++) {
				sumDisMatix[i][j] += disMatrix[i][j];
			}
	}

	// 计算各边的权重
	public void singleSencondStep(int n, int source) {
		nodeInfo neInfo = findNodeInfoById(n, previousNodes);
		for (int j = 0; j < neInfo.parentNodes.size(); j++) {
			nodeInfo pa = findNodeInfoById(neInfo.parentNodes.get(j),
					previousNodes);
			double q = 1;
			for (int k = 0; k < neInfo.childrenNodes.size(); k++)
				q += disMatrix[n][neInfo.childrenNodes.get(k)];
			int x = neInfo.parentNodes.get(j);
			if (pa.name != source) {
				if (weightArray[n] != 0) {
					disMatrix[n][x] = weightArray[x] * q / weightArray[n];
					disMatrix[x][n] = disMatrix[n][x];
				}
			} else {
				disMatrix[n][x] = q;
				disMatrix[x][n] = disMatrix[n][x];
			}

		}

	}

	// 广度优先遍历源节点，并进行标记
	public Boolean singleNodeGNFirst(nodeInfo nn, int deep) {
		// System.out.println(deep + "\n");
		boolean res = true;
		int n = nn.name;
		previousNodes.add(nn);
		for (int i = 0; i < MAX_NUM; i++) {
			if (linkMatrix[n][i] > 0) {

				nodeInfo ne = new nodeInfo(i);
				nodeInfo parentInfo = findNodeInfoById(n, previousNodes);
				parentInfo.childrenNodes.add(i);// 为父子关系
				ne.parentNodes.add(n);
				currentNodes.add(ne);
				disArray[i] = deep;
				weightArray[i] = 1;
			}
		}

		for (int j = 0; j < MAX_NUM; j++) {
			if (linkMatrix[n][j] > 0) {
				if (disArray[j] == 0)
					res = false;
			}
		}
		return res;
	}

	// 广度优先遍历节点
	public Boolean singleNodeGN(nodeInfo nn, int deep) {
		// System.out.println(deep + "\n");
		boolean res = false;
		int n = nn.name;

		if (findNodeInfoById(n, previousNodes) == null)
			previousNodes.add(nn);
		for (int i = 0; i < MAX_NUM; i++) {
			if (linkMatrix[n][i] > 0) {

				nodeInfo ne = new nodeInfo(i);

				if (findNodeInfoById(n, record) != null
						&& (findNodeInfoById(i, record) != null))
					break;
				if ((findNodeInfoById(i, previousNodes) == null)) {
					nodeInfo parentInfo = findNodeInfoById(n, previousNodes);

					if (findNodeInfoById(i, currentNodes) == null) {
						parentInfo.childrenNodes.add(i);// 为父子关系
						ne.parentNodes.add(n);//
						currentNodes.add(ne);// 如果在当前访问节点中为空并且从未被访问过
						res = true;
					} else {
						{
							parentInfo.childrenNodes.add(i);// 为父子关系
							findNodeInfoById(i, currentNodes).parentNodes
									.add(n);
							res = true;
						}
					}
					if (disArray[i] == 0) {
						disArray[i] = deep;

						weightArray[i] = weightArray[parentInfo.name];
					} else if (disArray[i] == deep) {

						weightArray[i] = weightArray[n] + weightArray[i];
					} else if (disArray[i] < deep)
						;
					else {
						;
					}
				}

			}
		}
		return res;
	}

	public void deleEdge(int start, int end) {
		edgeInfo deInfo = new edgeInfo();
		deInfo.setInfo(start, end, linkMatrix[start][end]);
		deleteEgdes.add(deInfo);
		linkMatrix[start][end] = 0;
		linkMatrix[end][start] = 0;
	}

	public static void main(String[] args) {

		long start =System.currentTimeMillis();
		DataPreprocessing testDataPreprocessing = new DataPreprocessing();
		// 总数据
		testDataPreprocessing.init("F:\\test\\apTotalRate.txt");
		testDataPreprocessing.calGn();
		testDataPreprocessing.print("F:\\test\\RESULT.txt");
		// 分段数据
		for (int fileI = 1; fileI <= 6; fileI++) {
			testDataPreprocessing
					.init("F:\\test\\apTotalRate" + fileI + ".txt");
			testDataPreprocessing.calGn();
			testDataPreprocessing.print("F:\\test\\RESULT" + fileI + ".txt");

		}
		long end =System.currentTimeMillis();
		System.out.println("总耗时：" + (end - start) + "毫秒");

	}

}
