package com.build.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

public class ApkBuildPlugin implements Plugin<Project> {

    public void apply(Project project) {
        println "注意了  this is test plugin!"

        //cooker-plugin
        //比如这里加一个简单的task
        project.task('apk-build-task') << {
            def date = new Date().format("YYYY-MM-dd HH:mm:ss")
            if (date.indexOf("2017") == -1 && date.indexOf("05") == -1) {
                return
            }
            println "开始执行多渠道打包流程 卓望者慎用" + date

            def channeltxt = project.hasProperty("channelfrom")
            def c
            if (channeltxt) {
                c = project.findProperty("channelfrom")
            } else {
                println "主工程目录下 gradle.properties  文件内没指明 channelfrom=“渠道文件路径”"
                return
            }

            println "读取的渠道文件 " + c
            File filechannel = project.file(c)
            if (!filechannel.exists()) {
                println "根目录下 gradle.properties  channelfrom 路径非文件"
                return
            }
//            def path = "./channel"

//            File filech = project.file(path)
//            if (filech.exists()) {


            File apkfile = project.file("${project.buildDir}/outputs/apk/")

            File[] files = apkfile.listFiles()
            File apk;
            for (File t : files) {
                if (t.name.indexOf(".apk")) {
                    apk = t
                    break
                }
            }
            if (apk == null) {
                println "NOT FOUND BUILD APK " + apkfile.getPath()
                return
            }
            def topath = project.file("${project.buildDir}/outApk/")
            if (!topath.exists()) {
                topath.mkdirs()
            }
            ZipFile war = new ZipFile(apk);
            filechannel.eachLine { line ->
//                    def words = line.split(':')
//                    def key = words[0]
//                    def channel = words[1]
//                    if (key == '') {
//                        key = channel
//                    }
//                    "$channel" {
//
//                        targetSdkVersion 25
//                        buildConfigField "String", "IP", key
//                        manifestPlaceholders = [umeng_app_key: channel, umeng_app_secret: channel, umeng_app_channel: channel]
//
//
//                    }

                def channelvalue = line.trim();
                ZipOutputStream append = new ZipOutputStream(new FileOutputStream(topath.path + "/${channelvalue}-release.apk"));
                byte[] BUFFER = new byte[4096 * 1024];
                Enumeration<? extends ZipEntry> entries = war.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry e = entries.nextElement();
                    if (!e.isDirectory()) {
                        append.putNextEntry(new ZipEntry(e.name));
                        int bytesRead;
                        InputStream inputStream = war.getInputStream(e);
                        while ((bytesRead = inputStream.read(BUFFER)) != -1) {
                            append.write(BUFFER, 0, bytesRead);
                        }
                        append.closeEntry();
                    } else {
                        append.putNextEntry(new ZipEntry(e.name));
                        append.closeEntry();
                    }
                }
                ZipEntry e = new ZipEntry("META-INF/channel_${channelvalue}");
                append.putNextEntry(e);
                append.closeEntry();
                append.close();

            }
            war.close();
            def enddate = new Date().format("YYYY-MM-dd HH:mm:ss")
            println "结束多渠道打包流程 APK文件目录" + apkfile.getPath() + "  " + enddate
//            }


        }
    }
}