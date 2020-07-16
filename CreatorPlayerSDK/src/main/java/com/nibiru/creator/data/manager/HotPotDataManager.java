package com.nibiru.creator.data.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.nibiru.creator.data.HotPotData;
import com.nibiru.creator.data.HotPotData.BackGroundAudio;
import com.nibiru.creator.data.HotPotData.Event;
import com.nibiru.creator.data.HotPotData.HotPot;
import com.nibiru.creator.data.HotPotData.SkyBoxSceneAttribute;
import com.nibiru.creator.data.HotPotData.SkyBoxSceneGroup;
import com.nibiru.creator.data.HotPotData.ProjVersion;
import com.nibiru.creator.data.NPKData;
import com.nibiru.creator.data.NptData;
import com.nibiru.creator.data.SceneData;
import com.nibiru.creator.utils.Constants;
import com.nibiru.creator.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class HotPotDataManager {
    private static final String HEADER_NPJ = "78627FD028274EC8";    //npj文件的文件头
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String KEY = "78JBG51AEUXXK5M7";   // 原AES加密的密钥
    //    private static final String KEY_CMCC = "QM21I6MK56148FN6";   // CMCC AES测试加密的密钥
    private static final String KEY_CMCC = "WM0YRKK04BXOVM5G";   // CMCC AES正式加密的密钥
    private static String key = KEY;
    //    private Context mContext;
    private String fileName;
    private NptData nptData;
    private static HotPotDataManager self;
    private String fileHeader;

    private byte[] headerBytes;
    private byte[] versionBytes;
    private byte[] dataBytes;
    private SkyBoxSceneGroup sceneGroup;
    public Map<String, String> fileNameMap;

    private HotPotDataManager() {
//        this.mContext = mContext.getApplicationContext();
       /* this.nptData = nptData;
        this.fileName = FileUtils.getPBDataPath(nptData);*/
    }

    public static HotPotDataManager getInstance() {
        if (self == null) {
            synchronized (HotPotDataManager.class) {
                if (self == null) {
                    self = new HotPotDataManager();
                }
            }
        }
        return self;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDataStream(InputStream is) {
        if (is == null) {
            return;
        }
        parseFile(is);
        sceneGroup = deserializeSceneGroup();
    }

    private SkyBoxSceneAttribute deserializeSceneAttribute() {
        SkyBoxSceneAttribute dSceneAttribute = null;
        File dataFile = new File(fileName);
        if (dataFile.exists()) {
            try {
                FileInputStream fis = new FileInputStream(dataFile);
                dSceneAttribute = SkyBoxSceneAttribute.parseFrom(fis);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dSceneAttribute;
    }

    private SkyBoxSceneGroup deserializeSceneGroup() {
        SkyBoxSceneGroup sceneGroup = null;
        try {
            if (dataBytes != null) {
                sceneGroup = SkyBoxSceneGroup.parseFrom(dataBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sceneGroup;
    }

    public String getProjectVersion() {
        if (sceneGroup != null) {
            ProjVersion version = sceneGroup.getProjVersion();
            return version.getVersion();
        }
        return null;
    }

    public boolean needDividedBy1000() {
        String version = getProjectVersion();
        if (version != null) {
            try {
                int versionNum = Integer.parseInt(version.replaceAll("\\.", ""));
                if (versionNum >= 371) {
                    return true;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getFirstSceneId() {
        int firstSceneId = -1;
        try {
            SkyBoxSceneGroup sceneGroup;
            if (dataBytes != null) {
                sceneGroup = SkyBoxSceneGroup.parseFrom(dataBytes);
                firstSceneId = sceneGroup.getMFirstSceneID();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return firstSceneId;
    }

    /**
     * 加密
     *
     * @param src 待加密数据
     * @param key 加密所用的密钥
     * @return 加密后的数据
     */
    private static byte[] encrypt(byte[] src, String key) {
        if (key == null || key.length() != 16) {
            return null;
        }
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            result = cipher.doFinal(src);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解密
     *
     * @param src 待解密数据
     * @param key 解密所用的密钥
     * @return 解密后的数据
     */
    private byte[] decrypt(byte[] src, String key) {
        if (key == null || key.length() != 16) {
            return null;
        }
        byte[] result = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            result = cipher.doFinal(src);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解析文件以获取文件头、MD5等相关信息
     */
    private void parseFile(InputStream is) {
        ByteArrayOutputStream bos = null;
        try {
//            is = new FileInputStream(filePath);
            int len = 8;
            byte[] b = new byte[len];
            is.read(b, 0, len);
            headerBytes = b;
            // 字节序问题 Java都是高字节开头，而windows的字节序为低字节开头
            byte[] temp = new byte[len];
            for (int i = 0; i < len; i++) {
                temp[len - i - 1] = b[i];
            }
            String fileHeader = bytesToHexString(temp);
            this.fileHeader = fileHeader;
//            System.out.println("Header: " + fileHeader);
            if (!HEADER_NPJ.equals(fileHeader)) {
//                is = new FileInputStream(filePath);
                bos = new ByteArrayOutputStream();
                byte[] source = new byte[2048];
                len = -1;
                while ((len = is.read(source)) != -1) {
                    bos.write(source, 0, len);
                }
//                byte[] dataByte = bos.toByteArray();
                dataBytes = bos.toByteArray();
                return;
            }

            // 跳过文件版本的4字节，读取MD5值
//            is.skip(4);
            len = 4;
            b = new byte[len];
            is.read(b, 0, len);
            versionBytes = b;

            len = 16;
            b = new byte[len];
            is.read(b, 0, len);
            // 事先写入文件的MD5值
            String md5 = parseByte2HexStr(b);

            // 获取加密后的字节数组
            bos = new ByteArrayOutputStream();
            byte[] source = new byte[2048];
            len = -1;
            while ((len = is.read(source)) != -1) {
                bos.write(source, 0, len);
            }
            byte[] dataByte = bos.toByteArray();
            // 获取MD5值
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(dataByte);
            byte[] resultByteArray = messageDigest.digest();
            String dataMD5 = parseByte2HexStr(resultByteArray);
            boolean result = md5.equals(dataMD5);
            if (Constants.value_isCMCC) {
                key = KEY_CMCC;
            }
            dataBytes = decrypt(dataByte, key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件路径获取文件头信息
     *
     * @param filePath 文件路径
     * @return 文件头信息
     */
    private String getFileHeader(String filePath) {
        FileInputStream is = null;
        String fileHeader = null;
        try {
            is = new FileInputStream(filePath);
            int len = 8;
            byte[] b = new byte[len];
            is.read(b, 0, b.length);
            //字节序问题 Java都是高字节开头，而windows的字节序为低字节开头
            byte[] temp = new byte[len];
            for (int i = 0; i < len; i++) {
                temp[len - i - 1] = b[i];
            }
            fileHeader = bytesToHexString(temp);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileHeader;
    }

    /**
     * 判断文件是否合法
     * 获取事先写入文件的MD5值与加密后数据的MD5值，如果两值相同，代表文件合法
     *
     * @param filePath 文件路径
     * @return 文件是否合法
     */
    private boolean isLegalFile(String filePath) {
        boolean result = false;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            //跳过文件头的8个字节及文件版本的4字节
            fis.skip(12);
            int len = 16;
            byte[] b = new byte[len];
            fis.read(b, 0, len);
            //事先写入文件的MD5值
            String md5 = parseByte2HexStr(b);

            //获取加密后的字节数组
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] source = new byte[2048];
            len = -1;
            while ((len = fis.read(source)) != -1) {
                bos.write(source, 0, len);
            }
            byte[] dataByte = bos.toByteArray();
            //获取MD5值
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(dataByte);
            byte[] resultByteArray = messageDigest.digest();
            String dataMD5 = parseByte2HexStr(resultByteArray);
            result = md5.equals(dataMD5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private byte[] getMD5(byte[] input) {
        if (input == null) {
            return null;
        }
        byte[] resultByteArray = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(input);
            resultByteArray = messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return resultByteArray;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf 待转换的二进制数据
     * @return 转换后的16进制字符串
     */
    private String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将表示文件头信息的byte数组转换成String类型表示
     *
     * @param src 文件头信息的byte数组
     * @return 文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        if (src == null || src.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        String hv;
        for (byte b : src) {
            // 以十六进制无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(b & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    public List<HotPot> getHotPots() {
        List<HotPot> hotPots = new ArrayList<HotPot>();
        SkyBoxSceneAttribute sceneAttribute = deserializeSceneAttribute();
        if (sceneAttribute != null) {
            List<HotPot> tempHotPots = sceneAttribute.getMHotPotsList();
            if (tempHotPots != null && tempHotPots.size() > 0) {
                hotPots.addAll(tempHotPots);
            }
        }
        return hotPots;
    }

    public List<HotPot> getHotPots(int sceneId) {
        List<HotPot> hotPots = new ArrayList<HotPot>();
        if (sceneGroup != null) {
            List<SkyBoxSceneAttribute> list = sceneGroup.getMScenesList();
            if (list != null && list.size() > 0) {
                for (SkyBoxSceneAttribute sceneAttribute : list) {
                    if (sceneId == sceneAttribute.getMID()) {
                        List<HotPot> tempHotPots = sceneAttribute.getMHotPotsList();
                        if (tempHotPots != null && tempHotPots.size() > 0) {
                            hotPots.addAll(tempHotPots);
                        }
                        break;
                    }
                }
            }
        }
        return hotPots;
    }

    public List<Event> getEvents(int sceneId) {
        List<Event> events = new ArrayList<Event>();
        if (sceneGroup != null) {
            List<SkyBoxSceneAttribute> list = sceneGroup.getMScenesList();
            if (list != null && list.size() > 0) {
                for (SkyBoxSceneAttribute sceneAttribute : list) {
                    if (sceneId == sceneAttribute.getMID()) {
                        List<Event> tempEvents = sceneAttribute.getMEventsList();
                        if (tempEvents != null && tempEvents.size() > 0) {
                            events.addAll(tempEvents);
                        }
                        break;
                    }
                }
            }
        }
        return events;
    }

    public BackGroundAudio getBackgroundAudio() {
        BackGroundAudio backgroundAudio = null;
        SkyBoxSceneAttribute sceneAttribute = deserializeSceneAttribute();
        if (sceneAttribute != null) {
            backgroundAudio = sceneAttribute.getMBackGroundAudio();
        }
        return backgroundAudio;
    }

    public BackGroundAudio getBackgroundAudio(int sceneId) {
        BackGroundAudio backgroundAudio = null;
        if (sceneGroup != null) {
            List<SkyBoxSceneAttribute> list = sceneGroup.getMScenesList();
            if (list != null && list.size() > 0) {
                for (SkyBoxSceneAttribute sceneAttribute : list) {
                    if (sceneId == sceneAttribute.getMID()) {
                        backgroundAudio = sceneAttribute.getMBackGroundAudio();
                        break;
                    }
                }
            }
        }
        return backgroundAudio;
    }

    public BackGroundAudio getAllBackgroundAudio() {
        BackGroundAudio backgroundAudio = null;
        if (sceneGroup != null) {
            backgroundAudio = sceneGroup.getMAllBackGroundAudio();
        }
        return backgroundAudio;
    }

    public SkyBoxSceneAttribute getSkyBoxSceneAttribute() {
        return deserializeSceneAttribute();
    }

    public SkyBoxSceneAttribute getSkyBoxSceneAttribute(int sceneId) {
        if (sceneGroup != null) {
            List<SkyBoxSceneAttribute> list = sceneGroup.getMScenesList();
            if (list != null && list.size() > 0) {
                for (SkyBoxSceneAttribute sceneAttribute : list) {
                    if (sceneId == sceneAttribute.getMID()) {
                        return sceneAttribute;
                    }
                }
            }
        }
        return null;
    }

    public String getVideoFileName(int sceneId) {
        if (sceneGroup != null) {
            List<SkyBoxSceneAttribute> list = sceneGroup.getMScenesList();
            if (list != null && list.size() > 0) {
                for (SkyBoxSceneAttribute sceneAttribute : list) {
                    if (sceneId == sceneAttribute.getMID()) {
                        if (Constants.value_isCMCC) {
//                            return getTrueUrl(sceneAttribute.getMFileUrl()).getPlayUrl();
                            return sceneAttribute.getMFileUrl();
                        } else {
                            return sceneAttribute.getMFileName();
                        }
                    }
                }
            }
        }
        return null;
    }

    public Bitmap getSkyBoxBitmap(String sceneName, int width, int height) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sceneName, options);
        options.inSampleSize = Utils.getRatioSize(width, height, options.outWidth, options.outHeight);
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(sceneName, options);
        return bitmap;
    }

    public List<SceneData> getSceneDataList() {
        List<SceneData> sceneDataList = new ArrayList<SceneData>();
        if (sceneGroup != null) {
            List<SkyBoxSceneAttribute> list = sceneGroup.getMScenesList();
            int firstSceneID = sceneGroup.getMFirstSceneID();
            if (list != null && list.size() > 0) {
                for (SkyBoxSceneAttribute sceneAttribute : list) {
                    SceneData sceneData = new SceneData();
                    int sceneId = sceneAttribute.getMID();
                    sceneData.setSceneId(sceneId);
                    sceneData.setSceneName(sceneAttribute.getMSceneName());
                    String fileName = sceneAttribute.getMFileName();
                    sceneData.setFileName(fileName);
                    if (sceneAttribute.hasMScenetype()) {
                        int sceneType = sceneAttribute.getMScenetype();
                        sceneData.setSceneType(sceneType);
                    }
                    if (firstSceneID == sceneId) {
                        sceneData.setPrimaryScene(true);
                    } else {
                        sceneData.setPrimaryScene(false);
                    }
                    if (sceneAttribute.hasMRenderMode()) {
                        sceneData.setRenderMode(sceneAttribute.getMRenderMode().getNumber());
                    }
                    if (sceneAttribute.hasMFileUrl()) {
                        sceneData.setFileUrl(sceneAttribute.getMFileUrl());
                    }
                    sceneDataList.add(sceneData);
                }
            }
        }
        return sceneDataList;
    }

    public String getFileUrl(String fileName) {
        if (Constants.value_isCMCC) {
            return getTrueUrl(fileName).getPlayUrl();
        } else {
            return fileName;
        }
    }

    public NPKData getTrueUrl(String fileUrl) {

        NPKData npkData = new NPKData();
        try {
//            String path = "http://112.13.96.226:20081/contentPalyInfo/getResUrl?inputUrl=" + fileUrl;
            //1：url对象
            URL url = new URL("http://112.13.96.226:20081/contentPalyInfo/getResUrl");

            //2;url.openconnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //3设置请求参数
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10 * 1000);
            //请求头的信息
            String body = "inputUrl=" + URLEncoder.encode(fileUrl);
            conn.setRequestProperty("Content-Length", String.valueOf(body.length()));
            conn.setRequestProperty("Cache-Control", "max-age=0");
//            conn.setRequestProperty("Origin", "http://192.168.1.100:8081");

            //设置conn可以写请求的内容
            conn.setDoOutput(true);
            conn.getOutputStream().write(body.getBytes());

            //4响应码
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                String result = readFromStream(is);
                JSONObject jo = new JSONObject(result);
                if (jo.getInt("recode") == 1) {
                    JSONObject jo1 = jo.getJSONObject("data");
                    if (jo1 != null) {
                        JSONArray ja1 = jo1.getJSONArray("items");
                        if (ja1 != null) {
                            JSONObject jo2 = (JSONObject) ja1.get(0);
                            npkData.setInputUrl(jo2.getString("inputUrl"));
                            npkData.setPlayUrl(jo2.getString("playUrl"));
                        }
                        return npkData;
                    }
                }
                return npkData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return npkData;
        }
        return npkData;
    }

    public NPKData getTrueUrl_post(String fileUrl) {
        NPKData npkData = new NPKData();
        try {
//            String path = "http://112.13.96.226:20081/contentPalyInfo/getResUrl?inputUrl=" + fileUrl;
            //1：url对象
            URL url = new URL("http://112.13.96.226:20081/contentPalyInfo/getResUrl");

            //2;url.openconnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //3设置请求参数
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10 * 1000);
            //请求头的信息
            String body = "inputUrl=" + URLEncoder.encode(fileUrl);
            conn.setRequestProperty("Content-Length", String.valueOf(body.length()));
            conn.setRequestProperty("Cache-Control", "max-age=0");
//            conn.setRequestProperty("Origin", "http://192.168.1.100:8081");

            //设置conn可以写请求的内容
            conn.setDoOutput(true);
            conn.getOutputStream().write(body.getBytes());

            //4响应码
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                String result = readFromStream(is);
                JSONObject jo = new JSONObject(result);
                if (jo.getInt("recode") == 1) {
                    JSONObject jo1 = jo.getJSONObject("data");
                    if (jo1 != null) {
                        JSONArray ja1 = jo1.getJSONArray("items");
                        if (ja1 != null) {
                            JSONObject jo2 = (JSONObject) ja1.get(0);
                            npkData.setInputUrl(jo2.getString("inputUrl"));
                            npkData.setPlayUrl(jo2.getString("playUrl"));
                        }
                        return npkData;
                    }
                }
                return npkData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return npkData;
        }
        return npkData;
    }

    public NPKData loadNpk_post(String fileUrl) {
        NPKData npkData = new NPKData();
        try {
//            String path = "http://112.13.96.226:20081/contentPalyInfo/getResUrl?inputUrl=" + fileUrl;
            //1：url对象
            URL url = new URL(fileUrl);

            //2;url.openconnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //3设置请求参数
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(10 * 1000);
            //请求头的信息
            String body = "inputUrl=" + URLEncoder.encode(fileUrl);
            conn.setRequestProperty("Content-Length", String.valueOf(body.length()));
            conn.setRequestProperty("Cache-Control", "max-age=0");
//            conn.setRequestProperty("Origin", "http://192.168.1.100:8081");

            //设置conn可以写请求的内容
            conn.setDoOutput(true);
            conn.getOutputStream().write(body.getBytes());

            //4响应码
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                String result = readFromStream(is);
                JSONObject jo = new JSONObject(result);
                return npkData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return npkData;
        }
        return npkData;
    }

    /**
     * @param is 输入流
     * @return String 返回的字符串
     * @throws IOException
     */
    public String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        is.close();
        String result = baos.toString();
        baos.close();
        return result;
    }
}
