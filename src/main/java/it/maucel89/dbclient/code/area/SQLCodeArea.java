package it.maucel89.dbclient.code.area;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

/**
 * @author Mauro Celani
 */
public class SQLCodeArea extends CodeArea {

	public SQLCodeArea() {

		super();

		setParagraphGraphicFactory(
			LineNumberFactory.get(this));
	}

	public void initCode(String sql) {
		replaceText(0, 0, sql);
	}
}
