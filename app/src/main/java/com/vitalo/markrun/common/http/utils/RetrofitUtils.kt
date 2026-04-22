package com.vitalo.markrun.common.http.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit

object RetrofitUtils {

    private const val BASE_URL = "http://127.0.0.1"

    fun getInstance(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun downloadFileWithRetrofit(
        urlString: String,
        destFolder: String,
        onDownloadComplete: (File?) -> Unit
    ) {
        val retrofit = getInstance()
        val service = retrofit.create(FileDownloadService::class.java)

        if (!createFolderIfNotExist(destFolder)) {
            onDownloadComplete(null)
            return
        }

        val fileName = urlString.substringBefore("?").substringAfterLast("/")
        val destFile = File(destFolder, fileName)

        if (destFile.exists()) {
            onDownloadComplete(destFile)
            return
        }

        val call = service.downloadFile(urlString)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        response.body()?.let { responseBody ->
                            saveFileToDisk(responseBody, destFile)?.let {
                                onDownloadComplete(it)
                            } ?: run {
                                onDownloadComplete(null)
                            }
                        } ?: run {
                            onDownloadComplete(null)
                        }
                    }
                } else {
                    onDownloadComplete(null)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onDownloadComplete(null)
            }
        })
    }

    private fun createFolderIfNotExist(folderPath: String): Boolean {
        val folder = File(folderPath)
        return if (!folder.exists() || !folder.isDirectory) {
            folder.mkdirs()
        } else {
            true
        }
    }

    private fun saveFileToDisk(body: ResponseBody, destFile: File): File? {
        return try {
            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                inputStream = body.byteStream()
                outputStream = FileOutputStream(destFile)

                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) break
                    outputStream.write(fileReader, 0, read)
                }

                outputStream.flush()
                destFile
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

interface FileDownloadService {
    @GET
    @Streaming
    fun downloadFile(@Url fileUrl: String): Call<ResponseBody>
}
