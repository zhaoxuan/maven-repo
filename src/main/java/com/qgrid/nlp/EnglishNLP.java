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


import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 英文自然语言处理相关.
 */
public class EnglishNLP {

    /**
     * 去掉字符串里面的停用词和标点符号.
     * 只是保留英文实词.
     *
     * @param content String.
     *
     * @return String
     *
     * @throws null
     */
    public static String GetNotional(String content) {
        String result;

        List<Term> terms = NotionalTokenizer.segment(content);
        List<String> termArray = new LinkedList<>();

        // 只保留实词
        for (Term term : terms) {
            termArray.add(term.word);
        }

        result = StringUtils.join(termArray, " ");
        return result;
    }
}
