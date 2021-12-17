package orcha.lang.configuration

/**
 * @property directory the directory from which files are read, Example: "C:/Users/BenC/data", Use / on any system.
 * @property filenamePattern the file name or a file name pattern like *.json.
 */
data class InputFileAdapter(var directory: String, var filenamePattern: String, val adapter: Adapter = Adapter.File) : ConfigurableProperties() {



}
