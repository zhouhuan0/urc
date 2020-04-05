package com.yks.urc.excel;


import com.alibaba.fastjson.JSON;
import com.yks.urc.fw.StringUtility;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

public class FileUpDownLoadUtils {
	private static Logger logger = LoggerFactory.getLogger(FileUpDownLoadUtils.class);



	/**
	 * 前端点击下载或者导出文件，通过此方法上传文件到网关文件系统，并返回下载链接
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String postFile(String url, String fileName) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String result = "";
		try {
			HttpPost httpPost = new HttpPost(url);
			FileBody bin = new FileBody(new File(fileName));
			HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.addPart("file", bin).setCharset(CharsetUtils.get("UTF-8")).build();

			httpPost.setEntity(reqEntity);

			// 发起请求 并返回请求的响应
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				int statusCode = response.getStatusLine().getStatusCode();
				if (200 == statusCode) {
					HttpEntity resEntity = response.getEntity();
					if (null != resEntity) {
						result = EntityUtils.toString(resEntity, Charset.forName("UTF-8"));
						
					}
					logger.info("上传文件到文件系统成功, url={}, fileName={}, result={}", url, fileName, result);
					// 销毁
					EntityUtils.consume(resEntity);
				} else {
					logger.error(String.format("uploadFile err:url=%s,fileName=%s,statusCode=%s,ContentLength=%s】",
							url, fileName, statusCode, bin.getContentLength()), new Exception("uploadFile err"));
					return null;
				}
			}
			catch (Exception ex){
				logger.error(String.format("postFile ERROR:%s %s", url, fileName), ex);
			}
			finally {
				IOUtils.closeQuietly(response);
			}
		} catch (Exception e) {
			logger.error("上传文件到文件系统异常, url={}, fileName={}", url, fileName, e);
			return null;
		} finally {
			if (null != httpClient) {
				try {
					httpClient.close();
				} catch (IOException e) {
					logger.error("上传文件到文件系统异常, url={}, fileName={}", url, fileName, e);
					return null;
				}
			}
		}
		return result;
	}

	
	public static String getDownloadUrl(String url, String fileName) {
		String result = postFile(url,fileName);
		if(!StringUtility.isNullOrEmpty(result)){
			FileUploadRespVO fileUploadRespVO = JSON.parseObject(result, FileUploadRespVO.class);
			
			return fileUploadRespVO.getData().get(0).getPath();
		}
		return null;
	}
//	public static String getFileName(String fileLastName) {
//		String timeStamp = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
//		return timeStamp + fileLastName;
//	}

	public static String getStaticFilePath(String modelPath) throws IOException
	{
		try
		{
			return new File("").getCanonicalPath() + modelPath;
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(),e);
			throw e;
		}

	}
}
