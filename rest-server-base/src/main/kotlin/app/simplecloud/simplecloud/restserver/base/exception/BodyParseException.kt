package app.simplecloud.simplecloud.restserver.base.exception


/**
 * Date: 14.03.22
 * Time: 16:31
 * @author Frederick Baier
 *
 */
class BodyParseException(
    className: String,
    override val cause: Throwable?
) : HttpException(400, "Failed to parse body to class '${className}'", cause)