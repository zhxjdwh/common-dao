package cn.com.zhxj.common.dao.util;

public class ThreadUtils {

    public static void printTrack(){

        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        if(st==null){
            System.out.println("无堆栈...");
            return;
        }
        StringBuffer sbf =new StringBuffer("thread id: ["+Thread.currentThread().getId()+"]"+System.getProperty("line.separator"));
        for(StackTraceElement e:st){
            if(sbf.length()>0){
                sbf.append(" <- ");
                sbf.append(System.getProperty("line.separator"));
            }
            sbf.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    ,e.getClassName()
                    ,e.getMethodName()
                    ,e.getLineNumber()));
        }
        System.out.println(sbf.toString());
        System.out.println("------------------------------------------------------------------------------------------");
    }
}
