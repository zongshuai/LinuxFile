package cn.xgs.file2linux;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.opensymphony.xwork2.util.finder.ClassFinder.Info;
/**
 * @version: 1.0
 * @Description:文件上传/删除/解压缩
 * @author: zshuai
 * @date: 2019年4月9日
 */
public class SFTPUtil {
	private static final Logger LOG = LoggerFactory.getLogger(SFTPUtil.class);

	/*
	 * @Description: 获取文件上传的安全通道
	 * @param session
	 * @return
	 */
	public static Channel getChannel(Session session) {
		Channel channel = null;
		try {
			channel = session.openChannel("sftp");
			channel.connect();
			System.out.println("获取连接成功");
			LOG.info("get Channel success!");
		} catch (JSchException e) {
			LOG.info("get Channel fail!", e);
		}
		return channel;
	}

	/*
	 * @Description:获取连接信息，返回session，在session中获取安全通道
	 * @param host：连接主机ip
	 * @param port:端口号，一般sftp依托于ssh。端口号22
	 * @param username：用户名
	 * @param password：密码
	 * @return
	 */
	public static Session getSession(String host, int port, String username, final String password) {
		Session session = null;
		try {
			JSch jsch = new JSch();
			jsch.getSession(username, host, port);
			session = jsch.getSession(username, host, port);
			session.setPassword(password);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			session.setConfig(sshConfig);
			session.connect();
			LOG.info("Session connected!");
		} catch (JSchException e) {
			LOG.info("get Channel failed!", e);
		}
		return session;
	}

	/*
	 * @Description:创建文件夹
	 * @param sftp
	 * @param dir : 创建的文件夹名字
	 */
	public static Boolean mkdir(String dir) {
		Session s = getSession(SFTPInfo.SFTP_REQ_HOST, SFTPInfo.SFTP_DEFAULT_PORT, SFTPInfo.SFTP_REQ_USERNAME,
				SFTPInfo.SFTP_REQ_PASSWORD);
		Channel channel = getChannel(s);
		ChannelSftp sftp = (ChannelSftp) channel;
		Boolean result = false;
		try {
			sftp.cd("/");//相当于在linux命令行执行cd / ，然后在打开的目录下创建
			sftp.mkdir(dir);
			System.out.println("创建文件夹成功！");
			result = true;
		} catch (SftpException e) {
			System.out.println("创建文件夹失败！");
			result =false;
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * @Description: 文件上传的方法
	 * @param sftp : 客户端
	 * @param dir : 指定上传文件的目录
	 * @param file : 上传的文件
	 * @return :
	 */
	public static Boolean uploadFile(String dir, File file) {
		
		Session s = getSession(SFTPInfo.SFTP_REQ_HOST, SFTPInfo.SFTP_DEFAULT_PORT, SFTPInfo.SFTP_REQ_USERNAME,
				SFTPInfo.SFTP_REQ_PASSWORD);
		Channel channel = getChannel(s);
		ChannelSftp sftp = (ChannelSftp) channel;
		Boolean result =false;
		try {
			sftp.cd("/"+dir);
			System.out.println("打开目录");
			if (file != null) {
				sftp.put(new FileInputStream(file), file.getName());
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
			LOG.info("上传失败！", e);
			result = false;
		}
		closeAll(sftp, channel, s); // 关闭连接
		return result;
	}

	/**
	 * @Description: 文件下载
	 * @param directory    下载目录
	 * @param downloadFile 下载的文件
	 * @param saveFile     存在本地的路径
	 * @param sftp
	 */
	public static Boolean download(String directory, String downloadFile, String saveFile) {
		Session s = getSession(SFTPInfo.SFTP_REQ_HOST, SFTPInfo.SFTP_DEFAULT_PORT, SFTPInfo.SFTP_REQ_USERNAME,
				SFTPInfo.SFTP_REQ_PASSWORD);
		Channel channel = getChannel(s);
		ChannelSftp sftp = (ChannelSftp) channel;
		Boolean result =false;
		try {
			sftp.cd("/"+directory);
			sftp.get(downloadFile, saveFile);
			result = true;
		} catch (Exception e) {
			result = false;
			LOG.info("下载失败！", e);
			;
		}
		return result;
	}
	/**
	 * @Description: 文件删除
	 * @param directory  要删除文件所在目录
	 * @param deleteFile 要删除的文件
	 * @param sftp
	 */
	public static Boolean delete(String directory, String deleteFile ) {
		Session s = getSession(SFTPInfo.SFTP_REQ_HOST, SFTPInfo.SFTP_DEFAULT_PORT, SFTPInfo.SFTP_REQ_USERNAME,
				SFTPInfo.SFTP_REQ_PASSWORD);
		Channel channel = getChannel(s);
		ChannelSftp sftp = (ChannelSftp) channel;
		Boolean result = false;
		try {
			sftp.cd("/"+directory);
			sftp.rm(deleteFile);
			result = true;
		} catch (Exception e) {
			result = false;
			LOG.info("删除失败！", e);
		}
		return result;
	}

	private static void closeChannel(Channel channel) {
		if (channel != null) {
			if (channel.isConnected()) {
				channel.disconnect();
			}
		}
	}

	private static void closeSession(Session session) {
		if (session != null) {
			if (session.isConnected()) {
				session.disconnect();
			}
		}
	}

	public static void closeAll(ChannelSftp sftp, Channel channel, Session session) {
		try {
			closeChannel(sftp);
			closeChannel(channel);
			closeSession(session);
		} catch (Exception e) {
			LOG.info("closeAll", e);
		}
	}
}