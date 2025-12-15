package com.bixuebihui.util.html.publish;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Url2html {

    private List stringFilter = null;

    public void addFilter(IStringFilter filter) {
        if (filter != null) {
            if (stringFilter == null) {
                stringFilter = new ArrayList();
            }
            stringFilter.add(filter);
        }
    }

    public boolean writeFile(String src, String dest) throws IOException {
        return writeFile(src, dest, null);
    }

    public boolean writeFile(String src, String dest, String waterMark) throws IOException {
        URL url = new URL(src);
        java.io.InputStream inputstream = url.openStream();

        FileOutputStream fileoutputstream = new FileOutputStream(dest);

        boolean res = trans(inputstream, fileoutputstream, waterMark);

        fileoutputstream.close();
        inputstream.close();

        return res;
    }


    private boolean trans(InputStream in, OutputStream out, String waterMark) throws IOException {
        try {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String s;
            StringBuffer s2 = new StringBuffer("");
            String ln = System.getProperty("line.separator");
            for (; (s = bufferedreader.readLine()) != null; s2.append(s).append(ln)) {
            }
            bufferedreader.close();
            s = s2.toString();

            if (stringFilter != null) {
                for (int i = 0; i < stringFilter.size(); i++) {
                    IStringFilter filter = (IStringFilter) stringFilter.get(i);
                    s = filter.filter(s);
                }
            }

            if (waterMark == null || (s.contains(waterMark))) {
                PrintStream printstream = new PrintStream(out, false, "UTF-8");
                printstream.println(s);
                printstream.close();
                return true;
            }
            return false;
        } catch (IOException ioexception) {
            System.out.println("Error in I/O:" + ioexception.getMessage());
            throw ioexception;
        }

    }

}
