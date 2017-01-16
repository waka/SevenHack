package io.github.waka.sevenhack.views.components;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.github.waka.sevenhack.R;
import io.github.waka.sevenhack.data.entities.Episode;
import io.github.waka.sevenhack.data.models.ShowNote;

public class RelevantLinksView extends LinearLayout {
    public RelevantLinksView(Context context) {
        super(context);
        setup();
    }

    public RelevantLinksView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public RelevantLinksView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        setOrientation(LinearLayout.VERTICAL);
    }

    public void addLinkView(Episode episode) {
        List<ShowNote> linkList = ShowNote.parseToLinkList(episode.description);

        for (final ShowNote link : linkList) {
            View row = View.inflate(getContext(), R.layout.item_relevant_link, null);

            TextView linkTitleView = (TextView) row.findViewById(R.id.link_title);
            SpannableString spannableString = new SpannableString(link.title);
            spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
            linkTitleView.setText(spannableString);
            linkTitleView.setOnClickListener(v -> {
                Intent intent = new Intent(
                        "android.intent.action.VIEW",
                        Uri.parse(link.url));
                getContext().startActivity(intent);
            });

            addView(row);
        }
    }
}
