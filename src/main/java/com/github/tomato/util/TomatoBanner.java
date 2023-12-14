package com.github.tomato.util;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * @author liuxin
 * 2023/2/2 16:20
 */
public class TomatoBanner {

    private static final String NAME = "Tomato";
    private static final String DEFAULT_BANNER = " _____                      _        \n" +
            "/__   \\___  _ __ ___   __ _| |_ ___  \n" +
            "  / /\\/ _ \\| '_ ` _ \\ / _` | __/ _ \\ \n" +
            " / / | (_) | | | | | | (_| | || (_) |\n" +
            " \\/   \\___/|_| |_| |_|\\__,_|\\__\\___/ ";

    public static void print(String type) {
        printVersion(type);
    }


    /**
     * 打印版本号
     */
    private static void printVersion(String type) {
        System.out.println();
        PrintStream printStream = System.out;
        String version = TomatoBanner.getVersion();
        version = version != null ? " (v" + version + ") " : "";
        StringBuilder padding = new StringBuilder();

        while (padding.length() < 42 - (version.length() + NAME.length())) {
            padding.append(" ");
        }

        printStream.println(AnsiOutput.toString(new Object[]{AnsiColor.BRIGHT_RED, DEFAULT_BANNER + "     (Mode: " + type + ")", AnsiColor.DEFAULT, padding.toString(), AnsiStyle.FAINT}));
        printStream.println();
        printStream.println(AnsiOutput.toString(new Object[]{AnsiColor.GREEN, " :: Tomato :: ", AnsiColor.DEFAULT, padding.toString(), AnsiStyle.FAINT, version}));
        printStream.println();
    }

    /**
     * 获取组件版本号
     *
     * @return String
     */
    private static String getVersion() {
        String implementationVersion = TomatoBanner.class.getPackage().getImplementationVersion();
        if (implementationVersion != null) {
            return implementationVersion;
        } else {
            CodeSource codeSource = TomatoBanner.class.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                return null;
            } else {
                URL codeSourceLocation = codeSource.getLocation();

                try {
                    URLConnection connection = codeSourceLocation.openConnection();
                    if (connection instanceof JarURLConnection) {
                        return getImplementationVersion(((JarURLConnection) connection).getJarFile());
                    } else {
                        JarFile jarFile = new JarFile(new File(codeSourceLocation.toURI()));
                        Throwable var5 = null;

                        String var6;
                        try {
                            var6 = getImplementationVersion(jarFile);
                        } catch (Throwable var16) {
                            var5 = var16;
                            throw var16;
                        } finally {
                            if (jarFile != null) {
                                if (var5 != null) {
                                    try {
                                        jarFile.close();
                                    } catch (Throwable var15) {
                                        var5.addSuppressed(var15);
                                    }
                                } else {
                                    jarFile.close();
                                }
                            }

                        }

                        return var6;
                    }
                } catch (Exception var18) {
                    return null;
                }
            }
        }
    }

    /**
     * 获取包文件对应的版本用于banner的打印
     *
     * @param jarFile 文件
     * @return String 版本号
     * @throws IOException
     */
    private static String getImplementationVersion(JarFile jarFile) throws IOException {
        return jarFile.getManifest().getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }

}
