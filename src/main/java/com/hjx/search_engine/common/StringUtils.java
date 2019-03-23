package com.hjx.search_engine.common;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static JiebaSegmenter segmenter = new JiebaSegmenter();

    public static String concatWith(List<String> strList, String sep) {
        StringBuilder sb = new StringBuilder();
        for(String str:strList) {
            sb.append(str+sep);
        }
        return sb.substring(0,sb.length()-sep.length());
    }

    public static List<String> jiebaSegment(String keyword, JiebaSegmenter.SegMode mode) {
        List<SegToken> tokens = segmenter.process(keyword,mode);
        List<String> keywords = new ArrayList<>();
        for(SegToken token:tokens) {
            keywords.add(token.word);
        }
        return keywords;
    }

    public static String highlightStr(String str,List<String> keywordList) {
        for(String keyword:keywordList) {
            if(str.indexOf(keyword)!=-1) {
                str = str.replace(keyword,"<font color='red'>"+keyword+"</font>");
            }
        }
        return str;
    }

    public static int getBestStartPos(List<Integer> keywordPos,final int span) {
        //获得一定间距span内所能获得的最多的关键字个数
        int startIndex = 0, endIndex = 0;
        for(int i = 1; i < keywordPos.size(); i++) {
            if(keywordPos.get(i)-keywordPos.get(startIndex)>span) break;
            endIndex = i;
        }
        for(int startI = 1; startI < keywordPos.size(); startI++) {
            int endI = startI+(endIndex-startIndex)+1;
            for(;endI < keywordPos.size(); endI++) {
                if(keywordPos.get(endI)-keywordPos.get(startI)>span) break;//如果两个关键点之间的间距大于span，则跳出当前循环
                if(endI-startI > endIndex-startIndex) {//如果两个关键点之间的关键点个数大于已知的最多的关键点个数
                    startIndex = startI;
                    endIndex = endI;
                }
            }
        }
        return startIndex;
    }

    public static String contentToDiscription(String content,List<String> keywordList,final int span) {
        List<Integer> keywordPos = new ArrayList<>();
        Set<String> keywordSet = new HashSet<>();
        keywordSet.addAll(keywordList);
        List<SegToken> tokens = segmenter.process(content,JiebaSegmenter.SegMode.INDEX);
        for(SegToken token:tokens) {
            //只有这个分词包含在keywordSet中，才能将其位置加入到keywordPos中
            if(keywordSet.contains(token.word)) keywordPos.add(token.startOffset);
        }
        if(keywordPos.size()==0) return "";
        int startPos = getBestStartPos(keywordPos,span);
        String describeSubstr = content.substring(keywordPos.get(startPos),
                keywordPos.get(startPos)+span>content.length()?content.length():keywordPos.get(startPos)+span);
        String highlightSubstr = highlightStr(describeSubstr,keywordList);
        return highlightSubstr;
    }

    public static int getSepIndexOfTarget(String target,String sep,int count) {
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

    public static String getElementsWith(String target,int start,int num,String sep) {
        int startPos = getSepIndexOfTarget(target,sep,start);
        int endPos = getSepIndexOfTarget(target,sep,start+num);
        if(startPos>=0&&startPos<target.length()&&endPos>=0&&endPos<=target.length()&&startPos<endPos) {
            if(startPos==0) return target.substring(startPos,endPos);
            else return target.substring(startPos+sep.length(),endPos);
        }
        return null;
    }

    public static void main(String[] args) {
//        List<String> strList = new ArrayList<>();
//        strList.add("hello");
//        strList.add("world");
//        strList.add("nice");
//        strList.add("ok");
//        String lastStr = concatWith(strList,",");
//        System.out.println(lastStr);

        String title = "学费催缴通知_浙江大学软件学院";
        List<String> keywordList = new ArrayList<>();
        keywordList.add("学费");
        keywordList.add("软件学院");
        System.out.println(highlightStr(title,keywordList));;
    }
}
