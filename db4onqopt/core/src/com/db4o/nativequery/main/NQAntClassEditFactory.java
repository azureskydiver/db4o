package com.db4o.nativequery.main;

import com.db4o.instrumentation.core.*;

public class NQAntClassEditFactory implements ClassEditFactory {

	public BloatClassEdit createEdit() {
		return new TranslateNQToSODAEdit();
	}

}
