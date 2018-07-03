package com.yks.urc.log;
 
import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.Date;
 
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
 

public class Log4jRollingFileAppender extends RollingFileAppender {
 
	private long nextRollover = 0;
	
	public void rollOver() {
		File target;
		File file;
		
		if (qw != null) {
			long size = ((CountingQuietWriter) qw).getCount();
			nextRollover = size + maxFileSize;
		}
		LogLog.debug("maxBackupIndex=" + maxBackupIndex);
 
		boolean renameSucceeded = true;
		if (maxBackupIndex > 0) {
			for (int i = maxBackupIndex - 1; i >= 1 && renameSucceeded; i--) {
				file = new File(genFileName(fileName, i));
				if (file.exists()) {
					target = new File(genFileName(fileName, i + 1));
					renameSucceeded = file.renameTo(target);
				}
			}
 
			if (renameSucceeded) {
				target = new File(genFileName(fileName, 1));
 
				this.closeFile();
 
				file = new File(fileName);
				renameSucceeded = file.renameTo(target);
 
				if (!renameSucceeded) {
					try {
						this.setFile(fileName, true, bufferedIO, bufferSize);
					} catch (IOException e) {
						if (e instanceof InterruptedIOException) {
							Thread.currentThread().interrupt();
						}
						LogLog.error("setFile(" + fileName
								+ ", true) call failed.", e);
					}
				}
			}
		}
		if (renameSucceeded) {
			try {
				this.setFile(fileName, false, bufferedIO, bufferSize);
				nextRollover = 0;
			} catch (IOException e) {
				if (e instanceof InterruptedIOException) {
					Thread.currentThread().interrupt();
				}
				LogLog.error("setFile(" + fileName + ", false) call failed.", e);
			}
		}
	}
 
	private String genFileName(String name, int index) {
		String val = "_";
		String fileName = "";
		if (index > 0) {
			String num = index < 20 ? "" + index : String.valueOf(index);
			fileName = name.replace(".log", "") + val + num + ".log";
		} else {
			fileName = name;
		}
		return fileName;
	}
 
	protected void subAppend(LoggingEvent event) {
		super.subAppend(event);
		if (fileName != null && qw != null) {
			long size = ((CountingQuietWriter) qw).getCount();
			if (size >= maxFileSize && size >= nextRollover) {
				rollOver();
			}
		}
	}
	
	public void setFile(String file) {
	    String val = file.trim();
	    fileName = val.substring(0, val.lastIndexOf("/"))+"/"+new SimpleDateFormat("yyyyMMdd").format(new Date())+val.substring(val.lastIndexOf("/"), val.lastIndexOf("."))+"_"+new SimpleDateFormat("yyyyMMdd_HH").format(new Date())+val.substring(val.lastIndexOf("."), val.length());

	}
	
}
