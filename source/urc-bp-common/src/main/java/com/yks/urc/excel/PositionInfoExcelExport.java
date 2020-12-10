package com.yks.urc.excel;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.vo.UserByPosition;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component
public class PositionInfoExcelExport extends AbstractExcelExport {
	private Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	List<UserByPosition> list;
	//是个有用户名
	boolean flag = true;
	@Override
	void writeExcelData() {
		if (CollectionUtils.isEmpty(list)) {
            LOGGER.info("{} no data to write excel file", this.getClass().getSimpleName());
            return;
        }

        int dataLength = list.size();
        int startRowNum = TITLE_ROW_NUM + 1;
        for (int i = 0; i < dataLength; i++) {
            writeRowData(startRowNum + i, list.get(i));
        }
		
	}

	private void writeRowData(int rowNum, UserByPosition userByPosition) {
		LOGGER.info("write {} row data : resp_getUserListByPermitKey=", rowNum, StringUtility.toJSONString(userByPosition));
        Row row = sheet.createRow(rowNum);

        row.createCell(0).setCellValue(StringUtility.convertToString(userByPosition.getPermitName()));
        row.createCell(1).setCellValue(StringUtility.convertToString(userByPosition.getPositionName()));
        if(flag) {
            row.createCell(2).setCellValue(StringUtility.convertToString(userByPosition.getUserName()));
        }
		
	}

	@Override
	void createExcelTitle() {
		LOGGER.info("{} start to create excel title", this.getClass().getSimpleName());
        excelTitle = new ArrayList<String>();
        excelTitle.add("权限名称");
        excelTitle.add("岗位名称");
        if(flag) {
            excelTitle.add("用户名");
        }
        Row titleRow = sheet.createRow(TITLE_ROW_NUM);

        int titleLength = excelTitle.size();
        for (int i = 0; i < titleLength; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(excelTitle.get(i));
        }
    }

    public List<UserByPosition> getList() {
        return list;
    }

    public void setList(List<UserByPosition> list) {
        this.list = list;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
