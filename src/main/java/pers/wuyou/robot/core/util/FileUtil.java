package pers.wuyou.robot.core.util;

import org.springframework.core.io.Resource;
import pers.wuyou.robot.core.RobotCore;

import java.io.*;

/**
 * @author wuyou
 */
public class FileUtil {
    private FileUtil() {
    }

    /**
     * 将文件保存到缓存目录
     *
     * @param is         文件流
     * @param fileName   文件名
     * @param folderName 文件夹名
     * @return 保存后的文件路径
     * @throws IOException IOException
     */
    public static String saveTempFile(InputStream is, String fileName, String folderName) throws IOException {
        File tempDir = new File(RobotCore.TEMP_PATH + folderName + File.separator);
        File temp = new File(RobotCore.TEMP_PATH + folderName + File.separator + fileName);
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new IOException("Destination '" + tempDir + "' directory cannot be created");
        }
        if (!tempDir.canWrite()) {
            throw new IOException("Destination '" + tempDir + "' cannot be written to");
        }
        try (BufferedInputStream bis = new BufferedInputStream(is);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp))) {
            int len;
            byte[] buf = new byte[10240];
            while ((len = bis.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            bos.flush();
        }
        return temp.getPath();
    }

    /**
     * 将resource文件保存到临时路径
     *
     * @param resource   resource文件
     * @param folderName 文件夹名
     * @throws IOException IOException
     */
    public static void saveResourceToTempDirectory(Resource resource, String folderName) throws IOException {
        saveTempFile(resource.getInputStream(), resource.getFilename(), folderName);
    }
}
