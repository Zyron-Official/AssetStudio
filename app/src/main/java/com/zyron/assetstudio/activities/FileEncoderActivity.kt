package com.zyron.assetstudio.activities;

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.StringWriter
import com.zyron.assetstudio.R
import com.google.android.material.card.MaterialCardView
import com.zyron.assetstudio.databinding.ActivityFileEncoderBinding

class FileEncoderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFileEncoderBinding
    private lateinit var fileUri: Uri
    private lateinit var convertButton: Button
    private lateinit var previewTextView: TextView
    private lateinit var saveButton: Button

    private val PICK_SVG_FILE_REQUEST_CODE = 1
    private val CREATE_XML_FILE_REQUEST_CODE = 2

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileEncoderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        convertButton = findViewById(R.id.convertButton)
        previewTextView = findViewById(R.id.previewTextView)
        saveButton = findViewById(R.id.saveButton)

        val pickFile = findViewById<MaterialCardView>(R.id.imageIconUploader)
        pickFile.setOnClickListener {
            openFilePicker()
        }

        convertButton.setOnClickListener {
            convertSvgToXml()
        }

        saveButton.setOnClickListener {
            saveXmlToFile()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/svg+xml"
        }
        startActivityForResult(intent, PICK_SVG_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_SVG_FILE_REQUEST_CODE -> {
                    fileUri = data?.data!! // You can display the selected file name or path here
                }
                CREATE_XML_FILE_REQUEST_CODE -> {
                    val uri: Uri? = data?.data
                    uri?.let {
                        contentResolver.openOutputStream(it)?.use { outputStream ->
                            outputStream.write(previewTextView.text.toString().toByteArray())
                            previewTextView.text = "XML saved to: ${it.path}"
                        }
                    }
                }
            }
        }
    }

    private fun convertSvgToXml() {
        try {
            val xmlString = convertSvgToXml(fileUri)
            previewTextView.text = xmlString
        } catch (e: Exception) {
            previewTextView.text = "Error converting SVG: ${e.message}"
            e.printStackTrace() // Print the stack trace for debugging
        }
    }

    private fun saveXmlToFile() {
        val xmlString = previewTextView.text.toString()

        // Check for MANAGE_EXTERNAL_STORAGE permission (Android 11 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !hasManageExternalStoragePermission()) {
            requestManageExternalStoragePermission()
            return // Exit the function, wait for the user to grant permission
        }

        try {
            val directoryPath = "/storage/emulated/0/Asset Studio/SVG to XML/"
            val directory = File(directoryPath)

            if (!directory.exists()) {
                // Create parent directories first, then the target directory
                if (directory.parentFile?.mkdirs() == true && directory.mkdirs()) {
                    var file: File? = null
                    var fileCounter = 1
                    val baseFileName = "ic_vector"

                    // Find a unique filename
                    do {
                        val fileName = "${baseFileName}_${fileCounter}.xml"
                        file = File(directory, fileName)
                        fileCounter++
                    } while (file?.exists() == true)

                    // Check if a unique filename was found
                    if (file != null) {
                        val fileOutputStream = FileOutputStream(file)
                        fileOutputStream.write(xmlString.toByteArray())
                        fileOutputStream.close()
                        previewTextView.text = "XML saved to: ${file.absolutePath}"
                    } else {
                        previewTextView.text = "Error: Could not find a unique filename to save the file."
                    }
                } else {
                    previewTextView.text = "Error: Could not create directory $directoryPath"
                    return
                }
            } else {
                // Directory already exists
                var file: File? = null
                var fileCounter = 1
                val baseFileName = "ic_vector"

                // Find a unique filename
                do {
                    val fileName = "${baseFileName}_${fileCounter}.xml"
                    file = File(directory, fileName)
                    fileCounter++
                } while (file?.exists() == true)

                // Check if a unique filename was found
                if (file != null) {
                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(xmlString.toByteArray())
                    fileOutputStream.close()
                    previewTextView.text = "XML saved to: ${file.absolutePath}"
                } else {
                    previewTextView.text = "Error: Could not find a unique filename to save the file."
                }
            }

        } catch (e: Exception) {
            previewTextView.text = "Error saving XML: ${e.message}"
            e.printStackTrace()
        }
    }

    // Helper functions for permission handling (Android 11 and above)
    @RequiresApi(Build.VERSION_CODES.R)
    private fun hasManageExternalStoragePermission(): Boolean {
        return Environment.isExternalStorageManager()
    }

    private fun requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + packageName)
                startActivity(intent)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
            }
        } else {
            // For older Android versions, you don't need to request this permission
        }
    }

    @Throws(Exception::class)
    private fun convertSvgToXml(svgFileUri: Uri): String {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()

        contentResolver.openInputStream(svgFileUri)?.use { inputStream ->
            parser.setInput(InputStreamReader(inputStream, "UTF-8"))

            val serializer = XmlPullParserFactory.newInstance().newSerializer()
            val writer = StringWriter()
            serializer.setOutput(writer)

            var eventType = parser.eventType
            var inVectorTag = false

            serializer.startDocument("UTF-8", null)

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (parser.name == "svg") {
                            inVectorTag = true

                            // Start the <vector> tag (no need to set the prefix manually)
                            serializer.startTag(null, "vector")
                            serializer.attribute(null, "xmlns:android", "http://schemas.android.com/apk/res/android")
                            // Add attributes from <svg> tag to <vector>
                            for (i in 0 until parser.attributeCount) {
                                val attrName = parser.getAttributeName(i)
                                val attrValue = parser.getAttributeValue(i)
                                when (attrName) {
                                    "width" -> serializer.attribute(null, "android:width", parser.getAttributeValue(i))
                                    "height" -> serializer.attribute(null, "android:height", parser.getAttributeValue(i))
                                    "viewBox" -> {
                                        val viewBoxValues = parser.getAttributeValue(i).split(" ")
                                        if (viewBoxValues.size == 4) {
                                            serializer.attribute(null, "android:viewportWidth", viewBoxValues[2])
                                            serializer.attribute(null, "android:viewportHeight", viewBoxValues[3])
                                        }
                                    }
                                    // Add other relevant attributes here
                                    "opacity" -> serializer.attribute(null, "android:alpha", parser.getAttributeValue(i))
                                }
                            }

                            // Add the attributes with default values (even if not in SVG)
                            serializer.attribute(null, "android:tint", "?android:colorControlNormal") // Default: red tint
                            serializer.attribute(null, "android:tintMode", "src_in") // Default tint mode
                            serializer.attribute(null, "android:autoMirrored", "false") // Default: not mirrored
                        } else if (inVectorTag) {
                            when (parser.name) {
                                "path" -> {
                                    serializer.startTag(null, "path")
                                    for (i in 0 until parser.attributeCount) {
                                        when (parser.getAttributeName(i)) {
                                            "d" -> serializer.attribute(null, "android:pathData", parser.getAttributeValue(i))
                                            "fill" -> serializer.attribute(null, "android:fillColor", parser.getAttributeValue(i))
                                            "stroke" -> serializer.attribute(null, "android:strokeColor", parser.getAttributeValue(i))
                                            "stroke-width" -> serializer.attribute(null, "android:strokeWidth", parser.getAttributeValue(i))
                                            // Add other supported <path> attributes
                                        }
                                    }
                                    serializer.endTag(null, "path")
                                }
                                // ... (Handling for other tags: <group>, etc.) ...
                            }
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        if (parser.name == "svg") {
                            inVectorTag = false
                            serializer.endTag(null, "vector")
                        } else if (inVectorTag) {
                            // Handle closing tags for elements within <vector> if needed
                        }
                    }
                    // No need to handle TEXT events explicitly here
                }
                eventType = parser.next()
            }

            serializer.endDocument()
            return writer.toString()
        }

        return ""
    }
}