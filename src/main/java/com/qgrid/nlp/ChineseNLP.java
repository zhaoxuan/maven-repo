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

package com.qgrid.nlp;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.summary.TextRankKeyword;
import com.hankcs.hanlp.tokenizer.BasicTokenizer;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.logging.Logger;

/**
 * 中文自然语言处理相关.
 */
public class ChineseNLP {
     private final static Logger logger = Logger.getLogger(ChineseNLP.class.getName());

    /**
     * 去掉字符串里面的停用词和标点符号. 只是保留中文实词.
     *
     * @param content
     *            String.
     *
     * @return String
     *
     */
    public static String GetNotional(String content) {
        String result;

        List<Term> terms = NotionalTokenizer.segment(content);
        List<String> termArray = new LinkedList<>();
        // 去掉 nx (字母专名)
        // 只保留中文词组
        for (Term term : terms) {
            if (term.nature != Nature.nx) {
                termArray.add(term.word);
            }
        }

        result = StringUtils.join(termArray, "");
        return result;
    }

    /**
     * 提取中文文本里面的关键词.
     *
     * @param content
     *            String
     * @param size
     *            Integer
     *
     * @return List<String>
     *
     */
    public static List<String> GetKeywords(String content, Integer size) {
        List<String> out = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // 用 TextRank 里面的算法来完成排序
        TextRankKeyword textRankKeyword = new TextRankKeyword();
        // 默认分词器 以后可以替换其他分词器
        List<Term> termList = BasicTokenizer.segment(content);

        // 保留词性
        Map<String, String> termMap = new HashMap<>();
        for (Term term : termList) {
            // 去掉 nx (字母专名)
            // 只保留中文词组
            if (term.nature == Nature.nx) {
                continue;
            }
            termMap.put(term.word, term.nature.toString());
        }

        // 关键词排名
        Map<String, Float> result = textRankKeyword.getRank(termList);

        // 倒序排列
        List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(result.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // 取 top N
        Integer count = 0;

        for (Map.Entry entry : list) {
            String key = entry.getKey().toString();
            String nature = termMap.get(key);

            if (nature == null) {
                continue;
            }

            if (count < size) {
                if (nature.charAt(0) == 'n') {
                    out.add(key);
                    count++;
                }
                continue;
            } else {
                break;
            }
        }

        long endTime = System.currentTimeMillis();
        // logger.info("程序运行时间: " + (endTime - startTime) + " ms");

        return out;
    }
}
