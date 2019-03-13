package com.oasis.onebox.download;

import com.oasis.onebox.dao.DownloadTaskDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class HttpDownload implements Runnable, DownloadTaskDao {
	private final Log logger = LogFactory.getLog(HttpDownload.class);

	private String taskid;// task id
	private String userdir = "";// user directory
	private String url;// url address
	private String filename;// file name
	private long urlFileSize;// size of download file
	private long localFileSize;// size of local file
	private boolean continueTrans;// if resume
	private String message;
	private DownloadEnum downloadStatus;

	private boolean stopFlag = false;
	private Path localfiletmp;// local cache of download file
	private Path cfgpath;// config of download
	private int responseCode;// status of response
	private CloseableHttpResponse response = null;

	private int downloadSpeed;// speed of download

	public HttpDownload(String userdir, String url, String taskid) {
		this.taskid = taskid;
		this.userdir = userdir;
		this.url = url;
	}

	private boolean checkDownloadFile() {
		try {
			response = download(0);
			responseCode = response.getStatusLine().getStatusCode();
			switch (responseCode) {
			case 206:
				continueTrans = true;
				break;
			case 200:
				continueTrans = false;
				break;
			default:
				this.downloadStatus = DownloadEnum.ERROR;
				break;
			}
			urlFileSize = Integer.valueOf(response.getFirstHeader("Content-Length").getValue());
			getFileName();
			closeHttpResponse();
			return true;
		} catch (IOException e) {
			logger.error("download [" + url + "] ERROR", e);
			this.downloadStatus = DownloadEnum.ERROR;
			return false;
		}
	}

	//check local downloaded file
	private boolean checkLocalFile() throws IOException {
		Path localfile = Paths.get(userdir, filename);
		if (Files.exists(localfile, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
			this.downloadStatus = DownloadEnum.ERROR;
			this.message = "same name file on download directory, download has stopped";
			return false;
		} else {
			localfiletmp = Paths.get(localfile.toAbsolutePath().toString() + ".tmp");
			if (Files.exists(localfiletmp, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
				localFileSize = localfiletmp.toFile().length();
			} else {
				Files.createFile(localfiletmp);
			}
			cfgpath = Paths.get(localfile.toAbsolutePath().toString() + ".pcd.dl.cfg");// local cache of download file
			if (!Files.exists(cfgpath, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })) {
				Files.createFile(cfgpath);
			}
			FileWriter fw = new FileWriter(cfgpath.toFile());
			fw.write(url);
			fw.flush();
			fw.close();
			return true;
		}
	}

	public void run() {
		this.downloadStatus = DownloadEnum.READY;
		if (checkDownloadFile()) {
			try {
				if (checkLocalFile()) {
					startDownloadFile();
				}
			} catch (IOException e) {
				logger.error("download [" + url + "] ERROR", e);
				this.downloadStatus = DownloadEnum.ERROR;
				return;
			}
		}
	}

	private void startDownloadFile() {
		if (urlFileSize - localFileSize <= 0) {
			moveFile();
			downloadStatus = DownloadEnum.FINISH;
			downloadSpeed = 0;
			logger.info("Download " + filename + " success");
			return;
		}
		try {
			response = download(localFileSize);
			downloadStatus = DownloadEnum.PROCESS;
			InputStream is = response.getEntity().getContent();
			FileOutputStream fi = new FileOutputStream(localfiletmp.toFile());
			FileChannel channel = fi.getChannel();
			byte[] readBuffer = new byte[1048576];
			ByteBuffer writebuffer = ByteBuffer.allocate(1048576);
			long startTime = System.currentTimeMillis();
			int writeSize = 0;
			int readLength = -1;
			while ((readLength = is.read(readBuffer)) != -1 && !stopFlag) {
				writeSize += readLength;
				long endTime = System.currentTimeMillis();
				if (endTime - startTime > 1024) {
					downloadSpeed = writeSize;
					startTime = endTime;
					writeSize = 0;
				}
				if (continueTrans) {
					channel.position(localFileSize);
				}
				writebuffer.clear();
				writebuffer.put(readBuffer, 0, readLength);
				writebuffer.flip();
				while (writebuffer.hasRemaining()) {
					channel.write(writebuffer);
				}
				localFileSize += readLength;
			}
			writebuffer.clear();
			channel.close();
			fi.close();
			is.close();
			closeHttpResponse();
			startDownloadFile();
		} catch (IOException e) {
			logger.error("download [" + url + "] ERROR", e);
			this.downloadStatus = DownloadEnum.ERROR;
			return;
		}
	}


	private void moveFile() {
		try {
			Files.move(localfiletmp, Paths.get(userdir, filename), StandardCopyOption.REPLACE_EXISTING);
			Files.deleteIfExists(cfgpath);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private CloseableHttpResponse download(long startMark) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Range", String.format("bytes=%s-", startMark));
		return httpclient.execute(httpGet);
	}

	//get file name from response
	private void getFileName() {
		Header contentHeader = response.getFirstHeader("Content-Disposition");
		this.filename = null;
		if (contentHeader != null) {
			HeaderElement[] values = contentHeader.getElements();
			if (values.length == 1) {
				NameValuePair param = values[0].getParameterByName("filename");
				if (param != null) {
					try {
						filename = param.getValue();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (this.filename == null) {
			this.filename = url.substring(url.lastIndexOf("/") + 1);
		}
	}

	private void closeHttpResponse() {
		if (response != null) {
			try {
				response.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}


	private String getPercentComplete() {
		if (urlFileSize == 0 || localFileSize == 0) {
			return "0";
		}
		return new BigDecimal(localFileSize).multiply(new BigDecimal(100))
				.divide(new BigDecimal(urlFileSize), 2, BigDecimal.ROUND_HALF_EVEN).toString();
	}

	@Override
	public void stop() {
		stopFlag = true;
		this.downloadStatus = DownloadEnum.STOP;
		this.downloadSpeed = 0;
	}

	@Override
	public Map<String, String> getStatus() {
		Map<String, String> dlstatus = new HashMap<String, String>();
		dlstatus.put("taskid", taskid);
		dlstatus.put("url", url);
		dlstatus.put("filename", filename);
		dlstatus.put("filesize", calculateDescSize(urlFileSize));
		dlstatus.put("downloadSpeed", calculateDescSize(downloadSpeed) + "/S");
		dlstatus.put("percentcomplete", getPercentComplete());
		dlstatus.put("continuetrans", String.valueOf(continueTrans));
		dlstatus.put("status", downloadStatus.toString());
		dlstatus.put("message", message);
		return dlstatus;
	}

	@Override
	public void delete() {
		stopFlag = true;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		try {
			if (cfgpath != null) {
				Files.deleteIfExists(cfgpath);
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
