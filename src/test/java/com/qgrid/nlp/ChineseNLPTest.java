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

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.Assert;

import java.util.List;


/**
 * 中文自然语言处理测试.
 */
public class ChineseNLPTest {

    /**
     * 输入一个字符串.
     *
     */
    @Test
    public void testGetNotional() {
        String text = "发布了头条文章：《共享单车的决战场：两个“扫一扫”》 °共享单车的决战场：两个“扫一扫” b <U+200B><U+200B><U+200B><U+200B>";

        String actual = ChineseNLP.GetNotional(text);
        String expected = "发布头条文章共享单车决战扫扫共享单车决战扫扫";

        Assert.assertEquals(expected, actual);
    }

    /**
     * 提取关键词
     */
    @Test
    public void testGetKeywords() {
        String content = "&quot备受瞩目的后三国大剧《大军师司马懿之军师联盟》（以下简称《军师联盟》），6月8日在北京举行了“风云际会”新闻发布会。导演张永新，总制片人兼主演吴秀波以及刘涛、李晨、于和伟、翟天临、张芷溪、王劲松、王东、肖顺尧、檀健次、章贺、曹磊、来喜、陆思宇等主演悉数到场。吴秀波与刘涛现场“甜蜜”互动，李晨被吴秀波赞是“有古风的人”，于和伟化身曹操吟唱《短歌行》，发布会现场高潮不断。 \n" +
            "发布会启动仪式，全体演员主创合影 \n" +
            "吴秀波刘涛默契互动 于和伟现场吟唱《短歌行》 \n" +
            "此次发布会，由郭德纲和马东作为主持人登台，一番幽默的开场，掀起了当天发布会的第一个高潮。剧中饰演司马懿夫妇的吴秀波和刘涛，互相搀扶上台，狗粮撒到停不下来。吴秀波解释道，在剧中两人年纪越来越大，所以他们到了老年经常互相搀扶。吴秀波还透露剧中在司马懿家事事都由张春华做主。刘涛调侃自己剧中是个“悍妇”，而到了现场玩起“谁是卧底”的游戏，竟成游戏黑洞，首轮就被淘汰，现场跟李晨讨教游戏规则。 \n" +
            "吴秀波和刘涛 \n" +
            "据了解，在剧中饰演司马懿“老板”曹丕的李晨，为了接这部戏，特意调整已经满负荷的工作，全力投入拍摄,并被吴秀波称赞为“有古风的人”。而李晨也直言，这个剧组有人情味儿，演员们都受到了很多照顾，在拍摄的日子里都收获颇多。 \n" +
            "曹操的扮演者于和伟，被郭德纲点名现场小秀了一段《短歌行》，“明明如月，何时可掇?忧从中来，不可断绝”，旋律悠长苍凉、气势浑厚高远，一展曹公放眼天下的雄心壮志。 \n" +
            "曹魏视角看三国 大军师司马懿不走寻常路 \n" +
            "《军师联盟》讲述了魏国大军师司马懿跌宕起伏的一生，描绘了一个波澜壮阔的后三国时代。在已经曝光的预告片中，吴秀波饰演的世家子弟司马懿，面对接踵而来的危机，小心翼翼、如履薄冰，一心想辅佐曹魏君主结束乱世；同时他爱老婆、爱孩子，为了守护自己的家族，在“大丈夫”和“小男人”的身份之间自由切换。";

        List<String> keywords = ChineseNLP.GetKeywords(content, 5);
        String actual = StringUtils.join(keywords, ",");
        String expected = "司马,剧中,发布会,军师,演员";

        Assert.assertEquals(expected, actual);
    }
}
