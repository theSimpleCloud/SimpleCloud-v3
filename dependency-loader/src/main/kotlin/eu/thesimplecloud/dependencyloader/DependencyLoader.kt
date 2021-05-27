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
import eu.thesimplecloud.jsonlib.JsonLib
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 07.05.2021
 * Time: 16:42
 * @author Frederick Baier
 */
class DependencyLoader(
    private val localRepo: LocalFileRepository,
    private val repositories: List<Repository>,
    private val dependencies: List<Dependency>
) {

    private val foundDependencies = ArrayList<Dependency>()

    fun findAllDependenciesAsFiles(): List<File> {
        val allDependencies = findAllDependencies()
        return allDependencies.map { localRepo.getFileForJar(it) }
    }

    fun findAllDependencies(): List<Dependency> {
        this.dependencies.forEach { findSubDependenciesIfNotFound(it) }
        return this.foundDependencies
    }

    private fun findSubDependenciesIfNotFound(dependency: Dependency) {
        if (this.foundDependencies.contains(dependency))
            return
        this.foundDependencies.add(dependency)
        findSubDependenciesUnsafe(dependency)
    }

    private fun findSubDependenciesUnsafe(dependency: Dependency) {
        downloadDependencyAndSubDependencoesIfNecessary(dependency)
        val subDependencies = loadSubDependenciesOfDependencyFromFile(dependency)
        subDependencies.forEach { findSubDependenciesIfNotFound(it) }
    }

    private fun loadSubDependenciesOfDependencyFromFile(dependency: Dependency): List<Dependency> {
        val infoFile = localRepo.getInfoFile(dependency)
        return JsonLib.fromJsonFile(infoFile)!!.getObject(Array<Dependency>::class.java).toList()
    }

    private fun downloadDependencyAndSubDependencoesIfNecessary(dependency: Dependency) {
        try {
            DependencyDownloader(localRepo, dependency, repositories)
                .download()
        } catch (ex: DependencyDownloader.FileAlreadyDownloadedException) {
            //dependency is already downloaded
        }
    }

}