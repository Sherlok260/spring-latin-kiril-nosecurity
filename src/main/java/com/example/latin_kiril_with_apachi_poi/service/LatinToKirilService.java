package com.example.latin_kiril_with_apachi_poi.service;

import com.example.latin_kiril_with_apachi_poi.payload.ApiResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.*;
import org.python.core.PyObject;
import org.python.google.common.io.Resources;
import org.python.util.PythonInterpreter;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Service
public class LatinToKirilService {

    public XWPFRun translate(PythonInterpreter pythonInterpreter, XWPFRun run, String r1) {
        try {
            PyObject latin_to_kril = pythonInterpreter.eval("to_cyrillic('"+r1+"')");

            String response_latin_to_kril = latin_to_kril.toString();
            byte bytess[] = response_latin_to_kril.getBytes("ISO-8859-1");
            String result_latin_to_kril = new String(bytess, "UTF-8");

            run.setText(result_latin_to_kril, 0);
            System.out.println(run);
        } catch (Exception e) {
            System.out.println(r1);
        }
        return run;
    }

    public ApiResponse translate_and_save(MultipartFile multipartFile) throws InvalidFormatException, IOException {

        OutputStream os = new FileOutputStream(multipartFile.getOriginalFilename());
        os.write(multipartFile.getBytes());
        os.close();

        PythonInterpreter pythonInterpreter = new PythonInterpreter();
//        String pathh = "/file:/latin-kiril-apachi-poi.jar/BOOT-INF/classes/scripts/translate.py";
//        String pathh = ResourceLoader.class.getClassLoader().getResource("scripts/translate.py").getPath();
//        String pathh = ResourceUtils.FILE_URL_PREFIX + "scripts/translate.py";
//        String pathh = getClass().getClassLoader().getResource("scripts/translate.py").getPath();
//        String pathh = "latin-kiril-apachi-poi.jar/BOOT-INF/classes/scripts/translate.py";
//        String pathh = ResourceLoader.CLASSPATH_URL_PREFIX + File.separator + "scripts"+ File.separator +"translate.py";
//        String pathh = LatinToKirilService.class.getClassLoader().getResource("scripts/translate.py").getPath();
        String pathh = Resources.getResource("scripts/translate.py").getPath();

        System.out.println(pathh);
        pythonInterpreter.execfile(pathh);

        XWPFDocument docx = new XWPFDocument(OPCPackage.open(multipartFile.getOriginalFilename()));

        //pages
        List<XWPFParagraph> paragraphs = docx.getParagraphs();
        for(XWPFParagraph xwpfParagraph: paragraphs) {
            List<XWPFRun> runs = xwpfParagraph.getRuns();
            for(XWPFRun run: runs) {
                String r1 = String.valueOf(run);
                if(r1.equals(" ") || r1.equals("\n")) {
                    continue;
                }
                translate(pythonInterpreter, run, r1);
            }
        }

        //tables
        for (XWPFTable tbl : docx.getTables()) {
            for (XWPFTableRow row : tbl.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph p : cell.getParagraphs()) {
                        for (XWPFRun run : p.getRuns()) {
                            String r1 = String.valueOf(run);
                            if(r1.equals(" ") || r1.equals("\n")) {
                                continue;
                            }
                            translate(pythonInterpreter, run, r1);
                        }
                    }
                }
            }
        }

        //save
        try {
            String path = ResourceLoader.class.getClassLoader().getResource("uploads/Result.docx").getPath();
            docx.write(new FileOutputStream(path));
            docx.close();
            new File(multipartFile.getOriginalFilename()).delete();
            return new ApiResponse("success", true);
        } catch (Exception e) {
            return new ApiResponse(e.getMessage(), false);
        }
    }
}