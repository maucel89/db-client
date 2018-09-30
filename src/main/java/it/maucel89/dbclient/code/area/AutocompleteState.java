package it.maucel89.dbclient.code.area;

import com.jfoenix.controls.JFXAutoCompletePopup;
import javafx.geometry.Bounds;
import javafx.stage.Popup;
import org.fxmisc.richtext.model.PlainTextChange;
import org.reactfx.EventStream;
import org.reactfx.StateMachine;
import org.reactfx.Subscription;

import java.util.Optional;

/**
 * @author Mauro Celani
 */
public class AutocompleteState {
/*
	private static final String EMPTY_STRING = "";
	public enum State { SHOW, HIDE, REFILTER, INSERTION }

	private String filterText;
	private State state;

	private AutocompleteState(String filterText, State state) {
		this.filterText = filterText;
		this.state = state;
	}

	public static AutocompleteState initial() {
		return new AutocompleteState(EMPTY_STRING, State.HIDE);
	}

	public void showBox() {
		state = State.SHOW;
	}

	public void hideBox() {
		state = State.HIDE;
		filterText = EMPTY_STRING;
	}

	public String getFilterText() {
		return filterText;
	}

	public State getState() {
		return state;
	}

	// Note: this method's implementation is probably wrong because a PlainTextChange
	//  can be merged with a previous one. Although I'm assuming a textChange in this case
	//  only inserts or deletes one letter at a time, this may not be true in its actual usage.
	public void updateFilter(PlainTextChange textChange) {
		// Note: textChange.isInsertion() is pseudo code to demonstrate idea
		if (textChange.isInsertion()) {
			filterText += textChange.getInserted();
		}
		else if (textChange.isDeletion()) {
			filterText -= textChange.getRemoved();
		}
		state = !filterText.isEmpty() ? State.REFILTER : State.HIDE;
	}

	public void insertSelected() {
		state = State.INSERTION;
	}

	public static void boh(
		SQLCodeArea area, AutocompleteState state, JFXAutoCompletePopup popup) {

//		EventStream<?> appearanceTriggers = ...;
//		EventStream<?> disappearanceTriggers = ...;
		EventStream<PlainTextChange> textModifications =
			area.plainTextChanges().conditionOn(popup.showingProperty());
//		EventStream<?> insertionTriggers = ...;
		EventStream<AutocompleteState> boxEvents = StateMachine
			.init(AutocompleteState.initial())
			.on(appearanceTriggers).transition(
				(appearance, state) -> state.showBox())
			.on(disappearanceTriggers).transition(
				(ignore, state) -> state.hideBox())
			.on(textModifications).transition(
				(textChange, state) -> state.updateFilter(textChange))
			.on(insertionTriggers).transition(
				(ignore, state) -> state.insertSelected())
			.toEventStream();

		Subscription sub = boxEvents.subscribe(state -> {
			switch (state.getState()) {
				case SHOW:
					// show popup at caret bounds
					Optional<Bounds> caretBounds = area.getCaretBounds();
					if (caretBounds.isPresent()) {
						popup.show(
							area, caretBounds.get().getMaxX(),
							caretBounds.get().getMaxY());
					}
					break;

				case HIDE:
					popup.hide();
					break;

				case REFILTER:
					// assumes popup is already displayed
					autocompleteBox.refilter(state.getFilterText());
					// move autocompleteBox to new caret location
					popup.show();

				default: case INSERTION:
					area.insertText(
						state.getStartPosition(),
						autocompleteBox.getSelectedItem());
					popup.hide();
			}
		});
	}
*/
}
