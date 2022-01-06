package app.simplecloud.simplecloud.application.exception

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 27.03.2021
 * Time: 13:47
 */
class InvalidApplicationEntryPointFileException(
    missingParameter: String
) : RuntimeException("The application file is missing the parameter $missingParameter")