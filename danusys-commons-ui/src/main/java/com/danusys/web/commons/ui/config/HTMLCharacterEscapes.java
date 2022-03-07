package com.danusys.web.commons.ui.config;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/02/17
 * Time : 15:12
 */
public class HTMLCharacterEscapes extends CharacterEscapes {
    private final int[] asciiEscapes;

    private final CharSequenceTranslator translator;

    public HTMLCharacterEscapes() {
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;

        // TODO : json data 처리 중 문제 발생하여 추가 처리 필요(임시 적 조치)

//        asciiEscapes['"'] = CharacterEscapes.ESCAPE_CUSTOM;
//        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;

        Map<CharSequence, CharSequence> charSequenceMap = new HashMap<>();
        charSequenceMap.put("(", "&#40;");
        charSequenceMap.put(")", "&#41;");

        Map<CharSequence, CharSequence> lookupMap = Collections.unmodifiableMap(charSequenceMap);

        translator = new AggregateTranslator(new LookupTranslator(EntityArrays.BASIC_ESCAPE),
                new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE),
                new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE),
                new LookupTranslator(lookupMap));
    }
    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
        return new SerializedString(translator.translate(Character.toString((char) ch)));
    }
}
