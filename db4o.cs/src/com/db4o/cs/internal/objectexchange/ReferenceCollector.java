package com.db4o.cs.internal.objectexchange;

import com.db4o.foundation.*;

public interface ReferenceCollector {

	Iterator4 referencesFrom(int id);

}
