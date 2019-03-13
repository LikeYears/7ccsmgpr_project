package com.oasis.onebox.dao;

import java.math.BigDecimal;
import java.util.Map;

public interface DownloadTaskDao {
    void delete();

    void stop();

    Map<String, String> getStatus();

    default String calculateDescSize(long filesize) {
        BigDecimal big1 = new BigDecimal(filesize);
        if (filesize > 1073741824) // 1GB
        {
            return big1.divide(new BigDecimal(1073741824), 2, BigDecimal.ROUND_HALF_EVEN).toString() + "GB";
        } else if (filesize > 1024 * 1024) {
            return big1.divide(new BigDecimal(1024 * 1024), 2, BigDecimal.ROUND_HALF_EVEN).toString() + "MB";
        } else {
            return big1.divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_EVEN).toString() + "KB";
        }
    }
}
