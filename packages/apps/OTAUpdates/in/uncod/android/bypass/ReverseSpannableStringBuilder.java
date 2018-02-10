package in.uncod.android.bypass;

import android.text.SpannableStringBuilder;

public class ReverseSpannableStringBuilder extends SpannableStringBuilder {
    public <T> T[] getSpans(int queryStart, int queryEnd, Class<T> kind) {
        Object[] ret = super.getSpans(queryStart, queryEnd, kind);
        reverse(ret);
        return ret;
    }

    private static void reverse(Object[] arr) {
        if (arr != null) {
            int j = arr.length - 1;
            for (int i = 0; j > i; i++) {
                Object tmp = arr[j];
                arr[j] = arr[i];
                arr[i] = tmp;
                j--;
            }
        }
    }
}
