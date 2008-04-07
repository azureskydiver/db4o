package com.db4o.bench;

import com.db4o.test.crud.CrudBenchMark;

public class CrudBenchmarkTargetApplication implements TargetApplication {

	public void run(String logFilePath, String[] args) {
		CrudBenchMark.main(new String[]{"STORABLEPAGE"});
	}

}
