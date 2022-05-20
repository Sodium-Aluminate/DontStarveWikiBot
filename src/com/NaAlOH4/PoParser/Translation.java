package com.NaAlOH4.PoParser;

import org.jetbrains.annotations.Nullable;

public class Translation {
    public String msgctxt;
    public String msgid;
    public String msgstr;
    public boolean isDST;
    public String reArrangedMsgctxt;
    private boolean isQuote;
    private @Nullable String msgctxtWithoutCharacter;

    public Translation(String code, String en, String value, boolean isDST) {
        msgctxt = code;
        msgid = en;
        msgstr = value;
        this.isDST = isDST;
        reArrange();
    }

    private static final String[] CharaCode = {
            "WALTER", "WANDA", "WARLY", "WATHGRITHR", "WAXWELL", "WEBBER", "WENDY", "WICKERBOTTOM", "WILLOW", "GENERIC", "WINONA", "WOLFGANG", "WOODIE", "WORMWOOD", "WORTOX", "WURT", "WX78",
            "WAGSTAFF", "WALANI", "WARBUCKS", "WHEELER", "WILBA", "WILBUR", "WOODLEGS"

    };
    private static final String STRINGS_CHARACTERS_ = "STRINGS.CHARACTERS.";

    private void reArrange() { // STRINGS.CHARACTERS.GENERIC.BALABALA.FOOBAR
        isQuote = msgctxt.startsWith(STRINGS_CHARACTERS_);

        if (!isQuote) {
            reArrangedMsgctxt = msgctxt;
            return;
        }

        String[] s1 = msgctxt.substring(STRINGS_CHARACTERS_.length()).split("\\.", 2); // "GENERIC", "BALABALA.FOOBAR"

        CheckCharacterCorrect:
        {
            for (String c : CharaCode) {
                if (c.equals(s1[0])) break CheckCharacterCorrect;
            }
            throw new WrongCharaCodeException("\""+msgctxt+"\" is start with "+STRINGS_CHARACTERS_+", but "+s1[0]+" is not a character code.");
        }

        reArrangedMsgctxt =  STRINGS_CHARACTERS_ + s1[1] + "." + s1[0];

        msgctxtWithoutCharacter = STRINGS_CHARACTERS_ + s1[1];
    }

    /**
     * 判断另一个translation与自身是否 **不是** 来源于相同的quote
     * 返回 true 表示不是相同的 quote
     * 返回 false 表示其 msgctxt 除了名字之外与本身一模一样。
    **/
    public boolean isNotSameQuote(Translation another) {
        if (!(isQuote && another.isQuote)) return true;

        return !msgctxtWithoutCharacter.equals(another.msgctxtWithoutCharacter);
    }

    @Override
    public String toString() {
        return "Translation{" +
                "msgctxt='" + msgctxt + '\'' +
                ", msgid='" + msgid + '\'' +
                ", msgstr='" + msgstr + '\'' +
                ", isDST=" + isDST +
                ", reArrangedMsgctxt='" + reArrangedMsgctxt + '\'' +
                '}';
    }
}
