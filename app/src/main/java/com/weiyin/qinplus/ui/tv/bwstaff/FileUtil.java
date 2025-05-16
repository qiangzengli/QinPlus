package com.weiyin.qinplus.ui.tv.bwstaff;

import android.content.Context;
import android.util.Log;

import com.weiyin.qinplus.bluetooth.BlueToothControl;
import com.weiyin.qinplus.ui.tv.bwstaff.DB.TagWaterFall;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   : app 修改midi文件
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */

public class FileUtil {
    public String WriteFile(String fileName, byte[] b) {
        String filePath = "";
        try {
            File file = new File(fileName);
            Log.i("Fileutil", "filename=" + fileName + " b.length=" + b.length);
            file.createNewFile();
            FileOutputStream flout = new FileOutputStream(file.getPath());
            flout.write(b);
            flout.close();
            filePath = fileName;
        } catch (IOException i) {
            i.printStackTrace();
        }
        return filePath;
    }

    public static void writeFile(String oldPath, String path, double ratio) {
        File file = new File(oldPath);
        File file1 = new File(path);
        if (!file1.exists()) {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                inputStream = new FileInputStream(file);
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                String str = new String(bytes);
                for (int i = 0; i < bytes.length; i++) {
                    byte b = bytes[i];
                    if (b == -1) {
                        if (bytes[i + 1] == 81) {
                            if (bytes[i + 2] == 3) {
                                Log.i("OpernActivity", "i=" + i);
                                /* 10 */
                                int b1 = bytes[i + 3];
                                /* 103 */
                                int b2 = bytes[i + 4];
                                /* 90 */
                                int b3 = bytes[i + 5];

                                if (b1 < 0) {
                                    b1 = 256 + b1;
                                }

                                if (b2 < 0) {
                                    b2 = 256 + b2;
                                }
                                if (b3 < 0) {
                                    b3 = 256 + b3;
                                }
                                byte b4 = (byte) 225;
                                String string = BlueToothControl.algorithmToHEXString(b1) + BlueToothControl.algorithmToHEXString(b2) + BlueToothControl.algorithmToHEXString(b3);
                                /* 681818  852272 */
                                int nu = BlueToothControl.hexStringToAlgorithm(string);
                                /* int number=Integer.valueOf(string);//0a6757 */
                                double number = 0;
                                if (ratio == 0.8) {
                                    number = nu * 10 / 8;
                                } else if (ratio == 0.6) {
                                    number = nu * 10 / 6;
                                }

                                String s = BlueToothControl.algorithmToHEXString((int) number);
                                Log.i("OpernActivity", s);
                                byte[] bytes1 = BlueToothControl.hexStringToByte(s);
                                for (int j = 0; j < bytes1.length; j++) {
                                    bytes[i + 3 + j] = bytes1[j];
                                }
                            }
                        }
                    }
                }

                outputStream = new FileOutputStream(path);
                outputStream.write(bytes);

            } catch (IOException io) {
                io.printStackTrace();
            } finally {
                try {
                    assert inputStream != null;
                    inputStream.close();
                    assert outputStream != null;
                    outputStream.close();
                } catch (IOException io) {
                    io.printStackTrace();
                }
            }
        }
    }

    public ArrayList<TagWaterFall> updateSpeed(ArrayList<TagWaterFall> tagWaterFalls, double radio) {
        ArrayList<TagWaterFall> tagWaterFallArrayList = new ArrayList<>();
        if (tagWaterFalls != null) {
            if (tagWaterFalls.size() > 0) {
                for (int i = 0; i < tagWaterFalls.size(); i++) {
                    TagWaterFall tagWaterFall = new TagWaterFall();
                    tagWaterFall.startTime = (int) (tagWaterFalls.get(i).startTime / radio);
                    tagWaterFall.duration = (int) (tagWaterFalls.get(i).duration / radio);
                    tagWaterFall.leftRight = tagWaterFalls.get(i).leftRight;
                    tagWaterFall.note = tagWaterFalls.get(i).note;
                    tagWaterFallArrayList.add(tagWaterFall);
                }
            }
        }
        return tagWaterFallArrayList;
    }

    public ArrayList<TagWaterFall> readFileByLinesAudio(String fileName) {
        File file = new File(fileName);
        ArrayList<TagWaterFall> tagWaterFalls = new ArrayList<>();
        BufferedReader reader = null;
        try {
            /* System.out.println("以行为单位读取文件内容，一次读一整行："); */
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            /* 一次读入一行，直到读入null为文件结束 */
            while ((tempString = reader.readLine()) != null) {
                if (line != 1) {
                    int x = tempString.indexOf(":");
                    int y = tempString.indexOf(":", x + 1);
                    int z = tempString.indexOf(":", y + 1);
                    String strNote = tempString.substring(0, x);
                    String strTime = tempString.substring(x + 1, y);
                    String strDuration = tempString.substring(y + 1, z);
                    String strMM = tempString.substring(z + 1);

                    TagWaterFall tagWaterFall = new TagWaterFall();
                    tagWaterFall.note = Integer.parseInt(strNote);
                    tagWaterFall.duration = Integer.parseInt(strDuration);
                    tagWaterFall.startTime = Integer.parseInt(strTime);
                    tagWaterFall.leftRight = Integer.parseInt(strMM);

                    tagWaterFalls.add(tagWaterFall);
                }
                line++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return tagWaterFalls;
    }

    public static boolean deleteFile(String filePath) {
        boolean result = false;
        if (filePath != null) {
            File file = new File(filePath);
            result = file.exists() && file.delete();
        }
        return result;
    }

    public static File createFile(Context context, int index, String assetsName, int rawId, String fileName) {
        File file = null;
        InputStream inputStream = null;
        try {
            if (index == 0) {
                inputStream = context.getAssets().open(assetsName);
            } else if (index == 1) {
                inputStream = context.getResources().openRawResource(rawId);
            }
            if (inputStream != null) {
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
/* "/data/user/0/com.weiyin.qinplus/files/piano/classic/initiation/pdf" */
                OutputStream outputStream = new FileOutputStream(new File(context.getFilesDir().getPath() + "/" + fileName));
                outputStream.write(bytes);
                file = new File(context.getFilesDir().getPath() + "/" + fileName);
            }


        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException io) {
                io.printStackTrace();
            }
        }
        return file;
    }
}
