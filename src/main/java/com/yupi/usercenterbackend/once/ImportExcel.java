package com.yupi.usercenterbackend.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * 导入excel
 */
@Slf4j
public class ImportExcel {

    /**
     * 读取方法
     * @param args
     */
    public static void main(String[] args) {
        String fileName = "testExcel.xlsx"; // TODO 更改成绝对路径，文件放在resource下
        // 方法1：使用监听器读取数据
        readByListener(fileName);
        // 方法2：开启同步读，不使用监听器
        synchronousRead(fileName);
    }

    private static void readByListener(String fileName) {
        EasyExcel.read(fileName, PPUserInfo.class, new TableListener()).sheet().doRead();
    }

    private static void synchronousRead(String fileName){
        List<PPUserInfo> userInfoList =  EasyExcel.read(fileName).head(PPUserInfo.class).sheet().doReadSync();
        for (PPUserInfo ppUserInfo : userInfoList) {
            System.out.println(ppUserInfo.toString());
        }

    }

}
