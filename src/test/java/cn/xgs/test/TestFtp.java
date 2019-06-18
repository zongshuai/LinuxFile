package cn.xgs.test;

import java.io.File;

import org.junit.Test;

import cn.xgs.file2linux.SFTPUtil;


public class TestFtp {

	@Test
	//测试文件上传
	public void testuploadfile() {
		File file = new File("D://jar");
		long startTime = System.currentTimeMillis();//获取当前时间
		Boolean uploadFile = SFTPUtil.uploadFile("uploadir", file);
		/*Boolean remoteZipToFile = false;
		if (uploadFile) {
			System.out.println("上传成功，开始解压");
			remoteZipToFile = ExtractUtils.remoteZipToFile("linux-4.20.tar.gz");
		}*/
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间："+(endTime-startTime)+"ms");
		System.out.println(uploadFile);
	}
	
	@Test
	//测试文件下载
	public void testdownloadfile() {
		long startTime = System.currentTimeMillis();//获取当前时间
		 Boolean download = SFTPUtil.download("","ubuntu16.04_qh.tar.gz", "/Users/zshuai/Desktop/img");
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间："+(endTime-startTime)+"ms");
		System.out.println(download);
	}
	
	@Test
	//测试文件删除
	public void testdeletefile() {
		long startTime = System.currentTimeMillis();//获取当前时间
		Boolean delete = SFTPUtil.delete("zshuaipath","linux-4.20.tar.gz");
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间："+(endTime-startTime)+"ms");
		System.out.println(delete);
	}
	@Test
	//测试文件夹的创建
	public void testmkdir() {
		long startTime = System.currentTimeMillis();//获取当前时间
		Boolean mkdir = SFTPUtil.mkdir("zshuaipath");
		long endTime = System.currentTimeMillis();
		System.out.println("程序运行时间："+(endTime-startTime)+"ms");
		System.out.println(mkdir);
	}
	
	

}
