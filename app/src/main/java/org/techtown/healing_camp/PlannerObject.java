package org.techtown.healing_camp;

public class Object {
    static String Title;
    static String[] Memo,Result;
    public static Object object = new Object();
    private Object(){
    }
    public static Object getTitleObject(){
        return object;
    }

    //메모장 이름 받기
    public static void setTitle(String title) {
        Title = title;
    }
    //메모장 이름 건네주기
    public static String getTitle() {
        return Title;
    }

    //메모장 받기
    public static void setMemo(String[] memo){Memo = memo;}
    //메모장 건내주기
    public static String[] getMemo(){return Memo;}

    //캠핑장 받기
    public static void setResult(String[] result){Result = result;}
    //캠핑장 보내주기
    public static String[] getResult(){return Result;}
}
