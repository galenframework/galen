
this.doubleLoadCheck  = this.doubleLoadCheck + 1;
if (this.doubleLoadCheck > 1) {
	//Doing this in order to check that Galen javascript runner does not load same file twice
	throw new Error("Some error");
}

var varFromImportedScript = "text from imported script";