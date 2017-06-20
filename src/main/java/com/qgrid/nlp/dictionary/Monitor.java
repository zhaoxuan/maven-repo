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

package com.qgrid.nlp.dictionary;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Class description.
 */
public class Monitor implements Runnable {
    private final static Logger logger = Logger.getLogger(Monitor.class.getName());
    private static CloseableHttpClient httpclient = HttpClients.createDefault();

    /*
   * 上次更改时间
   */
    private String last_modified;
    /*
     * 资源属性
     */
    private String eTags;

    /*
     * 请求地址
     */
    private String location;

    public Monitor(String location) {
        this.location = location;
        this.last_modified = null;
        this.eTags = null;
    }

    /**
     * 监控流程：
     *  1.向词库服务器发送Head请求
     *  2.从响应中获取Last-Modify、ETags字段值，判断是否变化
     *  3.如果未变化，休眠1min，返回第1步
     *  4.如果有变化，重新加载词典
     *  5.休眠1min，返回第1步
     *
     */
    @Override
    public void run() {
        //超时设置
        RequestConfig rc = RequestConfig.custom().setConnectionRequestTimeout(10*1000)
            .setConnectTimeout(10*1000).setSocketTimeout(15*1000).build();

        HttpHead head = new HttpHead(location);
        head.setConfig(rc);

        //设置请求头
        if (last_modified != null) {
            head.setHeader("If-Modified-Since", last_modified);
        }
        if (eTags != null) {
            head.setHeader("If-None-Match", eTags);
        }

        CloseableHttpResponse response = null;

        try {
            response = httpclient.execute(head);

            //返回200 才做操作
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == 200) {

                if (((response.getLastHeader("Last-Modified") != null) && !response.getLastHeader("Last-Modified").getValue().equalsIgnoreCase(last_modified))
                    || ((response.getLastHeader("ETag") != null) && !response.getLastHeader("ETag").getValue().equalsIgnoreCase(eTags))) {

                    // 远程词库有更新,需要重新加载词典，并修改last_modified, eTags
                    Dictionary.reloadChineseMainDict();

                    // 保存上一次的时间
                    last_modified = response.getLastHeader("Last-Modified") == null ? null : response.getLastHeader("Last-Modified").getValue();
                    eTags = response.getLastHeader("ETag") == null ? null : response.getLastHeader("ETag").getValue();
                    logger.info("Update dict completely.");
                } else {
                    logger.info("No update dict");
                }
            } else if (response.getStatusLine().getStatusCode() == 304) {
                //没有修改，不做操作
                logger.info("No update dict with response code 304");
            } else {
                logger.info(String.format("remote_ext_dict %s return bad code %d" , location , response.getStatusLine().getStatusCode()));
            }

        } catch (Exception e) {
            logger.error(String.format("remote_ext_dict %s error!", e.getMessage()));
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
