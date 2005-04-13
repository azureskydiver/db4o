/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.browser.query.model;

import com.db4o.query.Constraint;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;

public class FieldConstraint {
    
    public final ReflectField field;
    public RelationalOperator relation;
    public Object value;
    
    public FieldConstraint(ReflectField field, QueryBuilderModel model) {
        this.field = field;
        this.relation = RelationalOperator.EQUALS;
        final ReflectClass fieldType = field.getType();
        if (fieldType.isSecondClass()) {
            this.value = null;
        } else {
            if (!model.typeInQuery(fieldType)) {
                model.addTypeToQuery(fieldType);
                this.value = new QueryPrototypeInstance(fieldType, model);
            }
        }
    }

    public void apply(Query query) {
        if (field.getType().isSecondClass()) {
            Constraint constraint=query.descend(field.getName()).constrain(value);
            relation.apply(constraint);
        } else {
            // It's an object reference, we need to traverse it to its
            // QueryPrototypeInstance and apply those constraints too
            QueryPrototypeInstance referencePrototype = (QueryPrototypeInstance) value;
            referencePrototype.addUserConstraints(query.descend(field.getName()));
        }
    }

    public QueryPrototypeInstance valueProto() {
        return (QueryPrototypeInstance) value;
    }

}

