package com.hjx.search_engine;

import com.hjx.search_engine.common.StringUtils;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCommon {

    private int getSepIndexOfTarget(String target,String sep,int count) {
        int num = 0;
        if(count==0) return 0;
        Pattern pattern = Pattern.compile(sep);
        Matcher mat = pattern.matcher(target);
        while(mat.find()) {
            num++;
            if(num==count) return mat.start();
        }
        return target.length();
    }

    private String getElementsWith(String target,int start,int num,String sep) {
        int startPos = getSepIndexOfTarget(target,sep,start);
        int endPos = getSepIndexOfTarget(target,sep,start+num);
        if(startPos>=0&&startPos<target.length()&&endPos>=0&&endPos<=target.length()&&startPos<endPos) {
            if(startPos==0) return target.substring(startPos,endPos);
            else return target.substring(startPos+sep.length(),endPos);
        }
        return null;
    }

    @Test
    public void testGetElementsWith() {
        String str = "76,110,61,104,4,41,91,46,86,40,90,77,89,25,118,132,122,35,93,97,48,81";
        String result = getElementsWith(str,-5,5,",");
        System.out.println(result);
    }


    @Test
    public void testJiebaSegementer() {
        JiebaSegmenter segmenter = new JiebaSegmenter();
//        //分词会重复
//        List<String> wordList1 = StringUtils.jiebaSegment("办事指南_浙江大学软件学院",JiebaSegmenter.SegMode.INDEX);
//        System.out.println(wordList1);
        //分词不会重复
        List<SegToken> wordList2 = segmenter.process("办事指南_浙江大学软件学院",JiebaSegmenter.SegMode.SEARCH);
        System.out.println(wordList2);
    }

    @Test
    public void testContentToDiscription() {
        String content = "学费催缴通知_浙江大学软件学院首页学院概况学院简介学院领导办学特色专业方向新闻中心办事指南学院风采联系我们招生信息招生简章招生通知招生问答网上报名考试大纲证书样式创新基地常见问题文件下载联系我们教学管理教务信息论文管理杭州在职专业导师实训管理实训平台文件下载表格下载思政工作重要信息支部风采学生社团党团工作评奖评优困难资助德育导师学子风采文件下载实习就业最新通知学生实习国际实习学生就业学生创业职业发展政策法规文档下载合作科研对外合作合作培训科研管理智慧人才校园服务校园安全院医务室网络管理院图书馆户籍管理交通指南浙大校历学生手册设施服务公文处理固定资产联系我们科研经费公开继续教育远程教育师资团队教学管理课程资源常见问题联系我们学子风采砥砺奋进的五年您现在的位置网站首页重要通知内容详细学费催缴通知作者来源超级管理员发布时间20170227点击次数1342512016级春季入学第二年学费尚未缴纳学生名单如下方明李成翰范士董黄峣12015级春季入学第二年学费尚未缴纳学生名单如下谢毅李海森郑云应帮诺刘薇2需要咨询学费问题请与学院财务室联系联系电话057427830868版权所有浙江大学软件学院2012浙备05074421号";
        String[] keywords = {"学费","缴纳"};
        String resStr = StringUtils.contentToDiscription(content, Arrays.asList(keywords),80);
        System.out.println(resStr);
    }
}
