package com.example.lanmessenger

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var tvLocalIp: TextView
    private lateinit var etTargetIp: TextInputEditText
    private lateinit var etMessage: TextInputEditText
    private lateinit var tvLog: TextView

    private val executor = Executors.newSingleThreadExecutor()
    private var receiverSocket: DatagramSocket? = null

    private val localPort = 50001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLocalIp = findViewById(R.id.tvLocalIp)
        etTargetIp = findViewById(R.id.etTargetIp)
        etMessage = findViewById(R.id.etMessage)
        tvLog = findViewById(R.id.tvLog)
        val btnSend: MaterialButton = findViewById(R.id.btnSend)

        val localIp = getLocalIpv4Address() ?: "未获取到"
        tvLocalIp.text = "本机 IP: $localIp"

        btnSend.setOnClickListener {
            val targetIp = etTargetIp.text?.toString()?.trim().orEmpty()
            val message = etMessage.text?.toString()?.trim().orEmpty()

            if (targetIp.isBlank() || message.isBlank()) {
                Toast.makeText(this, "请填写对方 IP 和消息", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendUdpMessage(targetIp, message, localIp)
        }

        startReceiver(localIp)
    }

    override fun onDestroy() {
        super.onDestroy()
        receiverSocket?.close()
        executor.shutdownNow()
    }

    private fun sendUdpMessage(targetIp: String, message: String, localIp: String) {
        executor.execute {
            try {
                DatagramSocket().use { socket ->
                    val payload = "from=$localIp;msg=$message"
                    val bytes = payload.toByteArray(Charsets.UTF_8)
                    val packet = DatagramPacket(bytes, bytes.size, InetAddress.getByName(targetIp), localPort)
                    socket.send(packet)
                }
                runOnUiThread {
                    appendLog("我 -> $targetIp: $message")
                    etMessage.setText("")
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "发送失败: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startReceiver(localIp: String) {
        executor.execute {
            try {
                receiverSocket = DatagramSocket(localPort)
                val buffer = ByteArray(1024)

                while (!Thread.currentThread().isInterrupted) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    receiverSocket?.receive(packet) ?: break

                    val text = String(packet.data, 0, packet.length, Charsets.UTF_8)
                    val senderIp = packet.address.hostAddress ?: "未知"

                    runOnUiThread {
                        appendLog("$senderIp -> 我: $text")
                        val alertTriggered = text.contains("from=$localIp") || senderIp == localIp
                        if (alertTriggered) {
                            playAlertTone()
                            Toast.makeText(this, "收到包含本机 IP 的消息", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (_: Exception) {
                // socket关闭时会抛异常，忽略
            }
        }
    }

    private fun playAlertTone() {
        ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100).startTone(ToneGenerator.TONE_PROP_BEEP, 300)
    }

    private fun appendLog(text: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        tvLog.append("\n[$time] $text")
    }

    private fun getLocalIpv4Address(): String? {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return null
            interfaces.toList().forEach { netInterface ->
                netInterface.inetAddresses.toList().forEach { address ->
                    if (!address.isLoopbackAddress && address.hostAddress?.contains(':') == false) {
                        return address.hostAddress
                    }
                }
            }
            null
        } catch (_: Exception) {
            null
        }
    }
}
