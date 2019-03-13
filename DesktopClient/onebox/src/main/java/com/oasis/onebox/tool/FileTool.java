package com.im;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 文件工具类
 *
 * @author
 */
public class FileTool {

    /**
     * 获取文件MD5
     *
     * @param file 文件
     * @return MD5值
     */
    private static String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
    /***
     * compare two file by Md5
     * 【用于传递2个文件，进行文件比对】
     * @param file1
     * @param file2
     * @return true 相同 false 不同
     */
    public static boolean isSameMd5(File file1, File file2) {
        String md5_1 = FileTool.getFileMD5(file1);
        String md5_2 = FileTool.getFileMD5(file2);
        return md5_1.equals(md5_2);
    }
    /***
     * compare two file by Md5
     * 【用于传递2个文件路径，进行文件比对】
     * @param filepath1
     * @param filepath2
     * @return true 相同 false 不同
     */
    public static boolean isSameMd5(String filepath1, String filepath2) {
        File file1 = new File(filepath1);
        File file2 = new File(filepath2);
        return isSameMd5(file1, file2);
    }

    /**
     * 改变二进制
     * @param args
     * @return
     */
    private static String[][] getPX(String args) {
        int[] rgb = new int[3];
        File file = new File(args);
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (Exception e) {
            System.out.println("对不起，您当前操作的文件不存在，无法进行信息比对");
        }
        int width = bi.getWidth();
        int height = bi.getHeight();
        int minx = bi.getMinX();
        int miny = bi.getMinY();
        String[][] list = new String[width][height];
        for (int i = minx; i < width; i++) {
            for (int j = miny; j < height; j++) {
                int pixel = bi.getRGB(i, j);
                rgb[0] = (pixel & 0xff0000) >> 16;
                rgb[1] = (pixel & 0xff00) >> 8;
                rgb[2] = (pixel & 0xff);
                list[i][j] = rgb[0] + "," + rgb[1] + "," + rgb[2];
            }
        }
        return list;
    }

    /**
     * 比较图像相识度
     * @param imgPath1 照片路径A
     * @param imgPath2 照片路径B
     */
    public static String compareImage(String imgPath1, String imgPath2){
        String[] images = {imgPath1, imgPath2};
        if (images.length == 0) {
            System.out.println("文件地址为空，终止信息比对！");
            return null;
        }
        // 分析图片相似度 begin
        String[][] list1 = getPX(images[0]);
        String[][] list2 = getPX(images[1]);
        int xs = 0;
        int bxs = 0;
        int i = 0, j = 0;
        for (String[] strings : list1) {
            if ((i + 1) == list1.length) {
                continue;
            }
            for (int m=0; m<strings.length; m++) {
                try {
                    String[] value1 = list1[i][j].toString().split(",");
                    String[] value2 = list2[i][j].toString().split(",");
                    int k = 0;
                    for (int n=0; n<value2.length; n++) {
                        if (Math.abs(Integer.parseInt(value1[k]) - Integer.parseInt(value2[k])) < 5) {
                            xs++;
                        } else {
                            bxs++;
                        }
                    }
                } catch (RuntimeException e) {
                    continue;
                }
                j++;
            }
            i++;
        }
        list1 = getPX(images[1]);
        list2 = getPX(images[0]);
        i = 0;
        j = 0;
        for (String[] strings : list1) {
            if ((i + 1) == list1.length) {
                continue;
            }
            for (int m=0; m<strings.length; m++) {
                try {
                    String[] value1 = list1[i][j].toString().split(",");
                    String[] value2 = list2[i][j].toString().split(",");
                    int k = 0;
                    for (int n=0; n<value2.length; n++) {
                        if (Math.abs(Integer.parseInt(value1[k]) - Integer.parseInt(value2[k])) < 5) {
                            xs++;
                        } else {
                            bxs++;
                        }
                    }
                } catch (RuntimeException e) {
                    continue;
                }
                j++;
            }
            i++;
        }
        String bfb;
        try {
            bfb = ((Double.parseDouble(xs + "") / Double.parseDouble((bxs + xs) + "")) + "");
            bfb = bfb.substring(bfb.indexOf(".") + 1, bfb.indexOf(".") + 3);
        } catch (Exception e) {
            bfb = "0";
        }
        if (bfb.length() <= 0) {
            bfb = "0";
        }
        if(bxs == 0){
            bfb="100";
        }
        return "相似像素数量：" + xs + " 不相似像素数量：" + bxs + " 相似率：" + Integer.parseInt(bfb) + "%";
    }


    /**
     *
     * @param searchText
     * @param comparedReader
     * @param rbw
     * @throws IOException
     */
    private static void searchAndSignProcess(String searchText, BufferedReader comparedReader, BufferedWriter rbw)
            throws IOException {
        String lineStr = "-\n";
        if (searchText == null) {
            return;
        }
        if ("".equals(searchText)) {
            rbw.write(lineStr);
            return;
        }
        String lineText = null;
        int lineNum = 1;
        while ((lineText = comparedReader.readLine()) != null) {
            String comparedLine = lineText.trim();
            if (searchText.equals(comparedLine)) {
                lineStr = "###=Equal:" + lineNum + "=###\n";
                break;
            }
            lineNum++;
        }
        rbw.write(lineStr);
        comparedReader.reset();
    }


    /**
     * 文本比较方法
     * @param txt1 需要比较的内容
     * @param txt2 被比较的内容
     *
     */
    public void txtCompare(String txt1,String txt2,String outTxt){
        System.out.println("======Start Search!=======");
        long startTime = System.currentTimeMillis();
        // Read first file
        File file = new File(txt1);
        File comparedFile = new File(txt2);
        BufferedReader br = null;
        BufferedReader cbr = null;
        BufferedWriter rbw = null;
        try {
            br = new BufferedReader(new FileReader(file));
            cbr = new BufferedReader(new FileReader(comparedFile));
            cbr.mark(90000000);
            rbw = new BufferedWriter(new FileWriter(outTxt));
            String lineText = null;
            while ((lineText = br.readLine()) != null) {
                String searchText = lineText.trim();
                searchAndSignProcess(searchText, cbr, rbw);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("======Process Over!=======");
            System.out.println("Time Spending:" + ((endTime - startTime) / 1000D) + "s");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (cbr != null && rbw != null) {
                        try {
                            cbr.close();
                            rbw.close();
                        } catch (IOException e) {
                            System.out.println("文本文件比对异常");
                        }
                    }
                }
            }

        }
    }

    /**
     * 获取构造版本号
     * @param v 当前版本号
     * 版本号定义规则：主版本号.次版本号.小版本 例如：1.2.1
     * @return
     */
    public static String getVersion(String v){
        if(null == v){
            return "1.0.0";
        }

        String[] arr = v.split("\\.");
        if(arr.length > 3){
            System.out.println("你当前的版本号非法，请重新导入计算");
            return null;
        }
        if(Integer.valueOf(arr[2]) < 10){
            arr[2] =(Integer.valueOf(arr[2]) + Integer.valueOf(1))+"";
        }else {
            if(Integer.valueOf(arr[1]) < 10){
                arr[1] = (Integer.valueOf(arr[1]) + Integer.valueOf(1))+"";
            }else{
                arr[0] = (Integer.valueOf(arr[0]) + Integer.valueOf(1))+"";
            }
        }
        StringBuffer sb = new StringBuffer();
        for (String a : arr) {
            sb.append(a);
            sb.append(".");
        }

        String temp = sb.toString();
        return temp.substring(0,temp.length()-1);
    }
    public static void main(String[] args) {
        // 测试文件比对
//        String f1 = "/Users/lxt/Desktop/a/1.zip";
//        String f2 = "/Users/lxt/Desktop/b/1.zip";
//        boolean compare = isSameMd5(f1,f2);
//        System.out.println("文件比对结果："+compare);
//        System.out.println("============================");

        //图片相识度对比
//       String str = FileTool.compareImage("/Users/lxt/Desktop/a/a.jpg", "/Users/lxt/Desktop/b/a.png");
//       System.out.println(str);
//       System.out.println("============================");

        //文本内容对比

        //版本号对比
//        System.out.println(getVersion("1.2.10.1"));
    }
}
