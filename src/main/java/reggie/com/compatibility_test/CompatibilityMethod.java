package reggie.com.compatibility_test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * @author: Reggie
 * @data: 2016年9月28日 下午3:50:10
 * @version: V1.0
 */
public class CompatibilityMethod implements CompatibilityInterface {

	public ArrayList<String> getURL() {
		ArrayList<String> url_list = new ArrayList<String>();
		try {
			Document doc = Jsoup.connect(CompatibilityEnum.http_url.getValue()).timeout(5000).get();
			Elements e1 = doc.getElementsByClass("com-install-btn");
			// System.out.println(e1);
			for (int i = 0; i < e1.size(); i++) {
				String ei = e1.get(i).attr("ex_url");
				String ex_url = ei.split("&")[0].trim();
				// System.out.println(ex_url);
				url_list.add(ex_url);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return url_list;
	}

	public void download() {
		ArrayList<String> urlList = readFile();
		String localPath = "D://android_myapp_" + getCurrentTime();

		File mDirectoryPath = new File(localPath);
		if (!mDirectoryPath.exists()) {
			mDirectoryPath.mkdirs();
		}

		InputStream inputStream = null;
		OutputStream outputStream = null;

		if (urlList.size() != 0) {
			for (int i = 0; i < urlList.size(); i++) {
				String fileName = urlList.get(i).split("=")[1].trim();
				System.out.println(fileName);
				try {
					URL url = new URL(urlList.get(i));
					URLConnection uRLConnection = url.openConnection();
					inputStream = uRLConnection.getInputStream();
					outputStream = new FileOutputStream(new File(localPath, fileName));
					byte[] buf = new byte[1024];
					int len = 0;
					while ((len = inputStream.read(buf)) != -1) {
						outputStream.write(buf, 0, len);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		}
		try {
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void install() {
		ArrayList<String> apkPathList = getAppLocalPathList();
		for (int i = 0; i < apkPathList.size(); i++) {
			// System.out.println(Arrays.toString(apkPathList.get(i).split("android_myapp_20160921111114")));
			String[] apkList = apkPathList.get(i).split("android_myapp_20160921111114");
			String apkList2 = apkList[apkList.length - 1];
			// System.out.println(apkList2);
			String apkList3 = apkList2.substring(1, apkList2.length());
			// System.out.println(apkList3);
			String app_name = apkList3;
			System.out.println("正在安装" + app_name);
			adbCommand("adb install " + apkPathList.get(i));
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void uninstall() {
		ArrayList<String> pckNameList = getPackageNameList();
		for (int i = 0; i < pckNameList.size(); i++) {
			System.out.println("正在卸载" + pckNameList.get(i));
			adbCommand("adb uninstall " + pckNameList.get(i));
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void testMonkey() {
		ArrayList<String> pck_name = new ArrayList<String>();
		pck_name.add("com.google.android.deskclock");
		pck_name.add("com.android.settings");
		pck_name.add("com.google.android.calculator");
		System.out.println("Monkey Test Start...");
		for (int i = 0; i < pck_name.size(); i++) {
			System.out.println("被测App:" + pck_name.get(i));
			adbCommand("adb shell monkey -p " + pck_name.get(i) + " -s 10 --throttle 500 -v 2000");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("End.");
	}

	public void getLogs() {
		StringBuffer stringBuffer = null;
		FileOutputStream fos = null;
		BufferedWriter bufw = null;
		try {
			Process process = Runtime.getRuntime().exec("adb logcat -v time");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			int mmLine = 100;
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains("FATAL EXCEPTION") || line.contains("ANR in")) {
					stringBuffer = new StringBuffer();
					try {
						fos = new FileOutputStream(
								System.getProperty("user.dir") + "\\logs\\" + getCurrentTime() + ".log");
						bufw = new BufferedWriter(new OutputStreamWriter(fos));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					mmLine = 0;
				}
				if (mmLine < 50) {
					bufw.write(line + "\n");
					stringBuffer.append(line + "\n");
					mmLine++;
					// 当mmLine等于50时,关闭流
					if (mmLine == 50) {
						bufw.close();
						fos.close();
					}
				}
				// if (mmLine == 49) {
				// stringBuffer.toString();
				// System.out.println(stringBuffer.toString());
				// }

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void writeFile() {
		String directoryPath = System.getProperty("user.dir") + "\\repository\\url";
		File mDirectoryPath = new File(directoryPath);

		if (!mDirectoryPath.exists()) {
			mDirectoryPath.mkdirs();
		}
		File file1 = new File(mDirectoryPath + "\\Url_" + getCurrentTime() + ".txt");
		if (!file1.exists()) {
			try {
				file1.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		ArrayList<String> oldList = getURL();
		System.out.println("oldList:" + oldList.size());
		// 使用网络爬虫技术，获取App下载链接
		// App下载链接去重操作
		ArrayList<String> newList = arrayListUniq(oldList);
		System.out.println("newList:" + newList.size());
		// 打印newList
		// for (int i = 0; i < newList.size(); i++) {
		// System.out.println(newList.get(i));
		// }

		FileOutputStream fileOutputStream = null;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			fileOutputStream = new FileOutputStream(file1, true);
			for (int i = 0; i < newList.size(); i++) {
				stringBuffer.append(newList.get(i) + "\n");
			}
			// 将newList写入到文件
			fileOutputStream.write(stringBuffer.toString().getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public ArrayList<String> readFile() {
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		ArrayList<String> arrayList = new ArrayList<String>();
		String line = "";
		try {
			File file1 = new File(System.getProperty("user.dir") + "\\repository\\url\\Url_20160921105009.txt");
			fileInputStream = new FileInputStream(file1);
			inputStreamReader = new InputStreamReader(fileInputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			while ((line = bufferedReader.readLine()) != null) {
				arrayList.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				inputStreamReader.close();
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return arrayList;

	}

	public void diffFile() {
		// TODO Auto-generated method stub

	}

	public String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(date);
	}

	public ArrayList<String> arrayListUniq(ArrayList<String> oldList) {
		ArrayList<String> newList = new ArrayList<String>();
		Iterator<String> it = oldList.iterator();
		while (it.hasNext()) {
			String a = it.next();
			if (newList.contains(a)) {
				it.remove();
			} else {
				newList.add(a);
			}
		}
		return newList;
	}

	public void download2() {
		// 读取本地的url_xxx.txt文件
		ArrayList<String> urlList = readFile();
		String localPath = "D://android_myapp_" + getCurrentTime();

		File mDirectoryPath = new File(localPath);
		if (!mDirectoryPath.exists()) {
			mDirectoryPath.mkdirs();
		}

		InputStream inputStream = null;
		OutputStream outputStream = null;
		// 判断url_xxx.txt文件是否为空
		if (urlList.size() != 0) {
			// 使用while语句控制下载App的数目
			// 计算下载耗时
			long start_time = System.currentTimeMillis();
			int j = 0;
			while (j < urlList.size()) {
				// System.out.println(j);
				String fileName = urlList.get(j).split("=")[1].trim();
				System.out.println(fileName);
				try {
					URL url = new URL(urlList.get(j));
					URLConnection uRLConnection = url.openConnection();
					inputStream = uRLConnection.getInputStream();
					outputStream = new FileOutputStream(new File(localPath, fileName));
					byte[] buf = new byte[1024];
					int len = 0;
					while ((len = inputStream.read(buf)) != -1) {
						outputStream.write(buf, 0, len);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (j == 4) {
					break;
				}
				j++;
			}
			long end_time = System.currentTimeMillis();
			long add_time = end_time - start_time;
			System.out.println("下载耗时:" + add_time + "milliseconds(毫秒)");

		}
		try {
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String adbCommand(String command) {
		String commandResult = null;
		BufferedReader in = null;
		StringBuffer stringBuffer = new StringBuffer();
		try {
			Process proc = Runtime.getRuntime().exec(command);
			proc.waitFor();
			in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line);
			}
			commandResult = stringBuffer.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return commandResult;
	}

	public ArrayList<String> getAppLocalPathList() {
		String filePath = "D://android_myapp_20160921111114";
		ArrayList<String> apkPathList = new ArrayList<String>();
		File file1 = new File(filePath);
		if (!file1.isDirectory()) {
			// System.out.println("111");
			// System.out.println(file1.getName());
		} else if (file1.isDirectory()) {
			// System.out.println("222");
			String[] fileList = file1.list();
			for (int i = 0; i < fileList.length; i++) {
				File file2 = new File(filePath + "\\" + fileList[i]);
				if (!file2.isDirectory()) {
					apkPathList.add(file2.getAbsolutePath());
				}
			}
			// Iterator<String> i = apkPathList.iterator();
			// while(i.hasNext()){
			// System.out.println(i.next());
			// }
		}
		return apkPathList;
	}

	public ArrayList<String> getPackageNameList() {
		ArrayList<String> pckNameList = new ArrayList<String>();// 存放应用包名
		try {
			Process proc = Runtime.getRuntime().exec("adb shell pm list packages -3");
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while ((line = in.readLine()) != null) {
				stringBuffer.append(line + " ");
			}
			String[] result = stringBuffer.toString().split("  ");
			for (int i = 0; i < result.length; i++) {
				String pckName = result[i].split(":")[1].trim();
				pckNameList.add(pckName);// 将pckName添加到pckNameList中
				System.out.println(pckName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pckNameList;
	}
}
