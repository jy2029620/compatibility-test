package reggie.com.compatibility_test;

import java.util.ArrayList;

/**
 * @author: Reggie
 * @data: 2016年9月28日 下午3:50:35
 * @version: V1.0
 */
public interface CompatibilityInterface {
	public ArrayList<String> getURL();// 获取下载路径

	public void download();// 下载方法1

	public void install();// 安装

	public void download2();// 下载方法2

	public void uninstall();// 卸载

	public void testMonkey();// 执行Monkey测试

	public void getLogs();// 抓取日志

	public void writeFile();// 写文件

	public ArrayList<String> readFile();// 读文件

	public void diffFile();// 对比文件

	public String getCurrentTime();// 获取当前时间

	public ArrayList<String> arrayListUniq(ArrayList<String> oldList);// arrayList去重

	public String adbCommand(String command);// 执行ADB命令

	public ArrayList<String> getAppLocalPathList();// 本地App目录

	public ArrayList<String> getPackageNameList();// 获取第三方App包名

}
