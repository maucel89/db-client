package it.maucel89.dbclient.connection.dialog;

/**
 * @author Mauro Celani
 */
public enum DialogMode {

	ADD("Aggiungi Connessione "),
	EDIT("Modifica Connessione ");

	private String title;

	DialogMode(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}
}
