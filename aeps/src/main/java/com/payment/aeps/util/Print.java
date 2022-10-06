package com.payment.aeps.util;

public class Print {

    static int count = 0;

    Print() { }

    public static void P(String sb) {
        if (sb == null || sb.length() == 0) {
            return;
        }
        String TAG = "LOG";
        if (sb.length() > 4000) {
            int chunkCount = sb.length() / 4000;     // integer division
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= sb.length()) {
                   //System.out.println("" + sb.substring(4000 * i));
                } else {
                  // System.out.println("" + sb.substring(4000 * i, max));
                }
            }
        } else {
          //  System.out.println("" + sb);
        }
    }

    public static void printFormUploadParameter(String key, String value) {
        //  System.out.println(key+" = "+value+" "+count++);
    }

    public static void appendLog(String text) {
        // Create an log file name with date
//        String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
//        String FileName = "AppLog_" + timeStamp + ".txt";
//        File myDirectory = new File(Environment.getExternalStorageDirectory(), "/SEC2PAY/LOG/");
//
//        if (!myDirectory.exists()) {
//            myDirectory.mkdirs();
//        }
//        File logFile = new File(Environment.getExternalStorageDirectory(), "/SEC2PAY/LOG/" + FileName);
//        if (!logFile.exists()) {
//            try {
//                logFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            //BufferedWriter for performance, true to set append to file flag
//            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
//            buf.append(text);
//            buf.newLine();
//            buf.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
