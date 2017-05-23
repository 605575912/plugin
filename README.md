# plugin
APK插件打包插件
使用在Android Studio 开发中 gradle 插件，用于产生多个渠道APK工具
900个渠道 一分钟内打包完成，原理是美图的打包方式，只是修改为groovy ，方便gradle 使用
   /**
     * APK  获取渠道号方法
     * @param context
     * @return
     */
    private static String getChannelFromApk(Context context) {
        long startTime = System.currentTimeMillis();
        //从apk包中获取
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        //默认放在meta-inf/里， 所以需要再拼接一下
        String key = "META-INF/channel";
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith(key)) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String channel = "";
        if (!TextUtils.isEmpty(ret)) {
            String[] split = ret.split("_");
            if (split != null && split.length >= 2) {
                channel = ret.substring(split[0].length() + 1);
            }
        } else {
            channel = "default";
        }
        return channel;

    }
