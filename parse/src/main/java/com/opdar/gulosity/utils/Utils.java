package com.opdar.gulosity.utils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Jeffrey on 2015/4/10.
 * E-Mail:shijunfan@163.com
 * Site:opdar.com
 * QQ:362116120
 */
public class Utils {
    public static byte[] is2byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream arrayBuffer = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(b)) != -1) {
            arrayBuffer.write(b, 0, len);
        }
        inputStream.close();
        arrayBuffer.close();
        return arrayBuffer.toByteArray();
    }

    public static char byte2Char(byte[] b) {
        char c = (char) (((b[0] & 0xFF) << 8) | (b[1] & 0xFF));
        return c;
    }


    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {

                        aChar = theString.charAt(x++);

                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }


    public static boolean isClassPath(String params) {
        if (params != null && params.indexOf("classpath:") == 0 && params.trim().length() > 10) {
            return true;
        }
        return false;
    }

    public static String getClassPath(String params) {
        if (isClassPath(params)) {
            return params.substring(10);
        }
        return null;
    }

    public static String parseSignFactor(String factor, List<String> parentMapping) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = factor.indexOf("#{");
        if (index != -1) {
            stringBuilder.append(factor.substring(0, index));
            int index2 = factor.indexOf("}");
            if (index2 != -1) {
                String key = factor.substring(index + 2, index2);

                parentMapping.add(key);
                stringBuilder.append(" %s ");
                stringBuilder.append(factor.substring(index2 + 1, factor.length()));
            } else {
                stringBuilder.append(factor.substring(index));
            }
            factor = parseSignFactor(stringBuilder.toString(), parentMapping);
        }
        return factor;
    }

    public static String testCamelField(String field) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] camels = field.split("_");
        for(int i=0;i<camels.length;i++){
            String s = camels[i];
            if(i == 0){
                stringBuilder.append(s);
            }else{
                stringBuilder.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
            }
        }
        return stringBuilder.toString();
    }

    public static String testUnderscore(String field,Boolean isUpper) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<field.length();i++){
            char c = field.charAt(i);
            if(i != 0){
                if(Character.isUpperCase(c)){
                    stringBuilder.append('_');
                    if(isUpper != null){
                        if(isUpper){
                            c = Character.toUpperCase(c);
                        }else{
                            c = Character.toLowerCase(c);
                        }
                    }
                }
            }
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static String testField(String field) {
        if(field.length() == 1)return field.toUpperCase();
        if (field.indexOf("is") == 0) {
            field = field.replaceFirst("is", "");
        }
        if (field.length() < 3 && field.length() > 1) {
            return Character.toUpperCase(field.charAt(0)) + field.substring(1);
        }
        char c1 = field.charAt(0);
        char c2 = field.charAt(1);
        if (Character.isUpperCase(c1)) {
            return field;
        }
        if (Character.isLowerCase(c1) && Character.isUpperCase(c2)) {
            return field;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Character.toUpperCase(c1)).append(field.substring(1));
        return stringBuilder.toString();
    }

    public static String testRouter(String router) {
        int i = router.indexOf("/");
        if (i == -1) {
            return "/".concat(router);
        }

        StringBuilder _router = new StringBuilder();
        for (String r : router.split("/")) {
            if (!r.trim().equals("")) {
                _router.append(r).append("/");
            }
        }
        if (_router.length() > 0) {
            _router.deleteCharAt(_router.length() - 1);
            _router.insert(0, "/");
        }
        return _router.toString();
    }

    public static Map<String, String> spliteParams(String params) {
        Map<String, String> values = new HashMap<String, String>();
        if (params == null) return values;
        String[] ps = null;
        if (params.indexOf(";") != -1) {
            ps = params.split(";");
        } else {
            ps = new String[]{params};
        }
        for (String s : ps) {
            if (s.indexOf(",") != -1) {
                String[] ss = s.split(",");
                if (ss.length > 1 && ss[0].trim().length() > 0) {
                    values.put(ss[0], ss[1]);
                }
            }
        }
        return values;
    }

    public static void save(byte[] code) {
        String filename = UUID.randomUUID().toString().replace("-","")+".class";
        try {
            File file;
            FileOutputStream fos = new FileOutputStream(file = new File(filename));
            System.out.print("FILE PATH : ");
            System.out.println(file.getAbsoluteFile());
            fos.write(code);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static byte[] byteMerger(byte[] bytes1, byte[] bytes2) {
        byte[] byte_3 = new byte[bytes1.length + bytes2.length];
        System.arraycopy(bytes1, 0, byte_3, 0, bytes1.length);
        System.arraycopy(bytes2, 0, byte_3, bytes1.length, bytes2.length);
        return byte_3;
    }

    public static String setMethodName(String fieldName) {
        if (fieldName.length() < 2) return "set".concat(fieldName);
        char c1 = Character.toUpperCase(fieldName.charAt(0));
        boolean c2IsUpper = Character.isUpperCase(fieldName.charAt(1));
        if (c2IsUpper) return "set".concat(fieldName);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("set");
        stringBuilder.append(c1);
        stringBuilder.append(fieldName.substring(1, fieldName.length()));
        return stringBuilder.toString();
    }
}
