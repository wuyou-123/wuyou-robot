package pers.wuyou.robot.util;

import org.springframework.core.io.Resource;

import java.io.*;

/**
 * @author wuyou
 */
public class FileUtil {

    public static void saveTempFile(InputStream is, String fileName, String folderName) throws IOException {
        File tempDir = new File(RobotUtil.TEMP_PATH + folderName + File.separator);
        File temp = new File(RobotUtil.TEMP_PATH + folderName + File.separator + fileName);
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
    }

    public static void saveResourceToTempDirectory(Resource resource, String folderName) throws IOException {
        saveTempFile(resource.getInputStream(), resource.getFilename(), folderName);
    }
}
