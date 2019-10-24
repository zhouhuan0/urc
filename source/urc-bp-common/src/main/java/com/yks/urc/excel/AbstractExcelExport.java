package com.yks.urc.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: TODO
 * @author: zengzheng 
 * @version: 2019年10月23日 下午2:50:06
 */
public abstract class AbstractExcelExport {
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected static final int TITLE_ROW_NUM = 0;

    // excel
    protected Workbook workbook;

    //数据sheet
    protected Sheet sheet;

    //导出标题
    protected List<String> excelTitle;

    protected String exportFilePath;

    public void initExportExcel() {
        try {
            createWorkBook();

            //创建表头
            createExcelTitle();

            //写Excel数据
            writeExcelData();

            //生成excel文件
            write();
        } catch (IOException e) {
            LOGGER.error("export excel file has exception", e);
        }
    }

    abstract void writeExcelData();

    abstract void createExcelTitle();

    protected void createWorkBook() throws IOException {

        if (exportFilePath.endsWith(".xls")) {
            workbook = new HSSFWorkbook();
        } else if (exportFilePath.endsWith(".xlsx")) {
            workbook = new XSSFWorkbook();
        } else {
            throw new IOException("error excel file type");
        }
        sheet = workbook.createSheet();
    }

    protected void write() {
        try {
            File file = new File(exportFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }
            OutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            LOGGER.error("excel file not found in export process : file={}", exportFilePath, e);
        } catch (IOException e) {
            LOGGER.error("export excel file has exception : file={}", exportFilePath, e);
        }
    }

    public String getExportFilePath() {
        return exportFilePath;
    }

    public void setExportFilePath(String exportFilePath) {
        this.exportFilePath = exportFilePath;
    }
}
