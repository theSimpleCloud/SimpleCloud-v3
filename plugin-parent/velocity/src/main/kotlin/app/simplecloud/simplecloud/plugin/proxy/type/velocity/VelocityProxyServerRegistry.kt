package app.simplecloud.simplecloud.plugin.proxy.type.velocity

import app.simplecloud.simplecloud.api.process.CloudProcess
import app.simplecloud.simplecloud.api.template.ProcessTemplateType
import app.simplecloud.simplecloud.plugin.proxy.ProxyServerRegistry
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.server.ServerInfo
import java.net.InetSocketAddress
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: Philipp.Eistrach
 * Date: 01.07.22
 * Time: 19:12
 */
class VelocityProxyServerRegistry(
    private val proxyServer: ProxyServer,
) : ProxyServerRegistry {

    override fun registerProcess(cloudProcess: CloudProcess) {
        if (this.proxyServer.getServer(cloudProcess.getName()).isPresent) {
            throw IllegalArgumentException("Service is already registered!")
        }

        if (cloudProcess.getProcessType() == ProcessTemplateType.PROXY)
            return

        val address = cloudProcess.getAddress()
        val socketAddress = InetSocketAddress(address.host, address.port)
        registerServer(cloudProcess.getName(), cloudProcess.getUniqueId(), socketAddress)
        println("Registered process ${cloudProcess.getName()}")
    }

    override fun registerServer(name: String, uniqueId: UUID, socketAddress: InetSocketAddress) {
        val info = ServerInfo(
            name,
            socketAddress
        )
        this.proxyServer.registerServer(info)
    }

    override fun unregisterServer(name: String) {
        this.proxyServer.getServer(name).ifPresent {
            this.proxyServer.unregisterServer(it.serverInfo)
            println("Unregistered process ${name}")
        }
    }

}