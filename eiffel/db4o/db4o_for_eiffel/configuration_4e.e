indexing
	description: "Installation of special translators to make db4o work with Eiffel"
	author: "Carl Rosenberger"
	date: "$Date: 2005/08/14 14:57:34 $"
	revision: "$Revision: 1.1 $"

class
	CONFIGURATION_4E

create    
	make

feature -- Initialization

	make is
		do
			init
			install_translators
		end

feature -- Implementation
	
    j4o_class : CLASS_
    
    init is
    	do
    		create j4o_class.make (system_string_class.get_type)
    	end

   	install_translators is
	    local
	    	string_translator : STRING_TRANSLATOR_4E
   		do
			create string_translator.make(system_string_class)
			translate("any string", string_translator)
   		end
    
	system_string_class : CLASS_ is
	    local
	    	my_system_string : SYSTEM_STRING
		do
			create my_system_string.make_from_c_and_count ('o', 1)
			Result := class_for_object (my_system_string)
		end


	class_for_object(obj: SYSTEM_OBJECT): CLASS_ is
		do
			Result := j4o_class.get_class_for_object (obj)
		end
		
	translate(obj: SYSTEM_OBJECT; translator: OBJECT_TRANSLATOR) is
		do
			{DB_4O}.configure.object_class (class_for_object (obj)).translate(translator)
		end
		
end
