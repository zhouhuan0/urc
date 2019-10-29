package com.yks.urc.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.yks.common.util.StringCommonUtils;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.vo.Resp_getUserListByPermitKey;

@Scope("prototype")
@Component
public class PermitInfoExcelExport extends AbstractExcelExport {
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	List<Resp_getUserListByPermitKey> lstRslt;
	@Override
	void writeExcelData() {
		if (CollectionUtils.isEmpty(lstRslt)) {
            LOGGER.info("{} no data to write excel file", this.getClass().getSimpleName());
            return;
        }

        int dataLength = lstRslt.size();
        int startRowNum = TITLE_ROW_NUM + 1;
        for (int i = 0; i < dataLength; i++) {
            writeRowData(startRowNum + i, lstRslt.get(i));
        }
		
	}

	private void writeRowData(int rowNum, Resp_getUserListByPermitKey resp_getUserListByPermitKey) {
		LOGGER.info("write {} row data : resp_getUserListByPermitKey=", rowNum, StringCommonUtils.toJSONString(resp_getUserListByPermitKey));
        Row row = sheet.createRow(rowNum);

        row.createCell(0).setCellValue(StringUtility.convertToString(resp_getUserListByPermitKey.permitName));
        row.createCell(1).setCellValue(StringUtility.convertToString(resp_getUserListByPermitKey.userName));
		
	}

	@Override
	void createExcelTitle() {
		LOGGER.info("{} start to create excel title", this.getClass().getSimpleName());
        excelTitle = new ArrayList<String>();
        excelTitle.add("权限名称");
        excelTitle.add("用户名");
        Row titleRow = sheet.createRow(TITLE_ROW_NUM);

        int titleLength = excelTitle.size();
        for (int i = 0; i < titleLength; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(excelTitle.get(i));
        }
    }

	public List<Resp_getUserListByPermitKey> getLstRslt() {
		return lstRslt;
	}

	public void setLstRslt(List<Resp_getUserListByPermitKey> lstRslt) {
		this.lstRslt = lstRslt;
	}

    

}
