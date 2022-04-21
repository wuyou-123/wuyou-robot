package pers.wuyou.robot.core.util;

import lombok.extern.slf4j.Slf4j;
import pers.wuyou.robot.core.RobotCore;
import pers.wuyou.robot.core.common.Constant;
import pers.wuyou.robot.game.common.GameException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 命令行工具类
 *
 * @author wuyou
 */
@Slf4j
public class CommandUtil {
    private CommandUtil() {
    }

    public static boolean checkEnv(String name) {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{name, "--version"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String s = reader.readLine();
            if (s == null) {
                reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                s = reader.readLine();
            }
            if (s == null || !s.toLowerCase(Locale.ROOT).contains(name)) {
                throw new GameException();
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public static List<String> exec(List<String> args) {
        try {
            List<String> list = new ArrayList<>();
            List<String> command = args.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (Constant.PYTHON.equals(command.get(0)) && RobotCore.PYTHON_PATH != null) {
                command.set(0, RobotCore.PYTHON_PATH);
            }
            Process proc = Runtime.getRuntime().exec(command.toArray(new String[0]));
            proc.waitFor();
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                list.add(line);
                log.info(line);
            }
            in.close();
            BufferedReader in2 = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String line2;
            while ((line2 = in2.readLine()) != null) {
                log.error(line2);
            }
            in2.close();
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

    public static void exec(String cmd, Collection<String> endArgs, String... args) {
        List<String> list = new ArrayList<>();
        list.add(cmd);
        list.addAll(Arrays.asList(args));
        list.addAll(endArgs);
        exec(list);
    }
}
