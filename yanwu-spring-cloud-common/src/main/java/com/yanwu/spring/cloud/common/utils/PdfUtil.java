package com.yanwu.spring.cloud.common.utils;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2024/9/19 09:59.
 * <p>
 * description:
 */
@Slf4j
public class PdfUtil {

    private PdfUtil() {
        throw new UnsupportedOperationException("PdfUtil should never be instantiated");
    }

    public static void main(String[] args) {
        String source = "/Users/xubaofeng/yanwu/tools/波星通卫星通信终端安装调试确认单V3.0.pdf";
        editPdf(source, EditPdfBO.newInstance(25, 50, "测试哦"), "/Users/xubaofeng/yanwu/tools/安装调试确认单/测试哦V3.0.pdf");
    }

    public static void editPdf(String sourceFile, EditPdfBO content, String targetFile) {
        editPdf(new File(sourceFile), Collections.singletonList(content), targetFile);
    }

    public static void editPdf(File sourceFile, EditPdfBO content, String targetFile) {
        editPdf(sourceFile, Collections.singletonList(content), targetFile);
    }

    public static void editPdf(File sourceFile, List<EditPdfBO> contents, String targetFile) {
        try (PDDocument document = PDDocument.load(sourceFile)) {
            PDPage page = document.getPage(0);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true, true)) {
                contentStream.beginText();
                for (EditPdfBO content : contents) {
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    // ----- x,y坐标
                    contentStream.newLineAtOffset(content.getTx(), content.getTy());
                    contentStream.showText(content.getContent());
                }
                contentStream.endText();
                contentStream.close();
                // ----- 保存修改后的文档
                document.save(targetFile);
            }
        } catch (IOException e) {
            log.error("edit pdf failed. source: {}, content: {}", sourceFile.getAbsolutePath(), contents, e);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class EditPdfBO implements Serializable {
        private static final long serialVersionUID = -1651087020656883284L;
        private float tx;
        private float ty;
        private String content;

        public static EditPdfBO newInstance(float tx, float ty, String content) {
            EditPdfBO instance = new EditPdfBO();
            instance.setTx(tx);
            instance.setTy(ty);
            instance.setContent(content);
            return instance;
        }
    }

}
