package solutions.mk.mobile.config

import org.koin.core.annotation.Single

// todo - the best way - read file from disk but now it's OK.
@Single class ApplicationConfig {
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