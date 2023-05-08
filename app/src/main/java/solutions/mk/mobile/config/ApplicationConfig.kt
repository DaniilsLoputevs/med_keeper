package solutions.mk.mobile.config

// todo - в идеале этот конфиг читается с файла в Bean + Inject, а пока так.
class ApplicationConfig {
    /**
     * description: ...
     * pattern: "${app}/files${value}"
     * example: "$solution.mk.mobile/files/records"
     * default: "/records"
     * */
    val relativePathToStoreRecordFiles: String = "/records"

    /**
     * description: size of byte array witch use for copy files into application while Import/Adding file.
     * default: "8 kb"
     */
    val copyFileByteBufferSize = 1024 * 8
}