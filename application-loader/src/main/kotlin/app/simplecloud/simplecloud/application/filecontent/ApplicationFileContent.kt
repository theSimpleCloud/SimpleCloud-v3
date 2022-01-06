package app.simplecloud.simplecloud.application.filecontent

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 26.03.2021
 * Time: 21:20
 */
interface ApplicationFileContent {

    /**
     * Returns the name of the application
     */
    fun getName(): String

    /**
     * Returns the name of the author
     */
    fun getAuthor(): String

    /**
     * Returns the version of the application
     */
    fun getVersion(): String

    /**
     * Returns the name of the class to be loaded
     */
    fun getClassNameToLoad(): String

}