package com.db4o.nativequery.main;

import com.db4o.instrumentation.core.*;

public class NQClassEditFactory implements ClassEditFactory {

	public BloatClassEdit createEdit() {
		return new TranslateNQToSODAEdit();
	}

}
