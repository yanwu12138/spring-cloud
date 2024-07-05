package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.pojo.MmMibBO;
import lombok.extern.slf4j.Slf4j;
import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author XuBaofeng.
 * @date 2023/10/7 10:46.
 * <p>
 * description: MIB文件解析
 */
@Slf4j
@SuppressWarnings("unused")
public class MIBUtil {

    private MIBUtil() {
        throw new UnsupportedOperationException("MIBUtil should never be instantiated");
    }

    public static List<MmMibBO> readMibFile(String filepath) {
        if (!FileUtil.fileExists(filepath)) {
            return Collections.emptyList();
        }
        return readMibFile(new File(filepath));
    }

    public static List<MmMibBO> readMibFile(File file) {
        if (!FileUtil.fileExists(file)) {
            return Collections.emptyList();
        }
        try {
            Mib mib = new MibLoader().load(file);
            if (mib == null) {
                return Collections.emptyList();
            }
            List<MmMibBO> result = new ArrayList<>();
            String parent = null, syntax = null, access = null, status = null;
            for (Object item : mib.getAllSymbols()) {
                if (!(item instanceof MibValueSymbol)) {
                    continue;
                }
                MibValueSymbol mibValue = (MibValueSymbol) item;
                SnmpObjectType sot = null;
                if (mibValue.getType() instanceof SnmpObjectType) {
                    sot = (SnmpObjectType) mibValue.getType();
                }
                if (sot != null) {
                    syntax = sot.getSyntax().getName();
                    access = sot.getAccess().toString();
                    status = sot.getStatus().toString();
                }
                if (mibValue.getParent() != null) {
                    parent = mibValue.getParent().getValue().toString();
                }
                result.add(MmMibBO.newInstance(mibValue, parent, syntax, access, status));
            }
            log.info("read mib file success. filepath: {}, result: {}", file.getPath(), result);
            return result;
        } catch (Exception e) {
            log.error("read mib file failed. filepath: {}", file.getPath(), e);
            return Collections.emptyList();
        }
    }

    public static void main(String[] args) throws Exception {
        String filePath = "/Users/xubaofeng/Downloads/mibs/NEWTEC-MAIN-MIB.mib";
        List<MmMibBO> mib = readMibFile(filePath);
        System.out.println("--------------------------------");
        File file = new File("/Users/xubaofeng/Downloads/mibs/");
        for (File item : file.listFiles()) {
            readMibFile(item);
            System.out.println("--------------------------------");
        }
    }
}
