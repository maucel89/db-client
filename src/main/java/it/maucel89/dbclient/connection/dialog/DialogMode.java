package it.maucel89.dbclient.connection.dialog;

/**
 * @author Mauro Celani
 */
public enum DialogMode {

	ADD("Aggiungi Connessione ", "Aggiungi"),
	EDIT("Modifica Connessione ", "Modifica");

	private String title;
	private String buttonLabel;

	DialogMode(String title, String buttonLabel) {
		this.title = title;
		this.buttonLabel = buttonLabel;
	}

	public String getTitle() {
		return title;
	}

    public String getButtonLabel() {
	    return buttonLabel;
    }
}
