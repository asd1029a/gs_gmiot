package com.danusys.web.commons.api.types;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/04/29
 * Time : 17:17
 */

@Getter
public enum BinaryType {
    BINARY_TRUE("1", Arrays.asList("흰지팡이","치마","청바지","긴바지","짧은소매","티셔츠","정장","줄무늬의상","가방","어린이","남성")),
    BINARY_FALSE("0",Arrays.asList("","","","","긴소매","","","","","","여성")),
    BINARY_EMPTY("2",Collections.EMPTY_LIST);

    private String code;
    private List<String> codeNameList;

    BinaryType(String code, List<String> codeNameList) {
        this.code = code;
        this.codeNameList = codeNameList;
    }

    public String getCode() {
        return this.code;
    }

    public static BinaryType findByCodeName(String code) {
        return Arrays.stream(BinaryType.values())
                .filter(binaryType -> binaryType.getCode().equals(code))
                .findAny().orElse(BINARY_EMPTY);
    }

    public static List<String> getBinaryCodeName(List<String> list ) {
        AtomicInteger index = new AtomicInteger();
        List<String> result = new ArrayList<>();
        list.forEach(binary -> {
                    BinaryType codeCheck = BinaryType.findByCodeName(binary);
                    String codeName = codeCheck.getCodeNameList().get(index.getAndIncrement());
                    if(codeName.isEmpty()) return;
                    result.add(codeName);
                });
        return result;
    }
    public static List<String> setBinaryLength(String hex) {
        String binary = Integer.toBinaryString(Integer.decode("0x" + hex));

        return null;
    }
 }
