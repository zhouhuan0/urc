package com.yks.urc.excel;

import com.yks.urc.fw.StringUtility;
import com.yks.urc.vo.PositionPower;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Scope("prototype")
@Component
public class PositionPowerExcelExport extends AbstractExcelExport {
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    List<PositionPower> list;

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

    private void writeRowData(int rowNum, PositionPower positionPower) {
        LOGGER.info("write {} row data : resp_PositionPowerExcelExport=", rowNum, StringUtility.toJSONString(positionPower));
        Map<String,String> map = new HashMap<String,String>();
        map.put("0","ERP系统");
        map.put("1","FBA系统");
        map.put("2","账单系统");
        map.put("3","海外物流优选");
        map.put("4","PBS系统");
        //excle设值
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(StringUtility.convertToString(positionPower.getPositionName()));
        row.createCell(1).setCellValue(map.get(StringUtility.convertToString(positionPower.getPlatformType())));
        row.createCell(2).setCellValue(StringUtility.convertToString(positionPower.getSystemName()));
        row.createCell(3).setCellValue(StringUtility.convertToString(positionPower.getPermitName()));
        row.createCell(4).setCellValue(StringUtility.convertToString(positionPower.getDistributionMan()));
        row.createCell(5).setCellValue(StringUtility.convertToString(positionPower.getLastDate()));

    }

    @Override
    void createExcelTitle() {
        LOGGER.info("{} start to create excel title", this.getClass().getSimpleName());
        //岗位、平台类型、系统名称、权限名称、分配人、最新分配时间
        excelTitle = new ArrayList<String>();
        excelTitle.add("岗位");
        excelTitle.add("平台类型");
        excelTitle.add("系统名称");
        excelTitle.add("权限名称");
        excelTitle.add("分配人");
        excelTitle.add("最新分配时间");
        Row titleRow = sheet.createRow(TITLE_ROW_NUM);

        int titleLength = excelTitle.size();
        for (int i = 0; i < titleLength; i++) {
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(excelTitle.get(i));
        }
    }

    public List<PositionPower> getList() {
        return list;
    }

    public void setList(List<PositionPower> list) {
        this.list = list;
    }

}
