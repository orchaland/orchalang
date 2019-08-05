package orcha.lang.configuration

class OutputFileAdapter(val directory: String, val filename: String, val appendNewLine: Boolean = true, val createDirectory: Boolean = true, val writingMode: WritingMode = WritingMode.APPEND) : ConfigurableProperties() {

    val adapter : Adapter = Adapter.File

    enum class WritingMode {
        REPLACE,
        APPEND
    }

    init {
        super.properties.add("directory")
        super.properties.add("filename")
    }

}
