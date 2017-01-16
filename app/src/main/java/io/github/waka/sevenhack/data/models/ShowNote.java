package io.github.waka.sevenhack.data.models;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowNote {

    public final String title;
    public final String url;

    private ShowNote(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public static List<ShowNote> parseToLinkList(String source) {
        List<ShowNote> linkList = new ArrayList<>();
        if (TextUtils.isEmpty(source)) {
            return linkList;
        }
        Pattern pattern = Pattern.compile("<a.*?href=\"(.*?)\".*?>(.*?)</a>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(source);

        while (matcher.find()) {
            String href = matcher.group(1).replaceAll("¥¥s", "");
            String text = matcher.group(2).replaceAll("¥¥s", "");
            linkList.add(new ShowNote(text, href));
        }

        if (linkList.size() > 0) {
            return linkList;
        } else {
            return backportParseToLinkList(source);
        }
    }

    private static List<ShowNote> backportParseToLinkList(String source) {
        List<ShowNote> linkList = new ArrayList<>();
        Pattern pattern = Pattern.compile(
                "(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(source);

        while (matcher.find()) {
            String href = matcher.group().replaceAll("¥¥s", "");
            linkList.add(new ShowNote(href, href));
        }
        return linkList;
    }
}
