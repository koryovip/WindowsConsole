package jp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AAA {

    public static void main(String[] args) throws InterruptedException,
            IOException {
        String ddd = "C:\\Users\\kou\\Desktop\\DCIM\\823WGTMA\\";
        String aaa = "_30.MOV";
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", //
                "C:\\Users\\kou\\Desktop\\ffmpeg\\bin\\ffprobe.exe", //
                ddd + aaa//
        //"C:\\Users\\kou\\Desktop\\【6v电影www.dy131.com】金蝉脱壳HD中字1280高清.rmvb"//
        );

        Process process = pb.start();

        //InputStreamのスレッド開始
        InputStreamThread it = new InputStreamThread(process.getInputStream());
        InputStreamThread et = new InputStreamThread(process.getErrorStream());
        it.start();
        et.start();

        //プロセスの終了待ち
        process.waitFor();

        //InputStreamのスレッド終了待ち
        it.join();
        et.join();

        System.out.println("戻り値：" + process.exitValue());

        //標準出力の内容を出力
        for (String s : it.getStringList()) {
            System.out.println(s);
        }
        //標準エラーの内容を出力
        for (String s : et.getStringList()) {
            if (s.trim().startsWith("date-jpn")) {
                System.out.println(s);
                int i = "date-jpn        : ".length();
                String fileName = s.trim().substring(i, i + 10);
                fileName += "_";
                fileName += s.replaceAll(":", "-").trim().substring(i + 11, i + 19);
                fileName += ".mov";
                File file = new File(ddd, aaa);
                file.renameTo(new File(ddd, fileName));
            }
            //System.err.println(s);
        }
    }
}

/**
 * InputStreamを読み込むスレッド
 */
class InputStreamThread extends Thread {

    private BufferedReader br;

    private List<String> list = new ArrayList<String>();

    /** コンストラクター */
    public InputStreamThread(InputStream is) {
        br = new BufferedReader(new InputStreamReader(is));
    }

    /** コンストラクター */
    public InputStreamThread(InputStream is, String charset) {
        try {
            br = new BufferedReader(new InputStreamReader(is, charset));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            for (;;) {
                String line = br.readLine();
                if (line == null)
                    break;
                list.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
        }
    }

    /** 文字列取得 */
    public List<String> getStringList() {
        return list;
    }
}