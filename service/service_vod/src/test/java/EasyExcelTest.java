import com.alibaba.excel.EasyExcel;
//import com.cc.service_vod.pojo.Stu;

import java.util.ArrayList;
import java.util.List;

public class EasyExcelTest {

    public static void main(String[] args) throws Exception {
        // 写法1
        //String fileName = "D:\\11.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
       // EasyExcel.write(fileName, Stu.class).sheet("写入方法").doWrite(data());
        //指定读取路径
        String fileName = "D:\\11.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, Stu.class, new ExcelListener()).sheet().doRead();
    }

   /* private static List data() {
        List resultList = new ArrayList();
        for (int i = 0; i < 10; i++) {
            Stu stu = new Stu();
            stu.setSname("A"+i);
            stu.setSno(i);
            resultList.add(stu);
        }
        return resultList;
    }*/

}
