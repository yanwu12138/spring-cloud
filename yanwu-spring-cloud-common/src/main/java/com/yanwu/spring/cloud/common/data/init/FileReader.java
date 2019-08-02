package com.yanwu.spring.cloud.common.data.init;

import com.google.common.base.Stopwatch;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.Resources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileReader {

    private static final Charset Default_Charset = Charset.forName("utf-8");

    private static final String DELIMITER_LINE = "\n";
    private static final String DATAFILE_ENCODING = "utf-8";

    @SuppressWarnings("unchecked")
    public List<Object> readFromFile(String fileName, Class objClass) {
        return readFromFile(fileName, objClass, Default_Charset);
    }

    @SuppressWarnings("unchecked")
    public List<Object> readFromFile(String fileName, Class objClass, Charset charset) {

        List<Object> ret = null;

        Stopwatch sw = Stopwatch.createStarted();
        try (InputStream is = Resources.getInputStream(fileName)) {

            String json = null;
            if (charset == null) {
                json = IOUtils.toString(is);
            } else {
                json = IOUtils.toString(is, charset);
            }
            ret = JsonUtil.toObjectList(json, objClass);

            log.info("load from {}  , rec= {} , timecost={}", fileName, ret, sw);

            if (ret != null) {
                for (Object record : ret) {

                    log.info("record {}", record);

                }
            }

        } catch (IllegalArgumentException iae) {
            log.info("no file ({}) found : {}", fileName, iae);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;

    }

    public List<String> readSQLFromFile(String fileName) {

        log.info("readSQLFromFile from {} ", fileName);

        List<String> ret = null;

        try (InputStream is = Resources.getInputStream(fileName)) {

            String allSqlStr = IOUtils.toString(is, Charset.forName(DATAFILE_ENCODING));

            if (allSqlStr != null) {
                String[] sqlArray = allSqlStr.split(DELIMITER_LINE);

                if (sqlArray != null) {
                    ret = new ArrayList<String>();
                    for (String sql : sqlArray) {
                        ret.add(sql);
                    }
                }
            }

            log.info("load sql from {}  , rec= {} ", fileName, ret);

        } catch (IllegalArgumentException iae) {
            log.info("no sql file ({}) found : {}", fileName, iae);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public String readAllFromFile(String fileName) {

        log.info("readAllFromFile from {} ", fileName);

        try (InputStream is = Resources.getInputStream(fileName)) {

            String allSqlStr = IOUtils.toString(is, Charset.forName(DATAFILE_ENCODING));

            log.info("load file from {}  ", fileName);

            return allSqlStr;

        } catch (IllegalArgumentException iae) {
            log.error("no file ({}) found : {}", fileName, iae);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //to do close?
        }

        return null;
    }

}
