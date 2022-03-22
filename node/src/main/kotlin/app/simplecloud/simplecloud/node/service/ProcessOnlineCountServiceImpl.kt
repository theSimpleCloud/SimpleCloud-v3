package app.simplecloud.simplecloud.node.service

import app.simplecloud.simplecloud.api.impl.service.AbstractProcessOnlineCountService
import app.simplecloud.simplecloud.node.task.NodeOnlineProcessesChecker
import com.google.inject.Inject
import com.google.inject.Singleton

/**
 * Date: 03.02.22
 * Time: 21:52
 * @author Frederick Baier
 *
 */
@Singleton
class ProcessOnlineCountServiceImpl @Inject constructor(
    private val onlineProcessesChecker: NodeOnlineProcessesChecker
) : AbstractProcessOnlineCountService() {

    override fun checkProcessOnlineCount() {
        this.onlineProcessesChecker.run()
    }
}