package com.mindskip.xzs.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum ExamPaperMethodEnum {

    Order(1, "顺序出题"),
    Random(2, "随机出题"),
    Free(3, "自选");

    int code;
    String name;

    ExamPaperMethodEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    private static final Map<Integer, ExamPaperMethodEnum> keyMap = new HashMap<>();

    static {
        for (ExamPaperMethodEnum item : ExamPaperMethodEnum.values()) {
            keyMap.put(item.getCode(), item);
        }
    }

    public static ExamPaperMethodEnum fromCode(Integer code) {
        return keyMap.get(code);
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
