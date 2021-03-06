package io.ankburov.videocontentserver.service.cmd

import io.ankburov.videocontentserver.model.MpegDashDir
import io.ankburov.videocontentserver.model.QualityEnum
import io.ankburov.videocontentserver.utils.absolutePath
import io.ankburov.videocontentserver.utils.execute
import io.ankburov.videocontentserver.utils.getErrorCause
import io.ankburov.videocontentserver.utils.usingByteOutputStreams
import org.apache.commons.exec.CommandLine
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Component
class Mp4BoxCommand {

    fun execute(convertedFiles: List<Pair<QualityEnum, Path>>): MpegDashDir {
        require(convertedFiles.isNotEmpty()) { "converted mp4 file list cannot be empty" }

        val mpegDashDir = Files.createTempDirectory("split-mp4-to-mpeg-dash-fragments")
        val mpegDashDirPath = mpegDashDir.absolutePath()

        convertedFiles.forEach { (quality, file) -> Files.createDirectories(mpegDashDir.resolve(quality.code)) }

        val cmdLine = CommandLine("MP4Box")
        cmdLine.addArgument("-dash")
        cmdLine.addArgument("8000")
        cmdLine.addArgument("-frag")
        cmdLine.addArgument("8000")
        cmdLine.addArgument("-rap")
        cmdLine.addArgument("-url-template")
        cmdLine.addArgument("-segment-name")
        cmdLine.addArgument("\$RepresentationID\$/segment_", true)
        cmdLine.addArgument("-out")
        val mpdName = "mpeg-dash.mpd"
        cmdLine.addArgument("$mpegDashDirPath/$mpdName", true)

        convertedFiles.map { (quality, file) -> "${file.toAbsolutePath()}#video:id=${quality.code}" }
                .forEach { cmdLine.addArgument(it, true) }

        cmdLine.addArgument("${convertedFiles[0].second.toAbsolutePath()}#audio:id=audio", true)

        usingByteOutputStreams { stdOut, stdErr ->
            val exitValue = cmdLine.execute(stdOut, stdErr)

            return when (exitValue) {
                0  -> MpegDashDir(mpdName, mpegDashDir)
                else -> throw IOException(getErrorCause(stdErr))
            }
        }
    }
}