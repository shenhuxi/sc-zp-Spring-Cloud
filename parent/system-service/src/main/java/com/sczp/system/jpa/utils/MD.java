package com.sczp.system.jpa.utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD {


    private static final char[] bcdLookup = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * Transform the specified byte into a Hex String form.
     */
    public static final String bytesToHexStr(byte[] bcd) {
        StringBuffer s = new StringBuffer(bcd.length * 2);
        for (int i = 0; i < bcd.length; i++) {
            s.append(bcdLookup[(bcd[i] >>> 4) & 0x0f]);
            s.append(bcdLookup[bcd[i] & 0x0f]);
        }
        return s.toString();
    }

    /**
     * Transform the specified Hex String into a byte array.
     */
    public static final byte[] hexStrToBytes(String s) {
        byte[] bytes;
        bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bytes;
    }

    private MessageDigest md = null;

    public MD() {

        String alg = "MD5";

        try {
            md = MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String getMd() {
        return bytesToHexStr(md.digest());
    }

    public void update(byte[] buffers, int length) {
        md.update(buffers, 0, length);
    }


    public static String md5(String str) {
        byte[] bytes = str.getBytes();
        return md5(bytes);
    }

    public static String md5(byte[] content) {
        MD md = new MD();
        md.update(content, content.length);
        return md.getMd();
    }

    public static String md5(File file) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
            String md=MD.md5(fis);
            fis.close();
            return md;
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            return null;
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static String md5(InputStream fis) {
        try {
            byte[] buffer = new byte[1024];
            MD md = new MD();
            int l = -1;
            while ((l = fis.read(buffer)) != -1) {
                md.update(buffer, l);
            }
            return md.getMd();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
