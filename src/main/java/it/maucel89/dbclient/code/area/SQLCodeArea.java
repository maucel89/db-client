package it.maucel89.dbclient.code.area;

import com.jfoenix.controls.JFXAutoCompletePopup;
import it.maucel89.dbclient.schema.Column;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Mauro Celani
 */
public class SQLCodeArea extends CodeArea {

	private static final String[] KEYWORDS = new String[] {
		"SELECT", "FROM", "WHERE", "JOIN", "ON",
		"AS", "ORDER", "BY", "LIMIT"
	};

	private static final String KEYWORD_PATTERN =
		"\\b(" + String.join("|", KEYWORDS) + ")\\b";

	private static final Pattern PATTERN = Pattern.compile(
		"(?i)(?<KEYWORD>" + KEYWORD_PATTERN + ")"
	);

	private JFXAutoCompletePopup<String> autoCompletePopup =
		new JFXAutoCompletePopup<>();

	public SQLCodeArea() {

		super();

		setParagraphGraphicFactory(
			LineNumberFactory.get(this));

		// recompute the syntax highlighting 500 ms after user stops editing area
		Subscription cleanupWhenNoLongerNeedIt =

			// plain changes = ignore style changes that are emitted when syntax
			// highlighting is reapplied multi plain changes = save computation
			// by not rerunning the code multiple times when making multiple
			// changes (e.g. renaming a method at multiple parts in file)
			multiPlainChanges()

			// do not emit an event until 500 ms have passed since the last
			// emission of previous stream
			.successionEnds(Duration.ofMillis(500))

			// run the following code block when previous stream emits an event
			.subscribe(ignore -> setStyleSpans(
				0, computeHighlighting(getText())));

		// TODO
		// when no longer need syntax highlighting and wish to clean up memory
		// leaks run: `cleanupWhenNoLongerNeedIt.unsubscribe();`


		// AUTO COMPLETE
		autoCompletePopup.getSuggestions().addAll(KEYWORDS);

		autoCompletePopup.setSelectionHandler(event -> {
			String selAutoCompEle = event.getObject();

			int caretPos = getCaretPosition();
			int wordStart;

			for (
				wordStart = 1;
				selAutoCompEle.toLowerCase().contains(
					getText(caretPos - wordStart, caretPos).toLowerCase()) &&
				caretPos - wordStart > 0;
				wordStart++);

			deleteText(caretPos - wordStart, caretPos);
			insertText(caretPos - wordStart, selAutoCompEle);
		});

		textProperty().addListener(observable -> {

			autoCompletePopup.filter(
				string -> string.toLowerCase().contains(
					getText().toLowerCase()));

			if (autoCompletePopup.getFilteredSuggestions().isEmpty() ||
				getText().isEmpty()) {

				// if you remove textField.getText.isEmpty() when text field is empty it suggests all options
				// so you can choose
				autoCompletePopup.hide();
			}
			else {
				Bounds caretBounds = getCaretBounds().get();
				autoCompletePopup.show(
					this, caretBounds.getMaxX(), caretBounds.getMaxY());
			}
		});

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

		while (matcher.find()) {
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

	public void initScene(Scene scene) {
		scene.getStylesheets().add(
			getClass().getResource("sql-keywords.css").toExternalForm());
	}

	public void initAutoCompletePopup(
		String oldTable, String newTable, List<String> oldColumns,
		List<String> newColumns) {

		ObservableList<String> suggestions = autoCompletePopup.getSuggestions();

		suggestions.remove(oldTable);
		suggestions.add(newTable);

		suggestions.removeAll(oldColumns);
		suggestions.addAll(newColumns);
	}

    public String getQuery() {
	    // TODO Check if selected text
	    return getText();
    }

}
