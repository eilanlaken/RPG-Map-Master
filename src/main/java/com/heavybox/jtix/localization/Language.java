package com.heavybox.jtix.localization;

public enum Language {

    // Major languages
    ENGLISH            ("en",    "English", "Latin",               WritingSystem.LTR),
    HEBREW             ("he",    "עברית",   "Hebrew",              WritingSystem.RTL),
    ARABIC             ("ar",    "العربية", "Arabic",              WritingSystem.RTL),
    CHINESE_SIMPLIFIED ("zh_CN", "简体中文",  "Simplified Chinese",  WritingSystem.TTB),
    CHINESE_TRADITIONAL("zh_TW", "繁體中文",  "Traditional Chinese", WritingSystem.TTB),
    JAPANESE           ("ja",    "日本語",    "Japanese",            WritingSystem.TTB),
    FRENCH             ("fr",    "Français", "Latin",               WritingSystem.LTR),
    SPANISH            ("es",    "Español",  "Latin",               WritingSystem.LTR),

    // Other widely used languages
    GERMAN    ("de", "Deutsch",   "Latin",      WritingSystem.LTR),
    RUSSIAN   ("ru", "Русский",   "Cyrillic",   WritingSystem.LTR),
    PORTUGUESE("pt", "Português", "Latin",      WritingSystem.LTR),
    ITALIAN   ("it", "Italiano",  "Latin",      WritingSystem.LTR),
    HINDI     ("hi", "हिन्दी",       "Devanagari", WritingSystem.LTR),
    BENGALI   ("bn", "বাংলা",       "Bengali",    WritingSystem.LTR),
    KOREAN    ("ko", "한국어",      "Hangul",     WritingSystem.TTB),

    // Languages with distinct scripts
    GREEK     ("el", "Ελληνικά",   "Greek",     WritingSystem.LTR),
    THAI      ("th", "ไทย",        "Thai",      WritingSystem.LTR),
    VIETNAMESE("vi", "Tiếng Việt", "Latin",     WritingSystem.LTR),
    TAMIL     ("ta", "தமிழ்",      "Tamil",     WritingSystem.LTR),
    TELUGU    ("te", "తెలుగు",      "Telugu",    WritingSystem.LTR),
    MALAYALAM ("ml", "മലയാളം",   "Malayalam", WritingSystem.LTR),
    URDU      ("ur", "اردو",       "Arabic",    WritingSystem.RTL),
    PERSIAN   ("fa", "فارسی",      "Arabic",    WritingSystem.RTL),

    // Less commonly included languages
    SWAHILI   ("sw", "Kiswahili",        "Latin", WritingSystem.LTR),
    ZULU      ("zu", "isiZulu",          "Latin", WritingSystem.LTR),
    XHOSA     ("xh", "isiXhosa",         "Latin", WritingSystem.LTR),
    TURKISH   ("tr", "Türkçe",           "Latin", WritingSystem.LTR),
    POLISH    ("pl", "Polski",           "Latin", WritingSystem.LTR),
    DUTCH     ("nl", "Nederlands",       "Latin", WritingSystem.LTR),
    MALAY     ("ms", "Bahasa Melayu",    "Latin", WritingSystem.LTR),
    INDONESIAN("id", "Bahasa Indonesia", "Latin", WritingSystem.LTR),

    // Native American and Indigenous languages
    HAWAIIAN("haw", "ʻŌlelo Hawaiʻi", "Latin", WritingSystem.LTR),
    QUECHUA ("qu",  "Runa Simi",      "Latin", WritingSystem.LTR),
    NAHUATL ("nah", "Nāhuatl",        "Latin", WritingSystem.LTR),

    // African languages
    HAUSA  ("ha", "Hausa",    "Latin", WritingSystem.LTR),
    AMHARIC("am", "አማርኛ",    "Ge'ez", WritingSystem.LTR),
    SOMALI ("so", "Soomaali", "Latin", WritingSystem.LTR),

    // Indigenous Asian scripts
    KHMER  ("km", "ខ្មែរ",   "Khmer",   WritingSystem.LTR),
    BURMESE("my", "မြန်မာ", "Burmese",  WritingSystem.LTR),
    LAO    ("lo", "ລາວ",   "Lao",     WritingSystem.LTR),

    // Scandinavian languages
    DANISH   ("da", "Dansk",   "Latin", WritingSystem.LTR),
    NORWEGIAN("no", "Norsk",   "Latin", WritingSystem.LTR),
    SWEDISH  ("sv", "Svenska", "Latin", WritingSystem.LTR),

    // Celtic languages
    WELSH          ("cy", "Cymraeg",  "Latin", WritingSystem.LTR),
    IRISH          ("ga", "Gaeilge",  "Latin", WritingSystem.LTR),
    SCOTTISH_GAELIC("gd", "Gàidhlig", "Latin", WritingSystem.LTR);

    public final String shorthand;
    public final String nativeName;
    public final String script;
    public final WritingSystem writingSystem;

    Language(String shorthand, String nativeName, String script, WritingSystem writingSystem) {
        this.shorthand = shorthand;
        this.nativeName = nativeName;
        this.script = script;
        this.writingSystem = writingSystem;
    }

    public enum WritingSystem {
        LTR, // Left-to-Right
        RTL, // Right-to-Left
        TTB, // Top-to-Bottom
        BTT; // Bottom-to-Top (rare)
    }

}

