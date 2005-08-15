indexing
	description: "db4o query wrapper to handle string and field name translation"
	author: "Carl Rosenberger"
	date: "$Date: 2005/08/15 12:00:33 $"
	revision: "$Revision: 1.2 $"

class
	QUERY_4E

create
	make,from_query

feature -- Initialization
	
	query : QUERY
	executed : BOOLEAN

	make (oc : OBJECT_CONTAINER) is
		do
			from_query(oc.query)
		end
		
	from_query (q : QUERY) is
		do
			executed := false
			query := q
		end
	
feature	-- Use

	order_descending: QUERY_4E is
		require
			executed = false
		do
			query := query.order_ascending
			Result := Current
		end

	order_ascending: QUERY_4E is
		require
			executed = false
		do
			query := query.order_ascending
			Result := Current
		end

	execute: OBJECT_SET is
		require
			executed = false
		do
			executed := true
			Result := query.execute
		end

	descend (field_name: STRING): QUERY_4E is
		require
			executed = false
		local
			sub_node : QUERY
			sub_node_wrapper : QUERY_4E
		do
			field_name.prepend ("$$")
			sub_node := query.descend(field_name)
			create sub_node_wrapper.from_query(sub_node)
			Result := sub_node_wrapper
		end
		

	constraints: CONSTRAINTS is
		require
			executed = false
		do
			Result := query.constraints
		end
		
	
	constrain (constraint: SYSTEM_OBJECT) : CONSTRAINT is
		require
			executed = false
		do
			Result := query.constrain (constraint)
		end

end
