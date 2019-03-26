package com.oasis.onebox.tool;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


public class copy {
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();

            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
    }

    public static void main(String[] args) {
        String newPath = "D:\\zc\\defaultroot\\sbbb\\temp\\SGHT\\201411030005064964\\1.docx";
        String oldPath = "D:\\zc\\defaultroot\\sbbb\\annex\\ZC_SGHT\\201411030005064964\\1.docx";
        copy.copyFile(oldPath, newPath);

    }
}
//