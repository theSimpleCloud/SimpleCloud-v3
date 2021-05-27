/*
 * MIT License
 *
 * Copyright (C) 2021 The SimpleCloud authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.dependencyloader

import eu.thesimplecloud.dependencyloader.local.LocalFileRepository
import eu.thesimplecloud.dependencyloader.util.URLUtil
import eu.thesimplecloud.jsonlib.JsonLib
import org.eclipse.aether.artifact.DefaultArtifact
import java.io.FileNotFoundException

/**
 * Created by IntelliJ IDEA.
 * Date: 05.05.2021
 * Time: 18:52
 * @author Frederick Baier
 */
class DependencyDownloader(
    private val localRepository: LocalFileRepository,
    private val dependency: Dependency,
    private val repositories: List<Repository>
) {

    fun download() {
        checkAlreadyExists()
        val validRepository = downloadFromRepositories()
            ?: throw NoSuchElementException("Dependency ${this.dependency.getName()} not found in any of the specified repositories $repositories")

        val subDependencies = getSubDependenciesOfDependency(validRepository)
        createInfoFile(subDependencies)
        downloadSubDependencies(subDependencies)
    }

    private fun downloadSubDependencies(subDependencies: List<Dependency>) {
        subDependencies.forEach {
            downloadSubDependency(it)
        }
    }

    private fun downloadSubDependency(dependency: Dependency) {
        try {
            DependencyDownloader(this.localRepository, dependency, this.repositories)
                .download()
        } catch (ignored: FileAlreadyDownloadedException)  {

        }

    }

    private fun createInfoFile(subDependencies: List<Dependency>) {
        val infoFile = this.localRepository.getInfoFile(this.dependency)
        JsonLib.fromObject(subDependencies.toTypedArray()).saveAsFile(infoFile)
    }

    private fun getSubDependenciesOfDependency(repository: Repository): List<Dependency> {
        val artifact =
            DefaultArtifact("${this.dependency.groupId}:${this.dependency.artifactId}:${this.dependency.version}")
        val dependencyResolver = SubDependencyResolver(repository.baseURL, artifact)
        val subDependencies = dependencyResolver.collectDependencies().map { it.artifact }
        return subDependencies.map { Dependency(it.groupId, it.artifactId, it.version) }
    }

    private fun checkAlreadyExists() {
        val file = localRepository.getFileForJar(dependency)
        if (file.exists())
            throw FileAlreadyDownloadedException()
    }

    //Return the valid repository
    private fun downloadFromRepositories(): Repository? {
        return repositories.firstOrNull { tryDownloadFromRepository(it) }
    }

    private fun tryDownloadFromRepository(repository: Repository): Boolean {
        val urlToJar = repository.constructURLToJar(this.dependency)
        val jarFile = this.localRepository.getFileForJar(this.dependency)
        return try {
            URLUtil.downloadFile(urlToJar, jarFile)
            true
        } catch (ex: FileNotFoundException) {
            false
        }
    }

    class FileAlreadyDownloadedException : Exception()


}