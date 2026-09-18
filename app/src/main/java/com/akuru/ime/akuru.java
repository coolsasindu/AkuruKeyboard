package com.akuru.ime;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import java.util.HashMap;
import java.util.Map;

public class akuru extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private StringBuilder buffer;
    private Map<String, String> phoneticMap;

    @Override
    public void onCreate() {
        super.onCreate();
        buffer = new StringBuilder();
        initializePhoneticMap();
    }

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        keyboard = new Keyboard(this, R.xml.qwerty);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        buffer.setLength(0);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        // Handle Backspace / Delete
        if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleDelete(ic);
            return;
        }

        // Handle Spacebar
        if (primaryCode == 32) {
            buffer.setLength(0);
            ic.commitText(" ", 1);
            return;
        }

        char c = (char) primaryCode;
        buffer.append(c);

        String input = buffer.toString();
        String sinhalaChar = findMatch(input);

        if (sinhalaChar != null) {
            // Remove previous buffered character draft before committing translated Sinhala text
            if (buffer.length() > 1) {
                ic.deleteSurroundingText(buffer.length() - 1, 0);
            }
            ic.commitText(sinhalaChar, 1);
            buffer.setLength(0);
        } else if (!canExtend(input)) {
            // If prefix sequence cannot match any Sinhala key, output raw character
            ic.commitText(String.valueOf(c), 1);
            buffer.setLength(0);
        }
    }

    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic != null) {
            ic.commitText(text, 1);
            buffer.setLength(0);
        }
    }

    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}

    private void initializePhoneticMap() {
        phoneticMap = new HashMap<>();

        // Vowels
        phoneticMap.put("a", "අ");
        phoneticMap.put("aa", "ආ");
        phoneticMap.put("q", "ඇ");
        phoneticMap.put("qq", "ඈ");
        phoneticMap.put("qa", "ඈ");
        phoneticMap.put("i", "ඉ");
        phoneticMap.put("ii", "ඊ");
        phoneticMap.put("u", "උ");
        phoneticMap.put("uu", "ඌ");
        phoneticMap.put("r", "ඍ");
        phoneticMap.put("e", "එ");
        phoneticMap.put("ee", "ඒ");
        phoneticMap.put("o", "ඔ");
        phoneticMap.put("oo", "ඕ");

        // Consonants
        phoneticMap.put("ka", "ක");
        phoneticMap.put("ga", "ග");
        phoneticMap.put("cha", "ච");
        phoneticMap.put("ja", "ජ");
        phoneticMap.put("ta", "ට");
        phoneticMap.put("da", "ඩ");
        phoneticMap.put("tha", "ත");
        phoneticMap.put("dha", "ද");
        phoneticMap.put("na", "න");
        phoneticMap.put("pa", "ප");
        phoneticMap.put("ba", "බ");
        phoneticMap.put("ma", "ම");
        phoneticMap.put("ya", "ය");
        phoneticMap.put("ra", "ර");
        phoneticMap.put("la", "ල");
        phoneticMap.put("wa", "ව");
        phoneticMap.put("va", "ව");
        phoneticMap.put("sa", "ස");
        phoneticMap.put("sha", "ශ");
        phoneticMap.put("ha", "හ");
        phoneticMap.put("fa", "ෆ");

        // Consonants + Vowels
        phoneticMap.put("kq", "කැ");
        phoneticMap.put("kqq", "කෑ");
        phoneticMap.put("kqa", "කෑ");
        phoneticMap.put("ki", "කි");
        phoneticMap.put("kii", "කී");
        phoneticMap.put("ku", "කු");
        phoneticMap.put("kuu", "කූ");
        phoneticMap.put("ke", "කෙ");
        phoneticMap.put("kee", "කේ");
        phoneticMap.put("ko", "කො");
        phoneticMap.put("koo", "කෝ");

        phoneticMap.put("mq", "මැ");
        phoneticMap.put("mqq", "මෑ");
        phoneticMap.put("mqa", "මෑ");
        phoneticMap.put("mi", "මි");
        phoneticMap.put("mii", "මී");
        phoneticMap.put("mu", "මු");
        phoneticMap.put("muu", "මූ");
        phoneticMap.put("me", "මෙ");
        phoneticMap.put("mee", "මේ");
        phoneticMap.put("mo", "මෝ");
        phoneticMap.put("moo", "මෞ");

        phoneticMap.put("gq", "ගැ");
        phoneticMap.put("gqq", "ගෑ");
        phoneticMap.put("gi", "ගි");
        phoneticMap.put("gii", "ගී");
        phoneticMap.put("gu", "ගු");
        phoneticMap.put("guu", "ගූ");

        phoneticMap.put("nq", "නැ");
        phoneticMap.put("nqq", "නෑ");
        phoneticMap.put("ni", "නි");
        phoneticMap.put("nii", "නී");
        phoneticMap.put("nu", "නු");
        phoneticMap.put("nuu", "නූ");

        phoneticMap.put("pq", "පැ");
        phoneticMap.put("pqq", "පෑ");
        phoneticMap.put("pi", "පි");
        phoneticMap.put("pii", "පී");

        phoneticMap.put("bq", "බැ");
        phoneticMap.put("bqq", "බෑ");
        phoneticMap.put("bi", "බි");
        phoneticMap.put("bii", "බී");

        phoneticMap.put("lq", "ලැ");
        phoneticMap.put("lqq", "ලෑ");
        phoneticMap.put("li", "ලි");
        phoneticMap.put("lii", "ලී");

        phoneticMap.put("wq", "වැ");
        phoneticMap.put("wqq", "වෑ");
        phoneticMap.put("wi", "වි");
        phoneticMap.put("wii", "වී");

        phoneticMap.put("sq", "සැ");
        phoneticMap.put("sqq", "සෑ");
        phoneticMap.put("si", "සි");
        phoneticMap.put("sii", "සී");

        phoneticMap.put("hq", "හැ");
        phoneticMap.put("hqq", "හෑ");
        phoneticMap.put("hi", "හි");
        phoneticMap.put("hii", "හී");
    }

    private String findMatch(String input) {
        return phoneticMap.get(input);
    }

    private boolean canExtend(String input) {
        for (String key : phoneticMap.keySet()) {
            if (key.startsWith(input)) {
                return true;
            }
        }
        return false;
    }

    private void handleDelete(InputConnection ic) {
        if (buffer.length() > 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        } else {
            ic.deleteSurroundingText(1, 0);
        }
    }
}
