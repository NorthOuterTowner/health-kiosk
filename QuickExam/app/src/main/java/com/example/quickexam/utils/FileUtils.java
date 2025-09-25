package com.example.quickexam.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileUtils {

    public static String pathHead = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PEM/";
    public static String settingFileName = "Config/setting.txt";

    /**
     * 读系统文件
     *
     * @param fileName
     * @return
     */
    public static String readString(String pathHead, String fileName) {
        try {
            String str = "";
            String mimeTypeLine = "";
            File urlFile = new File(pathHead);
            if (!urlFile.exists()) {
                urlFile.mkdirs();
            }
            File file = new File(pathHead + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream stream = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(stream, "UTF-8");
//            InputStreamReader isr = new InputStreamReader(stream, "GB2312");
            BufferedReader br = new BufferedReader(isr);
            while ((mimeTypeLine = br.readLine()) != null) {
                str += mimeTypeLine;
            }
            return str;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写外部存储
     *
     * @param copyContent 内容
     * @param fileName    文件名
     */
    public static void write(String copyContent, String fileName) {
        try {
            String filePath = pathHead + fileName;
            File file = new File(filePath);
            File fileParent = file.getParentFile();
            if (!fileParent.exists())
                fileParent.mkdirs();
            if (!file.exists())
                file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(copyContent.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件
     */
    public static void writeFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists())
                file.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeMethod(String fileName, String conent) {
        BufferedWriter out = null;
        try {
            String filePath = pathHead + fileName;
            File file = new File(filePath);
            File fileParent = file.getParentFile();
            if (!fileParent.exists())
                fileParent.mkdirs();
            if (!file.exists())
                file.createNewFile();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取file列表
     */
    public static ArrayList<String> getFileLength(String filePath) {
        try {
            ArrayList<String> list = new ArrayList<>();
            File file = new File(filePath);
            for (File fileChild : file.listFiles()) {
                list.add(fileChild.getName());
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断文件是否存在
     */
    public static boolean isExists(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断文件是否存在、文件大小是否相同
     */
    public static boolean isExistence(String filePath, int filesize) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.length() == filesize)
                    return true;
                else {
//                    Log.d("sss filesize no",filePath + " |" + file.length() + "--"  +filesize);
//                    file.delete();
                    return false;
                }
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除文件
     *
     * @param filepath
     * @return
     */
    public static void clearFile(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();//如果为文件，直接删除
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File filePath : files) {
                    filePath.delete();//如果为文件夹，递归调用
                }
            }
            file.delete();
        }
    }

    public static void clearFiles(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File filePath : files) {
                    filePath.delete();//如果为文件夹，递归调用
                }
            }
        }
    }

    /**
     * 截取url后缀名称
     *
     * @param string
     * @return
     */
    public static String substringName(String string) {
        return string.substring(string.lastIndexOf("/") + 1, string.length());
    }

    /**
     * 读系统文件
     *
     * @param fileName
     * @return
     */
    public static String[] read(String pathHead, String fileName) {
        try {
            String str = "";
            String mimeTypeLine = "";
            File urlFile = new File(pathHead + fileName);
            if (!urlFile.exists())
                urlFile.createNewFile();
            InputStreamReader isr = new InputStreamReader(new FileInputStream(urlFile), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            while ((mimeTypeLine = br.readLine()) != null) {
                str += mimeTypeLine + "\n";
            }
            String[] arr = str.split("\n");
            return arr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取本地文件
     *
     * @param pathHead
     * @param configFileName
     * @return
     */
    public static Map<String, String> getConfig(String pathHead, String configFileName) {
        String[] read = read(pathHead, configFileName);
        Map<String, String> map = new HashMap<>();
        if (read != null) {
            for (String str : read) {
                if (str.contains("=")) {
                    if (str.contains("=")) {
                        String key = str.substring(0, str.indexOf("="));
                        String value = str.substring(str.indexOf("=") + 1, str.length());
                        if (value.isEmpty())
                            map.put(key, "");
                        else
                            map.put(key, value);
                    }
                }
            }
        }
        return map;
    }

    /**
     * 判断是否为空
     *
     * @param s
     * @return
     */
    public static String isEmtpy(String s) {
        if (s.equals("") || s.equals(null) || s == null) {
            return null;
        }
        return s;
    }

    public static String analysisMap(Map<String, String> map) {
        String data = "";
        //使用迭代器，获取key
        Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            data += key + "=" + value + "\n";
        }
        return data;
    }
}
