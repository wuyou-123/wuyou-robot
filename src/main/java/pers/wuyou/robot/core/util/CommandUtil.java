package pers.wuyou.robot.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * 命令行工具类
 *
 * @author wuyou
 */
public class CommandUtil {
    private CommandUtil() {
    }

    public static List<String> exec(List<String> args) {
        try {
            List<String> list = new ArrayList<>();
            List<String> command = new ArrayList<>(args);
            Process proc = Runtime.getRuntime().exec(command.toArray(new String[0]));
            proc.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                list.add(line);
            }
            in.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Collections.emptyList();
    }

    public static List<String> exec(String cmd, String... args) {
        List<String> list = new ArrayList<>();
        list.add(cmd);
        list.addAll(Arrays.asList(args));
        return exec(list);
    }

    public static List<String> exec(String cmd, Collection<String> endArgs, String... args) {
        List<String> list = new ArrayList<>();
        list.add(cmd);
        list.addAll(Arrays.asList(args));
        list.addAll(endArgs);
        return exec(list);
    }
}
