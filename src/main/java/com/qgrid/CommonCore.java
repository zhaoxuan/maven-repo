/*
* Copyright (C) 2016 JohnZ
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the Free
* Software Foundation, either version 3 of the License, or (at your option)
* any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along
* with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.qgrid;


import com.qgrid.nlp.dictionary.Monitor;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 公共代码的初始化方法
 *      其他所有共有代码需要执行初始的，都必须在这里执行
 */
public class CommonCore {
    private final static Logger logger = Logger.getLogger(CommonCore.class.getName());

    // 单例
    private static CommonCore singleton;
    // 定时任务
    private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

    // 默认配置文件，在 resources 里面
    public static String configFileName = "common.properties";
    public static Properties config = new Properties();

    // 中文主词典
    public static boolean isRemoteChineseMainDict = false;
    public static String remoteChineseMainDict;

    /**
     * 初始化方法.<br>
     *     所有的配置都可以在这里完成
     *     synchronized 只允许一个线程执行初始化代码
     *
     * @return CommonCore
     *
     */
    public static synchronized CommonCore initial() {

        if (CommonCore.singleton == null) {
            CommonCore.singleton = new CommonCore();
            URL path = CommonCore.singleton.getClass().getClassLoader().getResource(configFileName);

            if (path != null) {
                try {
                    String configFilePath = path.getPath();

                    boolean isExists = new File(configFilePath).exists();

                    if (isExists) {
                        FileInputStream input = new FileInputStream(configFilePath);
                        CommonCore.config.load(input);
                    }
                } catch (IOException e) {
                    logger.warn("Load common.properties file is not exists, use default value.");
                }

            }

            if ("true".equals(config.getProperty("isRemoteChineseMainDict"))) {
                isRemoteChineseMainDict = true;
                remoteChineseMainDict = config.getProperty("remoteChineseMainDict");
                pool.scheduleAtFixedRate(new Monitor(remoteChineseMainDict), 10, 60, TimeUnit.SECONDS);
            }

        }

        return CommonCore.singleton;
    }

    public static CommonCore getInstance() {
        if (singleton == null) {
            throw new IllegalStateException("CommonCore has not been initial，please callback initial function first.");
        }
        return singleton;
    }
}
