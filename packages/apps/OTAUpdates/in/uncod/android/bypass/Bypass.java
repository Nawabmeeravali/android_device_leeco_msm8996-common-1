package in.uncod.android.bypass;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.text.style.LeadingMarginSpan.Standard;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import com.google.android.gms.cast.TextTrackStyle;
import com.google.android.gms.plus.PlusShare;
import in.uncod.android.bypass.Element.Type;
import in.uncod.android.bypass.style.HorizontalLineSpan;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Bypass {
    private static /* synthetic */ int[] $SWITCH_TABLE$in$uncod$android$bypass$Element$Type;
    private final int mBlockQuoteIndent;
    private final int mCodeBlockIndent;
    private final int mHruleSize;
    private final int mHruleTopBottomPadding;
    private final int mListItemIndent;
    private final Options mOptions;
    private final Map<Element, Integer> mOrderedListNumber;

    public interface ImageGetter {
        Drawable getDrawable(String str);
    }

    public static final class Options {
        private int mBlockQuoteColor = -16776961;
        private float mBlockQuoteIndentSize = 10.0f;
        private int mBlockQuoteIndentUnit = 1;
        private float mCodeBlockIndentSize = 10.0f;
        private int mCodeBlockIndentUnit = 1;
        private float[] mHeaderSizes = new float[]{1.5f, 1.4f, 1.3f, 1.2f, 1.1f, TextTrackStyle.DEFAULT_FONT_SCALE};
        private int mHruleColor = -7829368;
        private float mHruleSize = TextTrackStyle.DEFAULT_FONT_SCALE;
        private int mHruleUnit = 1;
        private float mListItemIndentSize = 10.0f;
        private int mListItemIndentUnit = 1;
        private String mUnorderedListItem = "•";

        public Options setHeaderSizes(float[] headerSizes) {
            if (headerSizes == null) {
                throw new IllegalArgumentException("headerSizes must not be null");
            } else if (headerSizes.length != 6) {
                throw new IllegalArgumentException("headerSizes must have 6 elements (h1 through h6)");
            } else {
                this.mHeaderSizes = headerSizes;
                return this;
            }
        }

        public Options setUnorderedListItem(String unorderedListItem) {
            this.mUnorderedListItem = unorderedListItem;
            return this;
        }

        public Options setListItemIndentSize(int unit, float size) {
            this.mListItemIndentUnit = unit;
            this.mListItemIndentSize = size;
            return this;
        }

        public Options setBlockQuoteColor(int color) {
            this.mBlockQuoteColor = color;
            return this;
        }

        public Options setBlockQuoteIndentSize(int unit, float size) {
            this.mBlockQuoteIndentUnit = unit;
            this.mBlockQuoteIndentSize = size;
            return this;
        }

        public Options setCodeBlockIndentSize(int unit, float size) {
            this.mCodeBlockIndentUnit = unit;
            this.mCodeBlockIndentSize = size;
            return this;
        }

        public Options setHruleColor(int color) {
            this.mHruleColor = color;
            return this;
        }

        public Options setHruleSize(int unit, float size) {
            this.mHruleUnit = unit;
            this.mHruleSize = size;
            return this;
        }
    }

    private native Document processMarkdown(String str);

    static /* synthetic */ int[] $SWITCH_TABLE$in$uncod$android$bypass$Element$Type() {
        int[] iArr = $SWITCH_TABLE$in$uncod$android$bypass$Element$Type;
        if (iArr == null) {
            iArr = new int[Type.values().length];
            try {
                iArr[Type.AUTOLINK.ordinal()] = 12;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Type.BLOCK_CODE.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Type.BLOCK_HTML.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Type.BLOCK_QUOTE.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Type.CODE_SPAN.ordinal()] = 13;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[Type.DOUBLE_EMPHASIS.ordinal()] = 14;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[Type.EMPHASIS.ordinal()] = 15;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[Type.HEADER.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[Type.HRULE.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[Type.IMAGE.ordinal()] = 16;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[Type.LINEBREAK.ordinal()] = 17;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[Type.LINK.ordinal()] = 18;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[Type.LIST.ordinal()] = 6;
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[Type.LIST_ITEM.ordinal()] = 7;
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[Type.PARAGRAPH.ordinal()] = 8;
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[Type.RAW_HTML_TAG.ordinal()] = 19;
            } catch (NoSuchFieldError e16) {
            }
            try {
                iArr[Type.STRIKETHROUGH.ordinal()] = 22;
            } catch (NoSuchFieldError e17) {
            }
            try {
                iArr[Type.TABLE.ordinal()] = 9;
            } catch (NoSuchFieldError e18) {
            }
            try {
                iArr[Type.TABLE_CELL.ordinal()] = 10;
            } catch (NoSuchFieldError e19) {
            }
            try {
                iArr[Type.TABLE_ROW.ordinal()] = 11;
            } catch (NoSuchFieldError e20) {
            }
            try {
                iArr[Type.TEXT.ordinal()] = 21;
            } catch (NoSuchFieldError e21) {
            }
            try {
                iArr[Type.TRIPLE_EMPHASIS.ordinal()] = 20;
            } catch (NoSuchFieldError e22) {
            }
            $SWITCH_TABLE$in$uncod$android$bypass$Element$Type = iArr;
        }
        return iArr;
    }

    static {
        System.loadLibrary("bypass");
    }

    @Deprecated
    public Bypass() {
        this.mOrderedListNumber = new ConcurrentHashMap();
        this.mOptions = new Options();
        this.mListItemIndent = 20;
        this.mBlockQuoteIndent = 10;
        this.mCodeBlockIndent = 10;
        this.mHruleSize = 2;
        this.mHruleTopBottomPadding = 20;
    }

    public Bypass(Context context) {
        this(context, new Options());
    }

    public Bypass(Context context, Options options) {
        this.mOrderedListNumber = new ConcurrentHashMap();
        this.mOptions = options;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        this.mListItemIndent = (int) TypedValue.applyDimension(this.mOptions.mListItemIndentUnit, this.mOptions.mListItemIndentSize, dm);
        this.mBlockQuoteIndent = (int) TypedValue.applyDimension(this.mOptions.mBlockQuoteIndentUnit, this.mOptions.mBlockQuoteIndentSize, dm);
        this.mCodeBlockIndent = (int) TypedValue.applyDimension(this.mOptions.mCodeBlockIndentUnit, this.mOptions.mCodeBlockIndentSize, dm);
        this.mHruleSize = (int) TypedValue.applyDimension(this.mOptions.mHruleUnit, this.mOptions.mHruleSize, dm);
        this.mHruleTopBottomPadding = ((int) dm.density) * 10;
    }

    public CharSequence markdownToSpannable(String markdown) {
        return markdownToSpannable(markdown, null);
    }

    public CharSequence markdownToSpannable(String markdown, ImageGetter imageGetter) {
        Document document = processMarkdown(markdown);
        CharSequence[] spans = new CharSequence[document.getElementCount()];
        for (int i = 0; i < document.getElementCount(); i++) {
            spans[i] = recurseElement(document.getElement(i), imageGetter);
        }
        return TextUtils.concat(spans);
    }

    private CharSequence recurseElement(Element element, ImageGetter imageGetter) {
        Type type = element.getType();
        boolean isOrderedList = false;
        if (type == Type.LIST) {
            String flagsStr = element.getAttribute("flags");
            if (flagsStr != null) {
                isOrderedList = (Integer.parseInt(flagsStr) & 1) != 0;
                if (isOrderedList) {
                    this.mOrderedListNumber.put(element, Integer.valueOf(1));
                }
            }
        }
        CharSequence[] spans = new CharSequence[element.size()];
        for (int i = 0; i < element.size(); i++) {
            spans[i] = recurseElement(element.children[i], imageGetter);
        }
        if (isOrderedList) {
            this.mOrderedListNumber.remove(this);
        }
        CharSequence concat = TextUtils.concat(spans);
        SpannableStringBuilder builder = new ReverseSpannableStringBuilder();
        String text = element.getText();
        if (!(element.size() != 0 || element.getParent() == null || element.getParent().getType() == Type.BLOCK_CODE)) {
            text = text.replace('\n', ' ');
        }
        Drawable imageDrawable = null;
        if (!(type != Type.IMAGE || imageGetter == null || TextUtils.isEmpty(element.getAttribute("link")))) {
            imageDrawable = imageGetter.getDrawable(element.getAttribute("link"));
        }
        switch ($SWITCH_TABLE$in$uncod$android$bypass$Element$Type()[type.ordinal()]) {
            case 5:
                builder.append("-");
                break;
            case 6:
                if (element.getParent() != null && element.getParent().getType() == Type.LIST_ITEM) {
                    builder.append("\n");
                    break;
                }
            case 7:
                builder.append(" ");
                if (this.mOrderedListNumber.containsKey(element.getParent())) {
                    int number = ((Integer) this.mOrderedListNumber.get(element.getParent())).intValue();
                    builder.append(Integer.toString(number) + ".");
                    this.mOrderedListNumber.put(element.getParent(), Integer.valueOf(number + 1));
                } else {
                    builder.append(this.mOptions.mUnorderedListItem);
                }
                builder.append("  ");
                break;
            case 12:
                builder.append(element.getAttribute("link"));
                break;
            case 16:
                if (imageDrawable != null) {
                    builder.append("￼");
                    break;
                }
                String show = element.getAttribute("alt");
                if (TextUtils.isEmpty(show)) {
                    show = element.getAttribute(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE);
                }
                if (!TextUtils.isEmpty(show)) {
                    builder.append("[" + show + "]");
                    break;
                }
                break;
            case 17:
                builder.append("\n");
                break;
        }
        builder.append(text);
        builder.append(concat);
        if (type == Type.LIST_ITEM) {
            if (element.size() == 0 || !element.children[element.size() - 1].isBlockElement()) {
                builder.append("\n");
            }
        } else if (element.isBlockElement() && type != Type.BLOCK_QUOTE) {
            if (type == Type.LIST) {
                if (element.getParent() == null || element.getParent().getType() != Type.LIST_ITEM) {
                    builder.append("\n");
                }
            } else if (element.getParent() == null || element.getParent().getType() != Type.LIST_ITEM) {
                builder.append("\n\n");
            } else {
                builder.append("\n");
            }
        }
        switch ($SWITCH_TABLE$in$uncod$android$bypass$Element$Type()[type.ordinal()]) {
            case 1:
                setSpan(builder, new Standard(this.mCodeBlockIndent));
                setSpan(builder, new TypefaceSpan("monospace"));
                break;
            case 2:
                setBlockSpan(builder, new Standard(this.mBlockQuoteIndent));
                setBlockSpan(builder, new QuoteSpan(this.mOptions.mBlockQuoteColor));
                setBlockSpan(builder, new Standard(this.mBlockQuoteIndent));
                setBlockSpan(builder, new StyleSpan(2));
                break;
            case 4:
                setSpan(builder, new RelativeSizeSpan(this.mOptions.mHeaderSizes[Integer.parseInt(element.getAttribute("level")) - 1]));
                setSpan(builder, new StyleSpan(1));
                break;
            case 5:
                setSpan(builder, new HorizontalLineSpan(this.mOptions.mHruleColor, this.mHruleSize, this.mHruleTopBottomPadding));
                break;
            case 6:
                setBlockSpan(builder, new Standard(this.mListItemIndent));
                break;
            case 12:
            case 18:
                String link = element.getAttribute("link");
                if (Patterns.EMAIL_ADDRESS.matcher(link).matches()) {
                    link = "mailto:" + link;
                }
                setSpan(builder, new URLSpan(link));
                break;
            case 13:
                setSpan(builder, new TypefaceSpan("monospace"));
                break;
            case 14:
                setSpan(builder, new StyleSpan(1));
                break;
            case 15:
                setSpan(builder, new StyleSpan(2));
                break;
            case 16:
                if (imageDrawable != null) {
                    setSpan(builder, new ImageSpan(imageDrawable));
                    break;
                }
                break;
            case 20:
                setSpan(builder, new StyleSpan(3));
                break;
            case 22:
                setSpan(builder, new StrikethroughSpan());
                break;
        }
        return builder;
    }

    private static void setSpan(SpannableStringBuilder builder, Object what) {
        builder.setSpan(what, 0, builder.length(), 33);
    }

    private static void setBlockSpan(SpannableStringBuilder builder, Object what) {
        builder.setSpan(what, 0, builder.length() - 1, 33);
    }
}
