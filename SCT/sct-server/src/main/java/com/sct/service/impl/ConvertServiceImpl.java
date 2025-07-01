package com.sct.service.impl;

import com.sct.controller.user.convert.ConvertEntity;
import com.sct.service.ConvertService;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Title: ConvertServiceImpl
 * @Author Xc_Star
 * @Package com.sct.service.impl
 * @Date 2025/6/26 23:38
 */

@Service
public class ConvertServiceImpl implements ConvertService {

    @Value("classpath:template/template.xlsx")
    private Resource templateResource;

    private static final int CARTON_TO_BOX = 54;
    private static final int BOX_TO_PIECES = 1728;
    private static final int HALF_BOX = 864;
    private static final int GROUP_TO_PIECES = 64;
    private static final int HALF_GROUP = 32;

    @Override
    public List<ConvertEntity> parseConvertList(MultipartFile file) throws IOException {

        List<ConvertEntity> converts = new ArrayList<>();
        Pattern pattern = Pattern.compile("^\\|.*\\|.*\\|.*\\|.*\\|$");

        try (InputStream inputStream = file.getInputStream()) {
            String content = new String(inputStream.readAllBytes());
            String[] lines = content.split("\\r?\\n");

            for (String line : lines) {
                line = line.trim();
                if (pattern.matcher(line).matches() && !line.startsWith("| Item")) {
                    String[] parts = line.substring(1, line.length() - 1).split("\\|");
                    if (parts.length == 4) {
                        ConvertEntity convert = new ConvertEntity();
                        convert.setName(parts[0].trim());
                        convert.setTotalRequired(Integer.parseInt(parts[1].trim()));
                        convert.setMissingQuantity(Integer.parseInt(parts[2].trim()));
                        convert.setAvailableQuantity(Integer.parseInt(parts[3].trim()));
                        converts.add(convert);
                    }
                }
            }
        }

        return converts;
    }

    @Override
    public byte[] generateExcelFromTemplate(List<ConvertEntity> converts, HttpServletResponse response) throws IOException {
        try (InputStream templateStream = templateResource.getInputStream();
             XSSFWorkbook workbook = new XSSFWorkbook(templateStream)) {

            XSSFSheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                XSSFRow row = sheet.getRow(i);
                if (row != null) {
                    sheet.removeRow(row);
                }
            }

            for (int i = 0; i < converts.size(); i++) {
                ConvertEntity convert = converts.get(i);
                int total = convert.getTotalRequired();
                int[] units = calculateUnits(total);

                XSSFRow row = sheet.createRow(i + 1);

                row.createCell(0).setCellValue(convert.getName());  // 材料名称
                row.createCell(1).setCellValue("");                  // 是否完成
                row.createCell(2).setCellValue(total);               // 总量/个
                row.createCell(3).setCellValue(units[0]);           // 所需箱
                row.createCell(4).setCellValue(units[1]);           // 所需盒
                row.createCell(5).setCellValue(units[2]);           // 所需组
                row.createCell(6).setCellValue(units[3]);           // 所需个
                row.createCell(7).setCellValue("");                  // 负责人
                row.createCell(8).setCellValue("");                  // 完成进度
                row.createCell(9).setCellValue("");                  // 物品所在位置
            }

            // 在最后添加一行"生成工具由Xc_Star编写"
            int lastRowNum = sheet.getLastRowNum();
            XSSFRow footerRow = sheet.createRow(lastRowNum + 2); // +2是为了空一行
            CellRangeAddress mergedRegion = new CellRangeAddress(lastRowNum + 2, lastRowNum + 2, 0, 9);
            sheet.addMergedRegion(mergedRegion);
            footerRow.createCell(0).setCellValue("生成工具由Xc_Star编写");

            // 设置合并单元格的样式（可选）
            CellStyle footerStyle = workbook.createCellStyle();
            footerStyle.setAlignment(HorizontalAlignment.CENTER);
            footerRow.getCell(0).setCellStyle(footerStyle);

            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private int[] calculateUnits(int total) {
        if (total <= 10) {
            return new int[]{0, 0, 0, 10};
        }

        int totalBoxesNeeded = (int) Math.ceil((double)total / BOX_TO_PIECES);

        int cartons = totalBoxesNeeded / CARTON_TO_BOX;
        int remainingBoxes = totalBoxesNeeded % CARTON_TO_BOX;

        int remainingPieces = total % BOX_TO_PIECES;
        int groups = 0;
        int pieces = 0;

        if (remainingPieces > 0) {
            groups = remainingPieces / GROUP_TO_PIECES;
            pieces = remainingPieces % GROUP_TO_PIECES;

            if (pieces >= HALF_GROUP) {
                groups++;
                pieces = 0;
            }

            if (groups * GROUP_TO_PIECES >= HALF_BOX) {
                remainingBoxes++;
                groups = 0;
                pieces = 0;
            }
        }

        return new int[]{cartons, remainingBoxes, groups, pieces};
    }
}
