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

package eu.thesimplecloud.container.local

import eu.thesimplecloud.simplecloud.container.FileImageInclusion
import eu.thesimplecloud.simplecloud.container.local.LocalImage
import eu.thesimplecloud.simplecloud.container.local.LocalImageFactory
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * Date: 18.04.2021
 * Time: 10:11
 * @author Frederick Baier
 */
class LocalImageBuildTest {

    private val testDir = File("imageBuildTest/")
    private val templatesDir = File("imageBuildTest/templates/")
    private val extraDir = File("imageBuildTest/extras/")

    private val factory = LocalImageFactory()

    private fun createTestTemplateFiles(): File {
        val testTemplateDir = File(templatesDir, "Test")
        testTemplateDir.mkdirs()

        val testFile1 = File(testTemplateDir, "Test1.txt")
        val testFile2 = File(testTemplateDir, "Test2.txt")
        testFile1.createNewFile()
        testFile2.createNewFile()
        return testTemplateDir
    }

    private fun createTest2TemplateFiles(): File {
        val testTemplateDir = File(templatesDir, "Test2")
        testTemplateDir.mkdirs()

        val testFile1 = File(testTemplateDir, "Test6.txt")
        val testFile2 = File(testTemplateDir, "Test7.txt")
        testFile1.createNewFile()
        testFile2.createNewFile()
        return testTemplateDir
    }

    private fun createExtraFile(): File {
        val extraFile = File(extraDir, "extra.txt")
        extraDir.mkdirs()
        extraFile.createNewFile()
        return extraFile
    }


    @Test
    fun buildImageFromOneDirectory() {
        val templateDir = createTestTemplateFiles()
        val image = factory.create("Test", listOf(templateDir))
        image.build().join()
        Assert.assertTrue(File(image.imageDir, "Test1.txt").exists())
        Assert.assertTrue(File(image.imageDir, "Test2.txt").exists())
        Assert.assertFalse(File(image.imageDir, "Test6.txt").exists())
        Assert.assertFalse(File(image.imageDir, "Test7.txt").exists())
    }

    @Test
    fun buildImageFromTwoDirectories() {
        val templateDir = createTestTemplateFiles()
        val template2Dir = createTest2TemplateFiles()
        val image = factory.create("Test", listOf(templateDir, template2Dir))
        image.build().join()
        Assert.assertTrue(File(image.imageDir, "Test1.txt").exists())
        Assert.assertTrue(File(image.imageDir, "Test2.txt").exists())
        Assert.assertTrue(File(image.imageDir, "Test6.txt").exists())
        Assert.assertTrue(File(image.imageDir, "Test7.txt").exists())
    }

    @Test
    fun buildImageFromTwoDirectoriesAndExtra() {
        val templateDir = createTestTemplateFiles()
        val template2Dir = createTest2TemplateFiles()
        val extraFile = createExtraFile()
        val inclusion = FileImageInclusion(extraFile, "plugins/extra2.txt")
        val image = factory.create("Test", listOf(templateDir, template2Dir), listOf(inclusion))
        image.build().join()
        println(image.imageDir.absolutePath)
        Assert.assertTrue(File(image.imageDir, "Test1.txt").exists())
        Assert.assertTrue(File(image.imageDir, "Test2.txt").exists())
        Assert.assertTrue(File(image.imageDir, "Test6.txt").exists())
        Assert.assertTrue(File(image.imageDir, "Test7.txt").exists())
        Assert.assertTrue(File(image.imageDir, "plugins/extra2.txt").exists())
    }

    @After
    fun tearDown() {
        val imagesDir = File(LocalImage.IMAGES_DIR)
        FileUtils.deleteDirectory(imagesDir)
        FileUtils.deleteDirectory(testDir)
    }



}