package it.maucel89.dbclient.code.area;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mauro Celani
 */
public class SQLCodeArea extends CodeArea {

	private static final String[] KEYWORDS = new String[] {
		"select", "from", "where", "join", "on",
		"as", "order", "by", "limit"
	};

	private static final String KEYWORD_PATTERN =
		"\\b(" + String.join("|", KEYWORDS) + ")\\b";

	private static final Pattern PATTERN = Pattern.compile(
		"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
	);

	public SQLCodeArea() {

		super();

		setParagraphGraphicFactory(
			LineNumberFactory.get(this));

		// recompute the syntax highlighting 500 ms after user stops editing area
		Subscription cleanupWhenNoLongerNeedIt = this

			// plain changes = ignore style changes that are emitted when syntax
			// highlighting is reapplied multi plain changes = save computation
			// by not rerunning the code multiple times when making multiple
			// changes (e.g. renaming a method at multiple parts in file)
			.multiPlainChanges()

			// do not emit an event until 500 ms have passed since the last
			// emission of previous stream
			.successionEnds(Duration.ofMillis(500))

			// run the following code block when previous stream emits an event
			.subscribe(ignore -> this.setStyleSpans(
				0, computeHighlighting(this.getText())));

		// TODO
		// when no longer need syntax highlighting and wish to clean up memory
		// leaks run: `cleanupWhenNoLongerNeedIt.unsubscribe();`

	}

	public void initCode(String sql) {
		replaceText(0, 0, sql);
	}

	private static StyleSpans<Collection<String>> computeHighlighting(
		String text) {

		Matcher matcher = PATTERN.matcher(text);

		int lastKwEnd = 0;

		StyleSpansBuilder<Collection<String>> spansBuilder =
			new StyleSpansBuilder<>();

		while(matcher.find()) {
			String styleClass =
				matcher.group("KEYWORD") != null ? "keyword" :
//				matcher.group("PAREN") != null ? "paren" :
//				matcher.group("BRACE") != null ? "brace" :
//				matcher.group("BRACKET") != null ? "bracket" :
//				matcher.group("SEMICOLON") != null ? "semicolon" :
//				matcher.group("STRING") != null ? "string" :
//				matcher.group("COMMENT") != null ? "comment" :
				null; /* never happens */ assert styleClass != null;

			spansBuilder.add(
				Collections.emptyList(), matcher.start() - lastKwEnd);

			spansBuilder.add(
				Collections.singleton(styleClass),
				matcher.end() - matcher.start());

			lastKwEnd = matcher.end();
		}

		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

		return spansBuilder.create();
	}

}
