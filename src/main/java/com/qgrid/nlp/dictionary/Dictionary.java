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


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.collection.trie.bintrie.BinTrie;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.other.CharTable;
import com.qgrid.CommonCore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 字典类
 * 用于实时更新词典库
 */
public class Dictionary {
    private final static Logger logger = Logger.getLogger(Dictionary.class.getName());

    /**
     * 重新加载中文词典<br>
     *     trie 替换
     */
    public static void reloadChineseMainDict() {
        CustomDictionary.trie = null;
        // 使用临时的词典，避免影响 CustomDictionary.trie 的使用
        BinTrie _trie = new BinTrie<CoreDictionary.Attribute>();

        CoreDictionary.Attribute att = new CoreDictionary.Attribute(Nature.nz, 1);
        List<String> words = getRemoteWords(CommonCore.remoteChineseMainDict);

        for (String word : words) {
            if (word == null) continue;
            if (HanLP.Config.Normalization) word = CharTable.convert(word);
            _trie.put(word, att);
        }

        // TODO
        // HanLP 暂时没有 reload 的功能，测试 trie = null 暂时没有内存泄露
        CustomDictionary.trie = null;
        // 替换新的字典
        CustomDictionary.trie = _trie;
    }

    /**
     * 请求 url 返回字典文本<br>
     *     txt 文件，UTF-8 编码
     *     每一行是一个单词
     *     word 词性 词频
     * @param location 远程文件的 URL 地址
     * @return List<String>
     */
    private static List<String> getRemoteWords(String location) {
        List<String> buffer = new ArrayList<String>();

        // 设置超时时间
        RequestConfig rc = RequestConfig.custom()
            .setConnectionRequestTimeout(10 * 1000)
            .setConnectTimeout(10 * 1000)
            .setSocketTimeout(60 * 1000).build();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        BufferedReader in;

        HttpGet get = new HttpGet(location);
        get.setConfig(rc);

        try {
            response = httpclient.execute(get);
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == 200) {

                String charset = "UTF-8";
                // 获取编码，默认为utf-8
                if (response.getEntity().getContentType().getValue().contains("charset=")) {
                    String contentType = response.getEntity().getContentType().getValue();
                    charset = contentType.substring(contentType.lastIndexOf("=") + 1);
                }

                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
                String line;
                while ((line = in.readLine()) != null) {
                    buffer.add(line);
                }

                in.close();
                return buffer;
            }
        } catch (IOException e) {
            logger.error(String.format("getRemoteWords %s error", e.getMessage()));

        } finally {
            try {
                // 关闭 request 对象
                httpclient.close();

                // 关闭 response 对象
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(String.format("Close http %s error", e.getMessage()));
            }
        }
        return buffer;
    }
}
